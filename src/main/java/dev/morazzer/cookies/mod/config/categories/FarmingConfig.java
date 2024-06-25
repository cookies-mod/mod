package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.data.RancherSpeedConfig;
import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Parent;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.EnumCycleOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import javax.swing.SortOrder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

/**
 * Config that contains all farming related settings.
 */
@SuppressWarnings({"MissingJavadoc", "unused"})
public class FarmingConfig extends Category {

    public BooleanOption showPlotPriceBreakdown = new BooleanOption(
        Text.literal("Plot price breakdown"),
        Text.literal("Shows a breakdown of how much compost you need to unlock all plots."),
        false
    );

    public BooleanOption yawPitchDisplay = new BooleanOption(
        Text.literal("Yaw/Pitch display"),
        Text.literal("Displays your yaw/pitch on the screen (in a non obnoxious way)."),
        false
    );

    @Parent
    public TextDisplayOption ranchers = new TextDisplayOption(Text.literal("Rancher's Boots"), Text.literal(""));

    public BooleanOption showRancherSpeed = new BooleanOption(
        Text.literal("Show rancher speed"),
        Text.literal("Shows the speed selected on ranchers boots as item stack size."),
        false
    );

    public BooleanOption showRancherOptimalSpeeds = new BooleanOption(
        Text.literal("Show rancher overlay"),
        Text.literal("Show optimal speeds in the rancher's boots."),
        false
    );

    public RancherSpeedConfig rancherSpeed = new RancherSpeedConfig();

    @Parent
    public TextDisplayOption compostText = new TextDisplayOption(Text.literal("Composter"), Text.literal(""));

    public BooleanOption showCompostPriceBreakdown = new BooleanOption(
        Text.literal("Compost upgrade price"),
        Text.literal("Shows the amount of items required to max an upgrade."),
        false
    );

    public EnumCycleOption<SortOrder> compostSortOrder = new EnumCycleOption<>(
        Text.literal("Item sort"),
        Text.literal("How the items should be sorted."),
        SortOrder.ASCENDING
    ).withSupplier(value -> Text.of(StringUtils.capitalize(value.name().toLowerCase())))
     .onlyIf(showCompostPriceBreakdown);


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
