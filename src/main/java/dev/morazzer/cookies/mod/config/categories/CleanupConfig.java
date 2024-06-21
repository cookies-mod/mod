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

    @Parent
    public TextDisplayOption itemTitle = new TextDisplayOption(Text.literal("Items"), Text.empty());

    public BooleanOption removeDungeonStats = new BooleanOption(
        Text.literal("Remove dungeon stats"),
        Text.literal("Removes the dungeon stats from the item."),
        false
    );

    public BooleanOption removeReforgeStats = new BooleanOption(
        Text.literal("Remove reforge stats"),
        Text.literal("Removes the reforge stats from the item."),
        false
    );

    public BooleanOption removeHpbStats = new BooleanOption(
        Text.literal("Remove hpb"),
        Text.literal("Removes the yellow hot potato book stats."),
        false
    );

    public BooleanOption removeGemstoneStats = new BooleanOption(
        Text.literal("Remove gemstone stats"),
        Text.literal("Removes the gemstone stats from the item."),
        false
    );

    public BooleanOption removeGearScore = new BooleanOption(
        Text.literal("Remove gear score"),
        Text.literal("Removes the gear score from the item."),
        false
    );

    public BooleanOption removeBlank = new BooleanOption(
        Text.literal("Remove blank lines"),
        Text.literal("Removes blank lines from the item."),
        false
    );

    public BooleanOption removeFullSetBonus = new BooleanOption(
        Text.literal("Remove full set bonus"),
        Text.literal("Removes the full set bonus from the item."),
        false
    );

    public BooleanOption removeGemstoneLine = new BooleanOption(
        Text.literal("Remove gemstones"),
        Text.literal("Removes the gemstone line from the item."),
        false
    );

    public BooleanOption removeAbility = new BooleanOption(
        Text.literal("Remove abilities"),
        Text.literal("Removes abilities from the item."),
        false
    );

    public BooleanOption removePieceBonus = new BooleanOption(
        Text.literal("Remove piece bonus"),
        Text.literal("Removes the piece bonus from the item."),
        false
    );

    public BooleanOption removeEnchants = new BooleanOption(
        Text.literal("Remove enchants"),
        Text.literal("Removes enchants from the item."),
        false
    );

    public BooleanOption removeReforge = new BooleanOption(
        Text.literal("Remove reforges"),
        Text.literal("Removes reforges from the item."),
        false
    );

    public BooleanOption removeSoulbound = new BooleanOption(
        Text.literal("Remove soulbound"),
        Text.literal("Removes the soulbound text from the item."),
        false
    );

    public BooleanOption removeRunes = new BooleanOption(
        Text.literal("Remove runes"),
        Text.literal("Removes runes from the item."),
        false
    );

    @Parent
    public TextDisplayOption petTitle = new TextDisplayOption(Text.literal("Pets"), Text.empty());

    public BooleanOption removeMaxLevel = new BooleanOption(
        Text.literal("Remove max level"),
        Text.literal("Removes the max level and xp lines from the pet."),
        false
    );

    public BooleanOption removeActions = new BooleanOption(
        Text.literal("Remove actions"),
        Text.literal("Removes the left-click and right-click actions from the pet."),
        false
    );

    public BooleanOption removeHeldItem = new BooleanOption(
        Text.literal("Remove held item"),
        Text.literal("Removes the left-click and right-click actions from the pet."),
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
