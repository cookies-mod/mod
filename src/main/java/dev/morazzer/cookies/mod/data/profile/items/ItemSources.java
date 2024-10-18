package dev.morazzer.cookies.mod.data.profile.items;

import dev.morazzer.cookies.mod.data.profile.items.sources.AccessoryItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.CraftableItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.ForgeItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.InventoryItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.IslandChestItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.MiscItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.SackItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.StorageItemSource;
import dev.morazzer.cookies.mod.data.profile.sub.MiscItemData;
import dev.morazzer.cookies.mod.translations.TranslationKeys;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.minecraft.text.Text;

/**
 * A list of all supported item sources.
 */
@AllArgsConstructor
@Getter
public enum ItemSources {

	CHESTS(Text.translatable(TranslationKeys.ITEM_SOURCE_CHEST), IslandChestItemSource.getInstance(), false),
	STORAGE(Text.translatable(TranslationKeys.ITEM_SOURCE_STORAGE), StorageItemSource.getInstance(), true),
	SACKS(Text.translatable(TranslationKeys.ITEM_SOURCE_SACK), SackItemSource.getInstance(), true),
	INVENTORY(Text.translatable(TranslationKeys.ITEM_SOURCE_INVENTORY), InventoryItemSource.getInstance(), true),
	FORGE(Text.translatable(TranslationKeys.ITEM_SOURCE_FORGE), ForgeItemSource.getInstance(), false),
	VAULT(
			Text.translatable(TranslationKeys.ITEM_SOURCE_VAULT),
			vault -> MiscItemSource.get(MiscItemData.Type.VAULT, vault)),
	SACK_OF_SACKS(
			Text.translatable(TranslationKeys.ITEM_SOURCE_SACK_OF_SACKS),
			sacks -> MiscItemSource.get(MiscItemData.Type.SACK_OF_SACKS, sacks)),
	POTION_BAG(
			Text.translatable(TranslationKeys.ITEM_SOURCE_POTION_BAG),
			potionBag -> MiscItemSource.get(MiscItemData.Type.POTION_BAG, potionBag)),
	ACCESSORY_BAG(Text.translatable(TranslationKeys.ITEM_SOURCE_ACCESSORY_BAG), AccessoryItemSource.getInstance(), false),
	CRAFTABLE(Text.translatable(TranslationKeys.ITEM_SOURCE_CRAFTABLE), new CraftableItemSource(), false);

	private final Text name;
	private final ItemSource<?> itemSource;
	private final boolean supportsSupercraft;

	ItemSources(Text name, Function<ItemSources, ItemSource<?>> itemSource) {
		this.name = name;
		this.itemSource = itemSource.apply(this);
		this.supportsSupercraft = false;
	}

	public static Collection<Item<?>> getItems() {
		return getItems(ItemSources.values());
	}

	public static Collection<Item<?>> getItems(ItemSources... itemSources) {
		Set<Item<?>> items = new HashSet<>();
		for (ItemSources itemSource : itemSources) {
			items.addAll(itemSource.itemSource.getAllItems());
		}
		return items;
	}

	public static ItemSources[] none() {
		return new ItemSources[0];
	}
}
