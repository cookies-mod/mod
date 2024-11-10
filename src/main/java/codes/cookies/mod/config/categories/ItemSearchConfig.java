package codes.cookies.mod.config.categories;

import java.awt.*;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.ColorOption;
import codes.cookies.mod.config.system.options.EnumCycleOption;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import lombok.Getter;

import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * All settings related to the item search screen.
 */
public class ItemSearchConfig extends Category {

	public BooleanOption enableCraftableItems = new BooleanOption(CONFIG_ITEM_SEARCH_CRAFTABLE, true);
	public BooleanOption enableNotCraftableItems = new BooleanOption(CONFIG_ITEM_NON_SEARCH_CRAFTABLE, false).onlyIf(
			enableCraftableItems);
	public BooleanOption showOnlyMissingItems = new BooleanOption(CONFIG_ITEM_SHOW_ONLY_MISSING, true).onlyIf(
			enableCraftableItems);
	public BooleanOption showInMuseum = new BooleanOption(CONFIG_ITEM_SHOW_IN_MUSEUM, true);
	public BooleanOption persistSearch = new BooleanOption(CONFIG_ITEM_PERSIST_SEARCH, false);
	public EnumCycleOption<HighlightTime> highlightTime = new EnumCycleOption<>(
			CONFIG_ITEM_HIGHLIGHT_TIME,
			HighlightTime.TEN)
			.withSupplier(HighlightTime::getText);
	public ColorOption highlightColor = new ColorOption(CONFIG_ITEM_HIGHLIGHT_COLOR, new Color(Constants.MAIN_COLOR));

	public ItemSearchConfig() {
		super(new ItemBuilder(Items.CRAFTING_TABLE).build(), CONFIG_ITEM_SEARCH);
	}

	public static ItemSearchConfig getInstance() {
		return ConfigManager.getConfig().itemSearchConfig;
	}

	@Override
	public Row getRow() {
		return Row.BOTTOM;
	}

	@Override
	public int getColumn() {
		return 3;
	}

	@Getter
	public enum HighlightTime {
		TEN(10), TWENTY(20), THIRTY(30), SIXTY(60), ONETWENTY(120);

		private final Text text;
		private final int time;

		HighlightTime(int time) {
			this.time = time;
			this.text = Text.literal("%ss".formatted(time));
		}
	}
}
