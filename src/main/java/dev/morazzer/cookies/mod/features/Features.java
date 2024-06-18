package dev.morazzer.cookies.mod.features;

import dev.morazzer.cookies.mod.features.farming.FarmingFeatures;
import dev.morazzer.cookies.mod.features.misc.MiscFeatures;

/**
 * Utility class to load all features.
 */
@SuppressWarnings("MissingJavadoc")
public class Features {

    public static void load() {
        Loader.load("MiscFeatures", MiscFeatures::load);
        Loader.load("FarmingFeatures", FarmingFeatures::load);
    }

}
