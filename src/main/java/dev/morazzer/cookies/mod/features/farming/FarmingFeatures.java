package dev.morazzer.cookies.mod.features.farming;

import dev.morazzer.cookies.mod.features.Loader;
import dev.morazzer.cookies.mod.features.farming.garden.GardenFeatures;
import dev.morazzer.cookies.mod.features.farming.inventory.RancherBootsNumbers;
import dev.morazzer.cookies.mod.features.farming.inventory.RancherBootsOverlay;
import dev.morazzer.cookies.mod.features.farming.jacob.HighlightUnclaimedJacobsContest;

/**
 * Utility class to load all farming related features.
 */
@SuppressWarnings("MissingJavadoc")
public interface FarmingFeatures {
	static void load() {
        Loader.load("HighlightUnclaimedJacobContest", HighlightUnclaimedJacobsContest::load);
        Loader.load("ShowSpeedOnRancherBoots", RancherBootsNumbers::new);
        Loader.load("RancherBootsOverlay", RancherBootsOverlay::new);
        Loader.load("GardenFeatures", GardenFeatures::load);
        Loader.load("YawPitchDisplay", YawPitchDisplay::register);
		Loader.load("PlotSprayTracker", PlotSprayTracker::load);
    }
}
