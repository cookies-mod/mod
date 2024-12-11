package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.features.Loader;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager;

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
		Loader.load("StatsTracker", StatsTracker::init);
		Loader.load("ReforgeHelper", ReforgeHelper::init);
    }

}
