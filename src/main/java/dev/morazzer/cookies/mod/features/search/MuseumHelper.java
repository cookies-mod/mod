package dev.morazzer.cookies.mod.features.search;

import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.config.categories.ItemSearchConfig;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemCompound;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.items.sources.CraftableItemSource;
import dev.morazzer.cookies.mod.events.InventoryEvents;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.constants.MuseumData;
import dev.morazzer.cookies.mod.repository.constants.RepositoryConstants;
import dev.morazzer.cookies.mod.screen.search.InspectItemScreen;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.Either;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemTooltipComponent;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MuseumHelper {

	Map<RepositoryItem, List<Item<?>>> items;

	public static void init() {
		InventoryEvents.beforeInit("cookies-regex:.*?[Mm]useum.*?", MuseumHelper::isEnabled, MuseumHelper::new);
	}

	private static boolean isEnabled(HandledScreen<?> handledScreen) {
		return showItemSearch() || showArmorSets();

	}

	public MuseumHelper(HandledScreen<?> handledScreen) {
		InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), ExceptionHandler.wrap(this::update));
		items = ItemSources.getItems()
				.stream()
				.collect(Collectors.groupingBy(item -> Objects.requireNonNullElse(item.itemStack()
						.get(CookiesDataComponentTypes.REPOSITORY_ITEM), RepositoryItem.EMPTY)));
	}

	private static boolean showItemSearch() {
		return ItemSearchConfig.getInstance().showInMuseum.getValue();
	}

	private static boolean showArmorSets() {
		return ConfigManager.getConfig().miscConfig.showMuseumArmorSets.getValue();
	}

	private void update(int slot, ItemStack itemStack) {
		if (slot > 53) {
			return;
		}

		var itemByName = RepositoryConstants.museumData.getItemByName(itemStack.getName().getString());
		if (itemByName.isError()) {
			if (isDonatedOrUiItem(itemStack)) {
				return;
			}
			itemByName.getError().ifPresent(error -> {
				ItemStack item = switch (error.errorType()) {
					case NO_ARMOR_FOUND -> {
						final ItemStack stack = new ItemStack(Items.RED_DYE);
						stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFF9b9b, false));
						yield stack;
					}
					case ITEM_NOT_FOUND -> itemStack;
					case NO_MATCHING_MUSEUM_FOUND -> Items.BARRIER.getDefaultStack();
				};
				if (itemStack == item) {
					return;
				}
				ItemUtils.copy(DataComponentTypes.CUSTOM_NAME, itemStack, item);
				ItemUtils.copy(DataComponentTypes.ITEM_NAME, itemStack, item);
				final LoreComponent loreComponent = itemStack.get(DataComponentTypes.LORE);
				if (loreComponent == null) {
					itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE,
							List.of(Text.literal("Can't find item by name " + itemStack.getName().getString())
									.formatted(Formatting.RED)));
				} else {
					final List<Text> lines = new ArrayList<>(loreComponent.lines());
					lines.add(Text.empty());
					lines.add(Text.literal("Can't find item by name " + itemStack.getName().getString())
							.formatted(Formatting.RED));
					itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE, lines);
				}
				itemStack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, item);
			});
			return;
		}
		final Optional<Either<MuseumData.MuseumItem, MuseumData.ArmorItem>> optionalResult = itemByName.getResult();
		if (optionalResult.isEmpty()) {
			return;
		}
		final Either<MuseumData.MuseumItem, MuseumData.ArmorItem> result = optionalResult.get();
		if (result.isLeft()) {
			if (!showItemSearch()) {
				return;
			}
			result.getLeft().ifPresent(item -> this.handleMuseumItem(item, itemStack));
		} else {
			result.getRight().ifPresent(armor -> this.handleMuseumArmor(armor, itemStack));
		}
	}

	public boolean isDonatedOrUiItem(ItemStack stack) {
		return stack.getItem() != Items.GRAY_DYE;
	}

	private void handleMuseumItem(MuseumData.MuseumItem item, ItemStack itemStack) {
		if (isDonatedOrUiItem(itemStack)) {
			return;
		}
		Item<?> source = items.getOrDefault(item.item(), Collections.emptyList())
				.stream()
				.min(Comparator.comparingInt(element -> mapToPriority(element.source())))
				.orElse(null);
		if (source == null) {
			return;
		}

		final List<Text> texts = Objects.requireNonNullElse(itemStack.get(CookiesDataComponentTypes.CUSTOM_LORE),
				new ArrayList<>(itemStack.get(DataComponentTypes.LORE).styledLines()));


		if (source.source() == ItemSources.CRAFTABLE) {
			if (source.data() instanceof CraftableItemSource.Data data) {
				MutableText text = Text.literal("This item can be crafted!");
				texts.add(Text.empty());
				if (data.hasAllIngredients() && data.canSupercraft()) {
					itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, Items.GREEN_DYE.getDefaultStack());
				} else if (data.hasAllIngredients() && data.showSupercraftWarning()) {
					text.formatted(Formatting.YELLOW);
					texts.add(text);
					itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, Items.YELLOW_DYE.getDefaultStack());
				} else {
					text.formatted(Formatting.RED);
					texts.add(text);
					itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, Items.ORANGE_DYE.getDefaultStack());
				}

				ItemSearchService.addCraftableTooltip(texts, data);

				if (data.showSupercraftWarning()) {
					texts.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_OVERVIEW)
							.formatted(Formatting.YELLOW));
				}
			}
			itemStack.set(CookiesDataComponentTypes.ITEM_CLICK_CONSUMER, this.performAction(source));
			itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE, texts);
			return;
		}

		texts.add(Text.empty());
		texts.add(Text.literal("This item was found somewhere on your profile!").formatted(Formatting.GREEN));
		ItemSearchService.appendMultiTooltip(ItemCompound.CompoundType.of(source.source(), source.data()),
				source.data(),
				texts);
		itemStack.set(CookiesDataComponentTypes.ITEM_CLICK_CONSUMER, this.performAction(source));
		itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE, texts);
		itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, Items.GREEN_DYE.getDefaultStack());
	}

	private void handleMuseumArmor(MuseumData.ArmorItem armor, ItemStack itemStack) {
		final List<RepositoryItem> rawItems =
				armor.armorIds().stream().sorted(Comparator.comparingInt(this::mapToPriority)).toList();


		final LoreComponent loreComponent = itemStack.get(DataComponentTypes.LORE);
		if (loreComponent == null) {
			return;
		}
		final List<Text> texts = Objects.requireNonNullElse(itemStack.get(CookiesDataComponentTypes.CUSTOM_LORE),
				new ArrayList<>(itemStack.get(DataComponentTypes.LORE).styledLines()));

		final List<Text> list = texts.stream()
				.takeWhile(text -> text.getSiblings().stream().map(Text::getStyle).noneMatch(Style::isStrikethrough))
				.collect(Collectors.toList());
		texts.removeAll(list);

		if (showArmorSets()) {
			final List<ItemStack> collect =
					rawItems.stream().map(RepositoryItem::constructItemStack).collect(Collectors.toList());
			list.add(Text.empty());
			itemStack.set(
					CookiesDataComponentTypes.LORE_ITEMS,
					new ItemTooltipComponent(collect, 4, new ArrayList<>(list)));
			list.clear();
		}

		if (showItemSearch() && !isDonatedOrUiItem(itemStack)) {
			List<RepositoryItem> sources = rawItems.stream()
					.map(item -> items.getOrDefault(item, Collections.emptyList()))
					.map(itemList -> itemList.stream()
							.min(Comparator.comparingInt(element -> mapToPriority(element.source()))))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.filter(item -> item.source() != ItemSources.CRAFTABLE)
					.map(item -> item.itemStack().get(CookiesDataComponentTypes.REPOSITORY_ITEM))
					.filter(Objects::nonNull)
					.toList();

			int maxLength = 0;
			final MutableText bars = Text.empty().formatted(Formatting.STRIKETHROUGH, Formatting.GRAY);
			list.add(bars);
			boolean allPresent = true;
			for (RepositoryItem rawItem : rawItems) {
				final MutableText line = Text.empty();
				boolean isAvailable = sources.contains(rawItem);
				allPresent = allPresent && isAvailable;
				if (isAvailable) {
					line.append(Text.literal(Constants.Emojis.YES).formatted(Formatting.GREEN));
				} else {
					line.append(Text.literal(Constants.Emojis.NO).formatted(Formatting.RED));
				}
				line.append(Text.literal(" "));
				line.append(rawItem.getFormattedName());
				int width = MinecraftClient.getInstance().textRenderer.getWidth(line);
				if (width > maxLength) {
					maxLength = width;
				}
				list.add(line);
			}
			if (allPresent) {
				itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, Items.GREEN_DYE.getDefaultStack());
			}
			final int width = MinecraftClient.getInstance().textRenderer.getWidth(" ");
			bars.append(StringUtils.repeat(' ', maxLength / width + 1));
			list.add(bars);
			if (!texts.isEmpty()) {
				texts.removeFirst();
			}
		}
		texts.addAll(0, list);

		itemStack.set(
				CookiesDataComponentTypes.CUSTOM_LORE,
				texts.isEmpty() ? Collections.singletonList(Text.empty()) : texts);
	}

	public int mapToPriority(ItemSources source) {
		return switch (source) {
			case STORAGE, INVENTORY, VAULT -> 1;
			case CHESTS -> 2;
			case ACCESSORY_BAG, FORGE, POTION_BAG, SACKS, SACK_OF_SACKS -> 3;
			case CRAFTABLE -> 4;
		};
	}

	private Consumer<Integer> performAction(Item<?> item) {
		return button -> {
			final ItemCompound.CompoundType compoundType = ItemCompound.CompoundType.of(item.source(), item.data());
			final Object itemData = item.data();
			if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				ItemSearchService.performAction(compoundType, itemData, item);
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				if (itemData instanceof CraftableItemSource.Data data) {
					if (!data.showSupercraftWarning()) {
						return;
					}
				}
				CookiesMod.openScreen(new InspectItemScreen(new ItemCompound(item), null));
			}
		};
	}

	private int mapToPriority(RepositoryItem repositoryItem) {
		return switch (Objects.requireNonNullElse(repositoryItem.getCategory(), "").replaceAll("DUNGEON", "").trim()) {
			case "HELMET" -> 1;
			case "CHESTPLATE" -> 2;
			case "LEGGINGS" -> 3;
			case "BOOTS" -> 4;
			case "NECKLACE" -> 5;
			case "CLOAK" -> 6;
			case "BELT" -> 7;
			case "BRACELET" -> 8;
			default -> Integer.MAX_VALUE;
		};
	}

}
