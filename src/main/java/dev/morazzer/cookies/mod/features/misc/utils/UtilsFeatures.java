package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.features.Loader;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager;

/**
 * Utility class to load all utility features.
 */
@SuppressWarnings("MissingJavadoc")
public class UtilsFeatures {

    public static void load() {
        Loader.load("CraftHelper", CraftHelperManager::init);
        Loader.load("ModifiedRecipeScreen", ModifyRecipeScreen::new);
        Loader.load("StoragePreview", StoragePreview::new);
        Loader.load("AnvilHelper", AnvilHelper::new);
        Loader.load("ForgeRecipes", ForgeRecipes::new);
    }

}
