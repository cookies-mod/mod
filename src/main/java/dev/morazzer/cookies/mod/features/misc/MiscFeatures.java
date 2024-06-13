package dev.morazzer.cookies.mod.features.misc;

import dev.morazzer.cookies.mod.features.Loader;
import dev.morazzer.cookies.mod.features.misc.items.ItemFeatures;
import dev.morazzer.cookies.mod.features.misc.utils.CraftHelper;
import dev.morazzer.cookies.mod.features.misc.utils.UtilsFeatures;

/**
 * Utility class to load all miscellaneous features.
 */
@SuppressWarnings("MissingJavadoc")
public class MiscFeatures {

    public static void load() {
        Loader.load("ItemFeatures", ItemFeatures::load);
        Loader.load("UtilityFeatures", UtilsFeatures::load);
    }


}
