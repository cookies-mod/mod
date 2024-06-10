package dev.morazzer.cookies.mod.config.system;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Category to show all currently enabled settings.
 */
public class ToggledCategory extends Category {

    @SuppressWarnings("MissingJavadoc")
    public ToggledCategory() {
        super(new ItemStack(Items.RED_STAINED_GLASS_PANE));
    }

    @Override
    public Text getName() {
        return Text.literal("Toggled");
    }

    @Override
    public Text getDescription() {
        return Text.literal("Show either all on or off settings!");
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
