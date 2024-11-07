package dev.morazzer.cookies.mod.services.item;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemCompound;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.items.sources.CraftableItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.StorageItemSource;
import dev.morazzer.cookies.mod.data.profile.profile.IslandChestStorage;
import dev.morazzer.cookies.mod.repository.Ingredient;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class ItemServices {
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
	 * Appends the item source information and left click text to the tooltip.
	 *
	 * @param type         The type of compound.
	 * @param compoundData The data.
	 * @param tooltip      The tooltip to append to.
	 */
	public static void appendMultiTooltip(
			@Nullable ItemCompound.CompoundType type, Object compoundData, List<Text> tooltip
	) {
		switch (type) {
			case CHEST -> tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_ALL_CHEST)
					.formatted(Formatting.YELLOW));
			case CHEST_POS -> {
				tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_CHEST)
						.formatted(Formatting.YELLOW));
				final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) compoundData;
				tooltip.add(Text.translatable(
						TranslationKeys.BLOCK_XYZ,
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
			case CRAFTABLE -> addCraftableTooltip(tooltip, compoundData);
			case INVENTORY -> tooltip.add(Text.literal("This item is in your inventory!").formatted(Formatting.YELLOW));
			case FORGE ->
					tooltip.add(Text.literal("This item currently is in the forge!").formatted(Formatting.YELLOW));
			case null, default -> tooltip.add(Text.literal("An error occurred :c " + type).formatted(Formatting.RED));
		}
	}

	/**
	 * Appends the tooltip used for craftable items.
	 *
	 * @param tooltip      The tooltip.
	 * @param compoundData The data.
	 */
	public static void addCraftableTooltip(List<Text> tooltip, Object compoundData) {
		if (compoundData instanceof CraftableItemSource.Data data) {
			if (data.hasAllIngredients() && data.canSupercraft()) {
				tooltip.add(Text.literal("You can craft this item!").formatted(Formatting.DARK_GREEN));
			} else if (data.showSupercraftWarning()) {
				tooltip.add(Text.literal("Some items are out of the supercraft reach!").formatted(Formatting.YELLOW));
				tooltip.add(Text.literal("(Right-click to find items marked with " + Constants.Emojis.WARNING + ")")
						.formatted(Formatting.DARK_GRAY));
			}
			MutableText bar = Text.literal("").formatted(Formatting.STRIKETHROUGH, Formatting.GRAY);
			tooltip.add(bar);
			int maxWidth = 0;
			for (Map.Entry<Ingredient, CraftableItemSource.IngredientData> entry : data.amounts().entrySet()) {
				final CraftableItemSource.IngredientData ingredientData = entry.getValue();
				int amount = ingredientData.available();
				Text text = Text.literal(" ")
						.append(entry.getKey()
								.getAsItem()
								.getName()
								.copy()
								.append(Text.literal(": ")
										.formatted(Formatting.GRAY)
										.append(Text.literal("%s/%s".formatted(Math.min(
												entry.getKey().getAmount(),
												amount), entry.getKey().getAmount())))));
				final MutableText append;
				if (ingredientData.hasAllItems()) {
					if (ingredientData.canSupercraft()) {
						append = Text.literal(Constants.Emojis.YES).formatted(Formatting.GREEN).append(text);
					} else {
						append = Text.literal(Constants.Emojis.WARNING).formatted(Formatting.YELLOW).append(text);
					}
				} else {
					append = Text.literal(Constants.Emojis.NO).formatted(Formatting.RED).append(text);
				}
				maxWidth = Math.max(maxWidth, MinecraftClient.getInstance().textRenderer.getWidth(append));
				tooltip.add(append);
			}

			final int width = MinecraftClient.getInstance().textRenderer.getWidth(" ");
			bar.append(StringUtils.repeat(' ', maxWidth / width + 1));
			tooltip.add(bar);
			tooltip.add(Text.empty());
			if (data.hasAllIngredients()) {
				tooltip.add(Text.literal("Left-click to open recipe!").formatted(Formatting.YELLOW));
			} else {
				tooltip.add(Text.literal("Left-click to set as craft helper item!").formatted(Formatting.YELLOW));
			}
		}
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

	public static Set<BlockPos> extractChestPositions(ItemCompound itemCompound) {
		Set<BlockPos> positions = new HashSet<>();
		for (Item<?> item : itemCompound.items()) {
			if (item.source() != ItemSources.CHESTS) {
				continue;
			}

			final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) item.data();
			positions.add(data.blockPos());
			Optional.of(data).flatMap(IslandChestStorage.ChestItem::secondChest).ifPresent(positions::add);
		}

		return positions;
	}

	public static Optional<RepositoryItem> getRepositoryItem(@NotNull ItemStack itemStack) {
		return Optional.ofNullable(itemStack.get(CookiesDataComponentTypes.REPOSITORY_ITEM));
	}
}
