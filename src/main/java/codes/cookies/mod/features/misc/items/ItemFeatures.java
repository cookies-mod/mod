package codes.cookies.mod.features.misc.items;

import codes.cookies.mod.features.Loader;

/**
 * Utility class to load all item related features.
 */
@SuppressWarnings("MissingJavadoc")
public class ItemFeatures {

    public static void load() {
        Loader.load("ItemStats", ItemStats::register);
        Loader.load("SackTrackerListener", SackTrackerListener::new);
        Loader.load("SackInventoryTracker", SackInventoryTracker::new);
        Loader.load("StorageTracker", StorageTracker::new);
        Loader.load("ChestTracker", ChestTracker::new);
		Loader.load("MiscItemTracker", MiscItemTracker::register);
		Loader.load("AccessoryTracker", AccessoryTracker::register);
    }

}
