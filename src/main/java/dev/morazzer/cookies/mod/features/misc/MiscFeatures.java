package dev.morazzer.cookies.mod.features.misc;

import dev.morazzer.cookies.mod.features.Loader;
import dev.morazzer.cookies.mod.features.misc.items.ItemFeatures;
import dev.morazzer.cookies.mod.features.misc.render.PingDisplay;
import dev.morazzer.cookies.mod.features.misc.timer.NotificationManager;
import dev.morazzer.cookies.mod.features.misc.utils.UtilsFeatures;

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
    }


}
