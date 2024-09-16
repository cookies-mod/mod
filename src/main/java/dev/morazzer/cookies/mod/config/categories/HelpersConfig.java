package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Hidden;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.ButtonOption;
import dev.morazzer.cookies.mod.config.system.options.EnumCycleOption;
import dev.morazzer.cookies.mod.config.system.options.SliderOption;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelperLocation;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelperPlacement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Category that contains all helper related config settings.
 */
@SuppressWarnings("MissingJavadoc")
public class HelpersConfig extends Category {
    public BooleanOption craftHelper = new BooleanOption(CONFIG_HELPERS_CRAFT_HELPER, false);

    @Hidden
    public EnumCycleOption<CraftHelperLocation> craftHelperLocation =
        new EnumCycleOption<>("", CraftHelperLocation.RIGHT_INVENTORY);

    public ButtonOption openCraftHelperLocationEditor = new ButtonOption(CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS,
        this::openCraftHelperEditor,
        CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS_BUTTON);

    private void openCraftHelperEditor() {
        CookiesMod.openScreen(new CraftHelperPlacement());
    }

    public BooleanOption anvilHelper = new BooleanOption(CONFIG_HELPERS_ANVIL_HELPER, false);

    public BooleanOption itemChestTracker = new BooleanOption(CONFIG_HELPERS_CHEST_TRACKER, true);

    @Hidden
    public SliderOption<Integer> craftHelperSlot = SliderOption.integerOption("", 14);

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
