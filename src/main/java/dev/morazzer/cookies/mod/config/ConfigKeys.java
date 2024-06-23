package dev.morazzer.cookies.mod.config;

/**
 * A list of all available config keys.
 */
@SuppressWarnings("MissingJavadoc")
public class ConfigKeys {

    public static ConfigKey<Boolean> CLEANUP_HIDE_WATCHER_MESSAGE =
        new ConfigKey<>(config -> config.cleanupConfig.hideWatcherMessages);
    public static ConfigKey<Boolean> CLEANUP_HIDE_POTION_EFFECTS =
        new ConfigKey<>(config -> config.cleanupConfig.hidePotionEffectMessage);
    public static ConfigKey<Boolean> CLEANUP_HIDE_CLASS_MESSAGES =
        new ConfigKey<>(config -> config.cleanupConfig.hideClassMessages);
    public static ConfigKey<Boolean> CLEANUP_HIDE_ULTIMATE_READY =
        new ConfigKey<>(config -> config.cleanupConfig.hideUltimateReady);
    public static ConfigKey<Boolean> CLEANUP_HIDE_BLESSING =
        new ConfigKey<>(config -> config.cleanupConfig.hideBlessingMessage);
    public static ConfigKey<Boolean> CLEANUP_HIDE_SILVERFISH_MESSAGE =
        new ConfigKey<>(config -> config.cleanupConfig.hideSilverfishMessage);
    public static ConfigKey<Boolean> CLEANUP_HIDE_DUNGEON_KEY_MESSAGE =
        new ConfigKey<>(config -> config.cleanupConfig.hideDungeonKeyMessage);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_DUNGEON_STATS =
        new ConfigKey<>(config -> config.cleanupConfig.removeDungeonStats);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_REFORGE_STATS =
        new ConfigKey<>(config -> config.cleanupConfig.removeReforgeStats);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_HPB_STATS =
        new ConfigKey<>(config -> config.cleanupConfig.removeHpbStats);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_GEMSTONE_STATS =
        new ConfigKey<>(config -> config.cleanupConfig.removeGemstoneStats);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_GEAR_SCORE =
        new ConfigKey<>(config -> config.cleanupConfig.removeGearScore);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_BLANK =
        new ConfigKey<>(config -> config.cleanupConfig.removeBlank);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_ABILITY =
        new ConfigKey<>(config -> config.cleanupConfig.removeAbility);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_PIECE_BONUS =
        new ConfigKey<>(config -> config.cleanupConfig.removePieceBonus);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_FULL_SET_BONUS =
        new ConfigKey<>(config -> config.cleanupConfig.removeFullSetBonus);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_GEMSTONE_LINE =
        new ConfigKey<>(config -> config.cleanupConfig.removeGemstoneLine);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_ENCHANTS =
        new ConfigKey<>(config -> config.cleanupConfig.removeEnchants);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_REFORGE =
        new ConfigKey<>(config -> config.cleanupConfig.removeReforge);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_SOULBOUND =
        new ConfigKey<>(config -> config.cleanupConfig.removeSoulbound);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_RUNES =
        new ConfigKey<>(config -> config.cleanupConfig.removeRunes);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_MAX_LEVEL =
        new ConfigKey<>(config -> config.cleanupConfig.removeMaxLevel);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_ACTIONS =
        new ConfigKey<>(config -> config.cleanupConfig.removeActions);
    public static ConfigKey<Boolean> CLEANUP_REMOVE_HELD_ITEM =
        new ConfigKey<>(config -> config.cleanupConfig.removeHeldItem);
    public static ConfigKey<Boolean> MISC_SCROLLABLE_TOOLTIP =
        new ConfigKey<>(config -> config.miscConfig.enableScrollableTooltips);
    public static ConfigKey<Boolean> FARMING_PLOT_PRICE =
        new ConfigKey<>(config -> config.farmingConfig.showPlotPriceBreakdown);
    public static ConfigKey<Boolean> DEV_HIDE_SPAM =
        new ConfigKey<>(config -> config.devConfig.hideConsoleSpam);

}
