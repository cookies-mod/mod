package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.features.Loader;

/**
 * Utility class to load all utility features.
 */
@SuppressWarnings("MissingJavadoc")
public class UtilsFeatures {


    public static void load() {
        Loader.load("CraftHelper", CraftHelper::new);
        Loader.load("ModifiedRecipeScreen", ModifyRecipeScreen::new);
    }


}
