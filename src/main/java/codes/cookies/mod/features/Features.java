package codes.cookies.mod.features;

import codes.cookies.mod.features.cleanup.CleanupFeatures;
import codes.cookies.mod.features.dungeons.DungeonFeatures;
import codes.cookies.mod.features.farming.FarmingFeatures;
import codes.cookies.mod.features.mining.MiningFeatures;
import codes.cookies.mod.features.misc.MiscFeatures;
import codes.cookies.mod.features.search.MuseumHelper;

/**
 * Utility class to load all features.
 */
@SuppressWarnings("MissingJavadoc")
public interface 	Features {

    static void load() {
        Loader.load("MiscFeatures", MiscFeatures::load);
        Loader.load("FarmingFeatures", FarmingFeatures::load);
        Loader.load("CleanupFeatures", CleanupFeatures::load);
        Loader.load("MiningFeatures", MiningFeatures::load);
        Loader.load("DungeonFeatures", DungeonFeatures::new);
		Loader.load("MuseumItemSearch", MuseumHelper::init);
    }

}
