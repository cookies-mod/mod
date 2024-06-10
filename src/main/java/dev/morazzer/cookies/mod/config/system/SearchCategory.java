package dev.morazzer.cookies.mod.config.system;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Category to search through all config options.
 */
@SuppressWarnings({"unused", "MissingJavadoc"})
public class SearchCategory extends Category {
    public SearchCategory() {
        super(new ItemStack(Items.RECOVERY_COMPASS));
    }

    @Override
    public Text getName() {
        return Text.literal("Search");
    }

    @Override
    public Text getDescription() {
        return Text.literal("Search all config settings.");
    }

    @Override
    public Row getRow() {
        return Row.TOP;
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
