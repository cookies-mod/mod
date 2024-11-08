package codes.cookies.mod.config.categories;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Foldable;
import codes.cookies.mod.config.system.Hidden;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.ButtonOption;
import codes.cookies.mod.config.system.options.EnumCycleOption;
import codes.cookies.mod.config.system.options.SliderOption;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperLocation;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperPlacement;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * Category that contains all helper related config settings.
 */
@SuppressWarnings("MissingJavadoc")
public class HelpersConfig extends Category {


    public BooleanOption anvilHelper = new BooleanOption(CONFIG_HELPERS_ANVIL_HELPER, false);

    public BooleanOption itemChestTracker = new BooleanOption(CONFIG_HELPERS_CHEST_TRACKER, true);

	public CraftHelperFoldable craftHelper = new CraftHelperFoldable();

    @Hidden
    public SliderOption<Integer> craftHelperSlot = SliderOption.integerOption("", 14);

	public class CraftHelperFoldable extends Foldable {
		public BooleanOption craftHelper = new BooleanOption(CONFIG_HELPERS_CRAFT_HELPER_SETTING, true);

		@Hidden
		public EnumCycleOption<CraftHelperLocation> craftHelperLocation =
				new EnumCycleOption<>("", CraftHelperLocation.RIGHT_INVENTORY);

		public ButtonOption openCraftHelperLocationEditor = new ButtonOption(CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS,
				this::openCraftHelperEditor,
				CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS_BUTTON);

		public CraftHelperSourcesFoldable sources = new CraftHelperSourcesFoldable();

		public List<ItemSources> getSources() {
			List<ItemSources> sources = new ArrayList<>();
			if (this.sources.chests.getValue()) {
				sources.add(ItemSources.CHESTS);
			}
			if (this.sources.storage.getValue()) {
				sources.add(ItemSources.STORAGE);
			}
			if (this.sources.sacks.getValue()) {
				sources.add(ItemSources.SACKS);
			}
			if (this.sources.inventory.getValue()) {
				sources.add(ItemSources.INVENTORY);
			}
			if (this.sources.forge.getValue()) {
				sources.add(ItemSources.FORGE);
			}
			if (this.sources.vault.getValue()) {
				sources.add(ItemSources.VAULT);
			}
			if (this.sources.sacksOfSacks.getValue()) {
				sources.add(ItemSources.SACK_OF_SACKS);
			}
			if (this.sources.potionBag.getValue()) {
				sources.add(ItemSources.POTION_BAG);
			}
			if (this.sources.accessoryBag.getValue()) {
				sources.add(ItemSources.ACCESSORY_BAG);
			}
			return sources;
		}

		public class CraftHelperSourcesFoldable extends Foldable {

			public BooleanOption chests = new BooleanOption(ITEM_SOURCE_CHEST, true);
			public BooleanOption storage = new BooleanOption(ITEM_SOURCE_STORAGE, true);
			public BooleanOption sacks = new BooleanOption(ITEM_SOURCE_SACK, true);
			public BooleanOption inventory = new BooleanOption(ITEM_SOURCE_INVENTORY, true);
			public BooleanOption forge = new BooleanOption(ITEM_SOURCE_FORGE, true);
			public BooleanOption vault = new BooleanOption(ITEM_SOURCE_VAULT, true);
			public BooleanOption sacksOfSacks = new BooleanOption(ITEM_SOURCE_SACK_OF_SACKS, true);
			public BooleanOption potionBag = new BooleanOption(ITEM_SOURCE_POTION_BAG, true);
			public BooleanOption accessoryBag = new BooleanOption(ITEM_SOURCE_ACCESSORY_BAG, true);

			@Override
			public String getName() {
				return CONFIG_HELPERS_CRAFT_HELPER_SOURCES;
			}
		}

		private void openCraftHelperEditor() {
			CookiesMod.openScreen(new CraftHelperPlacement());
		}

		@Override
		public String getName() {
			return CONFIG_HELPERS_CRAFT_HELPER;
		}
	}


	public HelpersConfig() {
        super(new ItemStack(Items.SCAFFOLDING), CONFIG_HELPERS);
    }

    @Override
    public Row getRow() {
        return Row.BOTTOM;
    }

    @Override
    public int getColumn() {
        return 0;
    }
}
