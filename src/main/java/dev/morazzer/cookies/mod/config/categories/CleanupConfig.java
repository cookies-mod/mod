package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Parent;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.EnumCycleOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Settings related to clean up features.
 */
@SuppressWarnings("MissingJavadoc")
public class CleanupConfig extends Category {

    @Parent
    public TextDisplayOption coopTitle = new TextDisplayOption(Text.literal("Coop"), Text.empty());

    public EnumCycleOption<CoopCleanup> coopCleanupOption =
        new EnumCycleOption<>(Text.literal("Collection tooltips"),
            Text.literal("Hides the names of coop members from the collection item."), CoopCleanup.UNCHANGED)
            .withSupplier(CoopCleanup::getUnchanged);


    @Parent
    public TextDisplayOption dungeonTitle = new TextDisplayOption(Text.literal("Dungeons"), Text.empty());

    public BooleanOption hideWatcherMessages = new BooleanOption(
        Text.literal("Hide watcher messages"),
        Text.literal("Hides all watcher messages"),
        false
    );

    public BooleanOption hidePotionEffectMessage = new BooleanOption(
        Text.literal("Hide potion message"),
        Text.literal("Hides the paused effects message."),
        false
    );

    public BooleanOption hideClassMessages = new BooleanOption(
        Text.literal("Hide class messages"),
        Text.literal("Hides the class stat messages."),
        false
    );

    public BooleanOption hideUltimateReady = new BooleanOption(
        Text.literal("Hide ultimate ready"),
        Text.literal("Hides the ultimate ready message."),
        false
    );

    public BooleanOption hideBlessingMessage = new BooleanOption(
        Text.literal("Hide blessing messages"),
        Text.literal("Hides all blessing messages"),
        false
    );

    public BooleanOption hideSilverfishMessage = new BooleanOption(
        Text.literal("Hide silverfish messages"),
        Text.literal("Hides the silverfish moving message."),
        false
    );

    public BooleanOption hideDungeonKeyMessage = new BooleanOption(
        Text.literal("Hide key messages"),
        Text.literal("Hides the key pickup messages"),
        false
    );

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
