package dev.morazzer.cookies.mod.screen.search;

import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import lombok.Getter;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * The source categories for the {@link ItemSearchScreen}
 */
@Getter
public enum ItemSourceCategories {

	ALL(
			new ItemStack(Items.CRAFTING_TABLE),
			Text.translatable(TranslationKeys.ITEM_SOURCE_ALL),
			ItemSources.CHESTS,
			ItemSources.SACKS,
			ItemSources.STORAGE,
			ItemSources.VAULT,
			ItemSources.ACCESSORY_BAG,
			ItemSources.SACK_OF_SACKS,
			ItemSources.POTION_BAG),
	CHEST(new ItemStack(Items.CHEST), ItemSources.CHESTS.getName(), ItemSources.CHESTS),
	SACK(new ItemBuilder(Items.PLAYER_HEAD).setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI0OWEyY2I5MDczNzk5MzIwMWZlNzJhMWYxYWI3NWM1YzkzYzI4ZjA0N2Y2ODVmZmFkNWFiMjBjN2IwY2FmMCJ9fX0=")
			.build(), ItemSources.SACKS.getName(), ItemSources.SACKS),
	STORAGE(new ItemBuilder(Items.PLAYER_HEAD).setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY4NDA1MTE2YzFkYWE3Y2UyZjAxMjU5MTQ1OGQ1MDI0NmQwYTQ2N2JjYjk1YTVhMmMwMzNhZWZkNjAwOGI2MyJ9fX0K")
			.build(), ItemSources.STORAGE.getName(), ItemSources.STORAGE),;

	public static final ItemSourceCategories[] VALUES = values();
	private final ItemStack display;
	private final Text name;
	private final ItemSources[] sources;

	ItemSourceCategories(ItemStack display, Text name, ItemSources... sources) {
		this.display = display;
		this.name = name;
		this.sources = sources;
	}

	public boolean has(ItemSources source) {
		for (ItemSources itemSources : this.sources) {
			if (itemSources == source) {
				return true;
			}
		}
		return false;
	}
}
