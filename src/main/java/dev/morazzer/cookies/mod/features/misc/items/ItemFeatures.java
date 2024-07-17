package dev.morazzer.cookies.mod.features.misc.items;

import dev.morazzer.cookies.mod.features.Loader;

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
    }

}
