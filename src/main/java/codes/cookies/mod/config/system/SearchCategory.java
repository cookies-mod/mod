package codes.cookies.mod.config.system;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Category to search through all config options.
 */
@SuppressWarnings({"unused", "MissingJavadoc"})
public class SearchCategory extends Category {
    public SearchCategory() {
        super(new ItemStack(Items.RECOVERY_COMPASS), CONFIG_SEARCH);
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
