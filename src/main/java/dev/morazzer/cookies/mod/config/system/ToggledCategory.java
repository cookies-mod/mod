package dev.morazzer.cookies.mod.config.system;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Category to show all currently enabled settings.
 */
public class ToggledCategory extends Category {

    @SuppressWarnings("MissingJavadoc")
    public ToggledCategory() {
        super(new ItemStack(Items.RED_STAINED_GLASS_PANE), CONFIG_TOGGLED);
    }

    @Override
    public Row getRow() {
        return Row.BOTTOM;
    }

    @Override
    public int getColumn() {
        return 6;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
