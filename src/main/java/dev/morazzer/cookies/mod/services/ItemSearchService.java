package dev.morazzer.cookies.mod.services;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemCompound;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.items.sources.StorageItemSource;
import dev.morazzer.cookies.mod.data.profile.profile.IslandChestStorage;
import dev.morazzer.cookies.mod.events.ItemStackEvents;
import dev.morazzer.cookies.mod.render.WorldRender;
import dev.morazzer.cookies.mod.render.types.BlockHighlight;
import dev.morazzer.cookies.mod.render.types.Timed;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;

import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;

import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import java.util.function.Predicate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

/**
 * The service for easy interaction with the {@link IslandChestStorage}.
 */
public class ItemSearchService {

	static ScheduledFuture<?> schedule;
	@Nullable
	static ItemStack currentlySearched;
	static List<ItemStack> modifiedStacks = new CopyOnWriteArrayList<>();

	static {
		ItemStackEvents.EVENT.register(ItemSearchService::modify);
	}

	/**
	 * Performs whatever action is associated with the provided parameters.
	 *
	 * @param type The type of action.
	 * @param data The data related to the action.
	 * @param item The item which is subject of the action.
	 */
	public static void performAction(ItemCompound.CompoundType type, Object data, Item<?> item) {
		if (!LocationUtils.Island.PRIVATE_ISLAND.isActive() && type == ItemCompound.CompoundType.CHEST_POS) {
			CookiesUtils.sendFailedMessage(Text.literal("You need to be on your island to highlight chests!"));
			return;
		}
		setActiveStackWithColor(item.itemStack());
		final RepositoryItem repositoryItem = item.itemStack().get(CookiesDataComponentTypes.REPOSITORY_ITEM);
		final int color = getColor(repositoryItem);
		switch (type) {
			case CHEST_POS -> highlightChests(item, color);
			case STORAGE_PAGE -> openStorage((StorageItemSource.Context) data);
			case STORAGE -> sendCommand("storage");
			case SACKS, SACK_OF_SACKS -> sendCommand("sacks");
			case ACCESSORIES -> sendCommand("accessorybag");
			case POTION_BAG -> sendCommand("potionbag");
			case VAULT -> sendCommand("bank");
		}
	}

	/**
	 * Executes a command as the player.
	 *
	 * @param command The command to execute.
	 */
	public static void sendCommand(String command) {
		Optional.ofNullable(MinecraftClient.getInstance().player)
				.ifPresent(player -> player.networkHandler.sendCommand(command));
	}

	/**
	 * Gets the highlight color for the item.
	 *
	 * @param repositoryItem The item.
	 * @return The color, or {@link Constants#FAIL_COLOR} if no color was found.
	 */
	public static int getColor(RepositoryItem repositoryItem) {
		final int color;
		if (repositoryItem != null && repositoryItem.getTier() != null) {
			final Formatting formatting = repositoryItem.getTier().getFormatting();
			color = switch (formatting) {
				case GREEN -> Constants.SUCCESS_COLOR;
				case WHITE -> 0xFFEBEBEB;
				default -> Objects.requireNonNullElse(formatting.getColorValue(), Constants.FAIL_COLOR);
			};
		} else {
			color = Constants.FAIL_COLOR;
		}
		return 0xFF << 24 | color & 0xFFFFFF;
	}

	/**
	 * Performs the actions associated with the compound, if items are from the same source it will perform an item
	 * specific action.
	 *
	 * @param itemCompound The item compound.
	 */
	public static void performActions(ItemCompound itemCompound) {
		if (!LocationUtils.Island.PRIVATE_ISLAND.isActive() &&
			(itemCompound.type() == ItemCompound.CompoundType.MULTIPLE ||
			 itemCompound.type() == ItemCompound.CompoundType.CHEST)) {
			if (itemCompound.type() == ItemCompound.CompoundType.CHEST) {
				CookiesUtils.sendFailedMessage(Text.literal("You need to be on your island to highlight chests!"));
				return;
			}
			final Item<?>[] array = itemCompound.items()
					.stream()
					.filter(Predicate.not(item -> item.source() == ItemSources.CHESTS))
					.toArray(Item[]::new);
			if (array.length == 0) {
				return;
			}
			itemCompound = new ItemCompound(array[0]);
			for (int i = 1; i < array.length; i++) {
				itemCompound.add(array[i]);
			}
		}
		if (itemCompound.type() == ItemCompound.CompoundType.MULTIPLE ||
			itemCompound.type() == ItemCompound.CompoundType.CHEST) {

			highlightAll(itemCompound);
			return;
		}
		performAction(itemCompound.type(), itemCompound.data(), itemCompound.items().iterator().next());
	}

	/**
	 * Adds an item to the currently modified list.
	 */
	public static void add(ItemStack itemStack) {
		modifiedStacks.add(itemStack);
	}

	/**
	 * Opens the storage at the page of the provided data.
	 *
	 * @param data The data.
	 */
	private static void openStorage(StorageItemSource.Context data) {
		final String command = switch (data.location()) {
			case BACKPACK -> "bp";
			case ENDER_CHEST -> "ec";
		};
		sendCommand(command + " " + (data.page() + 1));
	}

	/**
	 * Sets the active item stacks and assigns it's background color.
	 *
	 * @param stack The stack to set.
	 * @return The color.
	 */
	public static int setActiveStackWithColor(ItemStack stack) {
		RepositoryItem repositoryItem = stack.get(CookiesDataComponentTypes.REPOSITORY_ITEM);
		final int color = getColor(repositoryItem);
		stack.set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR, color);
		setActiveStack(stack);
		return color;
	}

	/**
	 * Highlights all possible data sources associated with the item compound.
	 *
	 * @param itemCompound The item compound.
	 */
	public static void highlightAll(ItemCompound itemCompound) {
		int color = setActiveStackWithColor(itemCompound.itemStack());

		Set<BlockPos> addedHighlight = new HashSet<>();
		if (LocationUtils.Island.PRIVATE_ISLAND.isActive()) {
			for (Item<?> item : itemCompound.items()) {
				if (item.source() != ItemSources.CHESTS) {
					continue;
				}

				final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) item.data();
				if (!addedHighlight.contains(data.blockPos())) {
					highlightChest(data.blockPos(), color);
					addedHighlight.add(data.blockPos());
				}
				final Optional<BlockPos> blockPos = data.secondChest();
				if (blockPos.isPresent()) {
					if (!addedHighlight.contains(blockPos.get())) {
						highlightChest(blockPos.get(), color);
						addedHighlight.add(blockPos.get());
					}
				}
			}
		}
	}

	/**
	 * Highlights a chest in the world.
	 *
	 * @param item  The item to highlight.
	 * @param color The color the highlight should be in.
	 */
	public static void highlightChests(Item<?> item, int color) {
		if (item.source() != ItemSources.CHESTS) {
			return;
		}

		final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) item.data();
		highlightChest(data.blockPos(), color);
		data.secondChest().ifPresent(blockPos -> highlightChest(blockPos, color));
	}

	/**
	 * Highlights a chest at the specified position.
	 *
	 * @param blockPos The position.
	 * @param color    The color to highlight in.
	 */
	public static void highlightChest(BlockPos blockPos, int color) {
		if (blockPos == null) {
			return;
		}

		WorldRender.addRenderable(new Timed(new BlockHighlight(blockPos, color), 10, TimeUnit.SECONDS));
	}

	/**
	 * Sets the active item stack and initiates the removal of the highlight.
	 *
	 * @param stack The stack.
	 */
	private static void setActiveStack(ItemStack stack) {
		removeActiveStack();
		currentlySearched = stack;
		if (schedule != null) {
			schedule.cancel(false);
		}
		schedule = CookiesMod.getExecutorService().schedule(ItemSearchService::removeActiveStack, 10,
            TimeUnit.SECONDS);
	}

	/**
	 * Removes the currently active stack and clears all highlights from it.
	 */
	private static void removeActiveStack() {
		if (currentlySearched != null) {
			currentlySearched.remove(MiscDataComponentTypes.ITEM_SEARCH_MATCH_SAME);
		}
		currentlySearched = null;
		for (ItemStack modifiedStack : modifiedStacks) {
			if (modifiedStack.contains(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED)) {
				modifiedStack.remove(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
				final Integer remove = modifiedStack.remove(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED);
				if (remove != null && remove != 0) {
					modifiedStack.set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR, remove);
				}
			}
		}
		modifiedStacks.clear();
	}

	/**
	 * Whether the two items are the same.
	 * <br>
	 * This will be figured out by the following criteria. <br>
	 * 1. Skyblock ID <br>
	 * 2. Enchants <br>
	 * 3. Attributes <br>
	 * 4. Custom Name <br>
	 * 5. Modifier <br>
	 * <br>
	 * If one is not present on both, that criteria is considered a match. <br>
	 * If on is missing on either but present on the other, it is not considered a match and will return false.
	 *
	 * @param first  The first item stack to check.
	 * @param second The second item stack to check.
	 * @return Whether the two items are (more or less) the same.
	 */
	public static boolean isSame(ItemStack first, ItemStack second) {
		if (first == null || second == null) {
			return false;
		}
		if (first.getItem() != second.getItem()) {
			return false;
		}

		if (first.getItem() == Items.ENCHANTED_BOOK && isSame(first, second, CookiesDataComponentTypes.SKYBLOCK_ID)) {
			return true;
		}

		if (!isSame(first, second, CookiesDataComponentTypes.SKYBLOCK_ID)) {
			return false;
		}
		if (!isSame(first, second, CookiesDataComponentTypes.ENCHANTMENTS)) {
			return false;
		}
		if (!isSame(first, second, CookiesDataComponentTypes.ATTRIBUTES)) {
			return false;
		}
		if (!isSame(first, second, DataComponentTypes.CUSTOM_NAME)) {
			return false;
		}
		return isSame(first, second, CookiesDataComponentTypes.MODIFIER);
	}

	private static <T> boolean isSame(ItemStack first, ItemStack second, ComponentType<T> type) {
		T firstComponent = ItemUtils.getData(first, type);
		T secondComponent = ItemUtils.getData(second, type);

		if (firstComponent == null || secondComponent == null) {
			return firstComponent == null && secondComponent == null;
		}

		if (firstComponent instanceof Map<?, ?> firstMap && secondComponent instanceof Map<?, ?> secondMap) {
			for (Object firstMapKey : firstMap.keySet()) {
				if (!secondMap.containsKey(firstMapKey)) {
					return false;
				}
				if (!Objects.equals(secondMap.get(firstMapKey), firstMap.get(firstMapKey))) {
					return false;
				}
			}
		} else if (firstComponent instanceof Text firstText && secondComponent instanceof Text secondText &&
				   (firstText.getString() == null || !firstText.getString().equalsIgnoreCase(secondText.getString()))) {
			return false;
		}

		return Objects.deepEquals(firstComponent, secondComponent);
	}

	/**
	 * Modifies the provided stack to be highlighted in the same color as the currently searched item.
	 *
	 * @param itemStack The item stack to modify.
	 */
	public static void modify(ItemStack itemStack) {
		if (currentlySearched == null) {
			return;
		}
		if (itemStack.contains(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED)) {
			add(itemStack);
			return;
		}
		final IsSameResult isSame;
		if (currentlySearched.get(MiscDataComponentTypes.ITEM_SEARCH_MATCH_SAME) != null) {
			isSame = matchSame(itemStack, currentlySearched);
		} else {
			isSame = IsSameResult.wrapBoolean(isSame(itemStack, currentlySearched));
		}

		if (isSame != IsSameResult.NO) {
			final int saveColor;
			final Integer i = itemStack.get(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
			if (i == null) {
				saveColor = 0;
			} else {
				saveColor = i;
			}
			final Integer data = ItemUtils.getData(currentlySearched, CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
			if (data == null) {
				return;
			}
			final int color = data & 0xFFFFFF;
			itemStack.set(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED, saveColor);
			itemStack.set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR,
					(isSame == IsSameResult.YES ? 0xFF : 0x66) << 24 | color);
			modifiedStacks.add(itemStack);
		}
	}

	/**
	 * Appends the item source information and left click text to the tooltip.
	 *
	 * @param type         The type of compound.
	 * @param compoundData The data.
	 * @param tooltip      The tooltip to append to.
	 */
	public static void appendMultiTooltip(
			@Nullable ItemCompound.CompoundType type, Object compoundData, List<Text> tooltip) {
		switch (type) {
			case CHEST -> tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_ALL_CHEST)
					.formatted(Formatting.YELLOW));
			case CHEST_POS -> {
				tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_CHEST)
						.formatted(Formatting.YELLOW));
				final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) compoundData;
				tooltip.add(Text.translatable(TranslationKeys.BLOCK_XYZ,
						data.blockPos().getX(),
						data.blockPos().getY(),
						data.blockPos().getZ()).formatted(Formatting.DARK_GRAY));
			}
			case SACKS -> tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_SACKS)
					.formatted(Formatting.YELLOW));
			case STORAGE -> tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE)
					.formatted(Formatting.YELLOW));
			case STORAGE_PAGE -> {
				tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE_PAGE)
						.formatted(Formatting.YELLOW));
				final StorageItemSource.Context data = (StorageItemSource.Context) compoundData;
				if (data == null) {
					break;
				}
				tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE_PAGE_VALUE)
						.append(Text.literal(": " + (data.page() + 1)))
						.formatted(Formatting.DARK_GRAY));
				tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE_PAGE_STORAGE)
						.append(Text.literal(": ").append(Text.translatable(data.location().getTranslationKey())))
						.formatted(Formatting.DARK_GRAY));
			}
			case MULTIPLE -> {
				if (LocationUtils.Island.PRIVATE_ISLAND.isActive()) {
					tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT)
							.formatted(Formatting.YELLOW));
					break;
				}
				tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_NO_CHESTS)
						.formatted(Formatting.YELLOW));
			}
			case POTION_BAG ->
					tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_POTION_BAG)
							.formatted(Formatting.YELLOW));
			case SACK_OF_SACKS ->
					tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_SACK_OF_SACKS)
							.formatted(Formatting.YELLOW));
			case VAULT -> tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_VAULT)
					.formatted(Formatting.YELLOW));
			case ACCESSORIES ->
					tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_ACCESSORY_BAG)
							.formatted(Formatting.YELLOW));
			case null, default -> tooltip.add(Text.literal("An error occurred :c " + type).formatted(Formatting.RED));
		}
	}

	/**
	 * Whether the two items are the same, this will take multiple things into account like UUID, Timestamp, Lore and
	 * so on.
	 *
	 * @param itemStack         First item.
	 * @param currentlySearched Second item.
	 * @return Whether the two are the same.
	 */
	private static IsSameResult matchSame(ItemStack itemStack, ItemStack currentlySearched) {
		final UUID uuid = itemStack.get(CookiesDataComponentTypes.UUID);
		boolean failedOne = false;
		if (uuid != null) {
			if (uuid.equals(currentlySearched.get(CookiesDataComponentTypes.UUID))) {
				return IsSameResult.YES;
			}
			failedOne = true;
		}

		if (itemStack.get(DataComponentTypes.LORE) != null || currentlySearched.get(DataComponentTypes.LORE) != null) {
			final List<Text> lines = Optional.ofNullable(itemStack.get(DataComponentTypes.LORE))
					.map(LoreComponent::lines)
					.orElse(Collections.emptyList());
			final List<Text> otherLines = Optional.ofNullable(currentlySearched.get(DataComponentTypes.LORE))
					.map(LoreComponent::lines)
					.orElse(Collections.emptyList());

			if (lines.size() != otherLines.size()) {
				failedOne = true;
			} else {
				for (int i = 0; i < lines.size(); i++) {
					final Text line = lines.get(i);
					final Text otherLine = otherLines.get(i);
					if (!line.getString().equals(otherLine.getString())) {
						failedOne = true;
						break;
					}
				}
			}
		}


		if (itemStack.get(CookiesDataComponentTypes.TIMESTAMP) != null ||
			currentlySearched.get(CookiesDataComponentTypes.TIMESTAMP) != null) {
			long timestamp = Optional.ofNullable(itemStack.get(CookiesDataComponentTypes.TIMESTAMP))
					.map(Instant::toEpochMilli)
					.orElse(-1L);
			long otherTimestamp = Optional.ofNullable(currentlySearched.get(CookiesDataComponentTypes.TIMESTAMP))
					.map(Instant::toEpochMilli)
					.orElse(-1L);

			if (timestamp != otherTimestamp) {
				failedOne = true;
			}
		}

		if (itemStack.getCount() != currentlySearched.getCount()) {
			failedOne = true;
		}

		if (isSame(itemStack, currentlySearched)) {
			if (failedOne) {
				return IsSameResult.ALMOST;
			}
			return IsSameResult.YES;
		}
		return IsSameResult.NO;
	}

	/**
	 * Result used to distinguish between three states of matching.
	 */
	enum IsSameResult {
		YES,
		ALMOST,
		NO;

		public static IsSameResult wrapBoolean(boolean equals) {
			return equals ? YES : NO;
		}
	}
}
