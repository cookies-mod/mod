package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Hidden;
import dev.morazzer.cookies.mod.config.system.Option;
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
    public BooleanOption craftHelper = new BooleanOption(
        Text.literal("Craft Helper"),
        Text.literal("Shows the items required to craft something and your progress in the inventory."),
        false
    );

    public BooleanOption anvilHelper = new BooleanOption(
        Text.literal("Anvil Helper"),
        Text.literal("Highlights the same book in your inventory when combining them in an anvil."),
        false
    );

    public BooleanOption itemChestTracker = new BooleanOption(
        Text.literal("Chest Tracker"),
        Text.literal("Allows for tracking of chests on private island."),
        true
    );

    @Hidden
    public SliderOption<Integer> craftHelperSlot = SliderOption.integerOption(Text.empty(), Text.empty(), 14);

    public HelpersConfig() {
        super(new ItemStack(Items.SCAFFOLDING));
    }

    @Override
    public Text getName() {
        return Text.literal("Helpers");
    }

    @Override
    public Text getDescription() {
        return Text.literal("Settings that help you with keeping track of certain things.");
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
