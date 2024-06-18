package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Parent;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * All settings related to mining.
 */
@SuppressWarnings("MissingJavadoc")
public class MiningConfig extends Category {
    public MiningConfig() {
        super(new ItemStack(Items.DIAMOND_PICKAXE));
        updateHotm = ((Predicate<Object>)(o -> showHotmPerkLevelAsStackSize.getValue()))
            .or(o -> highlightDisabledHotmPerks.getValue())
            .or(o -> showNext10Cost.getValue())
            .or(o -> showTotalCost.getValue());
    }

    @Parent
    public TextDisplayOption hotmParentDisplay =
        new TextDisplayOption(Text.literal("HOTM"), Text.literal("Settings for the hotm"));

    public BooleanOption showHotmPerkLevelAsStackSize = new BooleanOption(
        Text.literal("Show perk as size"),
        Text.literal("Shows the hotm perk level as item stack size"),
        false
    );

    public BooleanOption highlightDisabledHotmPerks = new BooleanOption(
        Text.literal("Highlight disabled"),
        Text.literal("Highlights the disabled perks in red."),
        false
    );

    public BooleanOption showNext10Cost = new BooleanOption(
        Text.literal("Cost for next 10"),
        Text.literal("Shows the cost for the next 10 levels"),
        false
    );

    public BooleanOption showTotalCost = new BooleanOption(
        Text.literal("Total cost"),
        Text.literal("Shows the total cost"),
        false
    );

    public Predicate<Object> updateHotm;

    @Override
    public Text getName() {
        return Text.literal("Mining");
    }

    @Override
    public Text getDescription() {
        return Text.literal("All settings related to mining.");
    }

    @Override
    public Row getRow() {
        return Row.TOP;
    }

    @Override
    public int getColumn() {
        return 2;
    }
}
