package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Hidden;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.SliderOption;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Category that contains all helper related config settings.
 */
@SuppressWarnings("MissingJavadoc")
public class HelpersConfig extends Category {
    public BooleanOption craftHelper = new BooleanOption(CONFIG_HELPERS_CRAFT_HELPER, false);

    public BooleanOption anvilHelper = new BooleanOption(CONFIG_HELPERS_ANVIL_HELPER, false);

    public BooleanOption itemChestTracker = new BooleanOption(CONFIG_HELPERS_CHEST_TRACKER, true);

    @Hidden
    public SliderOption<Integer> craftHelperSlot = SliderOption.integerOption(Text.empty(), Text.empty(), 14);

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
