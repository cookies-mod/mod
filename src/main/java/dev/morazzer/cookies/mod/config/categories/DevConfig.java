package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Config category that contains dev related settings.
 */
public class DevConfig extends Category {

    public BooleanOption hideConsoleSpam = new BooleanOption(CONFIG_DEV_HIDE_CONSOLE_SPAM, true);

    @SuppressWarnings("MissingJavadoc")
    public DevConfig() {
        super(new ItemStack(Items.COMPARATOR), CONFIG_DEV);
    }

    @Override
    public Row getRow() {
        return Row.BOTTOM;
    }

    @Override
    public int getColumn() {
        return 5;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
