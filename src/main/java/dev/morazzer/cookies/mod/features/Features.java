package dev.morazzer.cookies.mod.features;

import dev.morazzer.cookies.mod.features.cleanup.CleanupFeatures;
import dev.morazzer.cookies.mod.features.dungeons.DungeonFeatures;
import dev.morazzer.cookies.mod.features.farming.FarmingFeatures;
import dev.morazzer.cookies.mod.features.mining.MiningFeatures;
import dev.morazzer.cookies.mod.features.misc.MiscFeatures;
import dev.morazzer.cookies.mod.features.search.MuseumHelper;

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
