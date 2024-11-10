package codes.cookies.mod.features.cleanup;

import codes.cookies.mod.features.Loader;
import codes.cookies.mod.features.cleanup.dungeon.DungeonMessagesCleanup;

/**
 * Utility class to load all cleanup features.
 */
@SuppressWarnings("MissingJavadoc")
public interface CleanupFeatures {

    static void load() {
        Loader.load("CoopCleanup", CoopCleanupFeature::new);
        Loader.load("DungeonMessagesCleanup", DungeonMessagesCleanup::new);
        Loader.load("ItemTooltipCleanup", ItemTooltipCleanup::new);
    }

}
