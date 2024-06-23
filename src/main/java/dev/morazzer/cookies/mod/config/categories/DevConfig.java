package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Config category that contains dev related settings.
 */
public class DevConfig extends Category {

    public BooleanOption hideConsoleSpam = new BooleanOption(
        Text.literal("Remove console spam"),
        Text.literal("Removes spam from the console by canceling various logger invocations"),
        true
    );

    @SuppressWarnings("MissingJavadoc")
    public DevConfig() {
        super(new ItemStack(Items.COMPARATOR));
    }

    @Override
    public Text getName() {
        return Text.literal("Dev Config");
    }

    @Override
    public Text getDescription() {
        return Text.literal("Development related config entries.");
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
