package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Parent;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Config that contains all farming related settings.
 */
@SuppressWarnings({"MissingJavadoc", "unused"})
public class FarmingConfig extends Category {

    public BooleanOption showRancherSpeed = new BooleanOption(
        Text.literal("Show rancher speed"),
        Text.literal("Shows the speed selected on ranchers boots as item stack size."),
        false
    );

    public BooleanOption showPlotPriceBreakdown = new BooleanOption(
        Text.literal("Plot price breakdown"),
        Text.literal("Shows a breakdown of how much compost you need to unlock all plots."),
        false
    );

    @Parent
    public TextDisplayOption jacobsText = new TextDisplayOption(Text.literal("Jacob / Contests"), Text.literal(""));

    public BooleanOption highlightUnclaimedJacobContests = new BooleanOption(
        Text.literal("Highlight unclaimed"),
        Text.literal("Highlight unclaimed jacob contests in his inventory."),
        false
    );

    public FarmingConfig() {
        super(new ItemStack(Items.WHEAT));
    }

    @Override
    public Text getName() {
        return Text.literal("Farming Config");
    }

    @Override
    public Text getDescription() {
        return Text.literal("Farming related settings.");
    }

    @Override
    public Row getRow() {
        return Row.TOP;
    }

    @Override
    public int getColumn() {
        return 1;
    }
}
