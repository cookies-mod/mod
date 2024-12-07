package codes.cookies.mod.features.misc;

import codes.cookies.mod.features.Loader;
import codes.cookies.mod.features.misc.items.ItemFeatures;
import codes.cookies.mod.features.misc.render.PingDisplay;
import codes.cookies.mod.features.misc.render.glowingmushroom.GlowingMushroomHighlights;
import codes.cookies.mod.features.misc.timer.NotificationManager;
import codes.cookies.mod.features.misc.utils.UtilsFeatures;

/**
 * Utility class to load all miscellaneous features.
 */
@SuppressWarnings("MissingJavadoc")
public interface MiscFeatures {

	static void load() {
        Loader.load("ItemFeatures", ItemFeatures::load);
        Loader.load("UtilityFeatures", UtilsFeatures::load);
        Loader.load("PingDisplay", PingDisplay::load);
		Loader.load("TimerFeatures", NotificationManager::register);
		Loader.load("GlowingMushroomHighlight", GlowingMushroomHighlights::register);
    }


}
