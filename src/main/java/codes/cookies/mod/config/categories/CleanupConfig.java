package codes.cookies.mod.config.categories;

import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Parent;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.EnumCycleOption;
import codes.cookies.mod.config.system.options.TextDisplayOption;
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
    public TextDisplayOption coopTitle = new TextDisplayOption(CONFIG_CLEANUP_CATEGORIES_COOP);

    public EnumCycleOption<CoopCleanup> coopCleanupOption = new EnumCycleOption<>(CONFIG_CLEANUP_COOP_CLEANUP,
        CoopCleanup.UNCHANGED).withSupplier(CoopCleanup::getUnchanged);


    @Parent
    public TextDisplayOption dungeonTitle = new TextDisplayOption(CONFIG_CLEANUP_CATEGORIES_DUNGEONS);

    public BooleanOption hideWatcherMessages = new BooleanOption(CONFIG_CLEANUP_HIDE_WATCHER_MESSAGES, false);

    public BooleanOption hidePotionEffectMessage = new BooleanOption(CONFIG_CLEANUP_HIDE_POTION_EFFECT_MESSAGE, false);

    public BooleanOption hideClassMessages = new BooleanOption(CONFIG_CLEANUP_HIDE_CLASS_MESSAGES, false);

    public BooleanOption hideUltimateReady = new BooleanOption(CONFIG_CLEANUP_HIDE_ULTIMATE_READY, false);

    public BooleanOption hideBlessingMessage = new BooleanOption(CONFIG_CLEANUP_HIDE_BLESSING_MESSAGE, false);

    public BooleanOption hideSilverfishMessage = new BooleanOption(CONFIG_CLEANUP_HIDE_SILVERFISH_MESSAGE, false);

    public BooleanOption hideDungeonKeyMessage = new BooleanOption(CONFIG_CLEANUP_HIDE_DUNGEON_KEY_MESSAGE, false);

    @Parent
    public TextDisplayOption itemTitle = new TextDisplayOption(CONFIG_CLEANUP_CATEGORIES_ITEMS);

    public BooleanOption removeDungeonStats = new BooleanOption(CONFIG_CLEANUP_REMOVE_DUNGEON_STATS, false);

    public BooleanOption removeReforgeStats = new BooleanOption(CONFIG_CLEANUP_REMOVE_REFORGE_STATS, false);

    public BooleanOption removeHpbStats = new BooleanOption(CONFIG_CLEANUP_REMOVE_HPB_STATS, false);

    public BooleanOption removeGemstoneStats = new BooleanOption(CONFIG_CLEANUP_REMOVE_GEMSTONE_STATS, false);

    public BooleanOption removeGearScore = new BooleanOption(CONFIG_CLEANUP_REMOVE_GEAR_SCORE, false);

    public BooleanOption removeBlank = new BooleanOption(CONFIG_CLEANUP_REMOVE_BLANK_LINE, false);

    public BooleanOption removeFullSetBonus = new BooleanOption(CONFIG_CLEANUP_REMOVE_FULL_SET_BONUS, false);

    public BooleanOption removeGemstoneLine = new BooleanOption(CONFIG_CLEANUP_REMOVE_GEMSTONE_LINE, false);

    public BooleanOption removeAbility = new BooleanOption(CONFIG_CLEANUP_REMOVE_ABILITY, false);

    public BooleanOption removePieceBonus = new BooleanOption(CONFIG_CLEANUP_REMOVE_PIECE_BONUS, false);

    public BooleanOption removeEnchants = new BooleanOption(CONFIG_CLEANUP_REMOVE_ENCHANTS, false);

    public BooleanOption removeReforge = new BooleanOption(CONFIG_CLEANUP_REMOVE_REFORGE, false);

    public BooleanOption removeSoulbound = new BooleanOption(CONFIG_CLEANUP_REMOVE_SOULBOUND, false);

    public BooleanOption removeRunes = new BooleanOption(CONFIG_CLEANUP_REMOVE_RUNES, false);

    @Parent
    public TextDisplayOption petTitle = new TextDisplayOption(CONFIG_CLEANUP_CATEGORIES_PETS);

    public BooleanOption removeMaxLevel = new BooleanOption(CONFIG_CLEANUP_REMOVE_MAX_LEVEL, false);

    public BooleanOption removeActions = new BooleanOption(CONFIG_CLEANUP_REMOVE_ACTIONS, false);

    public BooleanOption removeHeldItem = new BooleanOption(CONFIG_CLEANUP_REMOVE_HELD_ITEM, false);

    public CleanupConfig() {
        super(new ItemStack(Items.BRUSH), CONFIG_CLEANUP);
    }

    @Override
    public Row getRow() {
        return Row.BOTTOM;
    }

    @Override
    public int getColumn() {
        return 1;
    }

    @Getter
    public enum CoopCleanup {
        UNCHANGED(Text.translatable(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_KEEP)),
        EMPTY(Text.translatable(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_EMPTY)),
        ALL(Text.translatable(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_ALL)),
        OTHER(Text.translatable(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_OTHER)),
        ;

        private final Text unchanged;

        CoopCleanup(Text unchanged) {
            this.unchanged = unchanged;
        }
    }
}
