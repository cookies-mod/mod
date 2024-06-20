package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.EnumCycleOption;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Settings related to clean up features.
 */
@SuppressWarnings("MissingJavadoc")
public class CleanupConfig extends Category {

    public EnumCycleOption<CoopCleanup> coopCleanupOption =
        new EnumCycleOption<>(Text.literal("Collection tooltips"),
            Text.literal("Hides the names of coop members from the collection item."), CoopCleanup.UNCHANGED)
            .withSupplier(CoopCleanup::getUnchanged);

    @Getter
    public enum CoopCleanup {
        UNCHANGED(Text.literal("Keep")),
        EMPTY(Text.literal("Empty")),
        ALL(Text.literal("All")),
        OTHER(Text.literal("Others")),
        ;

        private final Text unchanged;

        CoopCleanup(Text unchanged) {
            this.unchanged = unchanged;
        }
    }

    public CleanupConfig() {
        super(new ItemStack(Items.BRUSH));
    }

    @Override
    public Text getName() {
        return Text.literal("Cleanup");
    }

    @Override
    public Text getDescription() {
        return Text.literal("Various cleanup settings, that either hide or modify what you see.");
    }

    @Override
    public Row getRow() {
        return Row.BOTTOM;
    }

    @Override
    public int getColumn() {
        return 1;
    }
}
