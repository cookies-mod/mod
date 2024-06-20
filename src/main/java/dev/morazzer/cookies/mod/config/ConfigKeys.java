package dev.morazzer.cookies.mod.config;

import dev.morazzer.cookies.mod.config.categories.CleanupConfig;

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

}
