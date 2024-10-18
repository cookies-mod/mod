package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Row;

import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import net.minecraft.item.Items;

/**
 * All settings related to the item search screen.
 */
public class ItemSearchConfig extends Category {

	public static ItemSearchConfig getInstance() {
		return ConfigManager.getConfig().itemSearchConfig;
	}

	public BooleanOption enableCraftableItems = new BooleanOption(CONFIG_ITEM_SEARCH_CRAFTABLE, true);
	public BooleanOption enableNotCraftableItems = new BooleanOption(CONFIG_ITEM_NON_SEARCH_CRAFTABLE, false).onlyIf(enableCraftableItems);
	public BooleanOption showOnlyMissingItems = new BooleanOption(CONFIG_ITEM_SHOW_ONLY_MISSING, true).onlyIf(enableCraftableItems);

	public ItemSearchConfig() {
		super(new ItemBuilder(Items.CRAFTING_TABLE).build(), CONFIG_ITEM_SEARCH);
	}

	@Override
	public Row getRow() {
		return Row.BOTTOM;
	}

	@Override
	public int getColumn() {
		return 3;
	}
}
