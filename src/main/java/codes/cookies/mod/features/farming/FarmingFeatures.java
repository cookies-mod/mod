package codes.cookies.mod.features.farming;

import codes.cookies.mod.features.Loader;
import codes.cookies.mod.features.farming.garden.GardenFeatures;
import codes.cookies.mod.features.farming.inventory.RancherBootsNumbers;
import codes.cookies.mod.features.farming.inventory.RancherBootsOverlay;
import codes.cookies.mod.features.farming.inventory.squeakymousemat.SqueakyMousematOverlay;
import codes.cookies.mod.features.farming.jacob.HighlightUnclaimedJacobsContest;

/**
 * Utility class to load all farming related features.
 */
@SuppressWarnings("MissingJavadoc")
public interface FarmingFeatures {
	static void load() {
        Loader.load("HighlightUnclaimedJacobContest", HighlightUnclaimedJacobsContest::load);
        Loader.load("ShowSpeedOnRancherBoots", RancherBootsNumbers::new);
        Loader.load("RancherBootsOverlay", RancherBootsOverlay::new);
        Loader.load("SqueakyMousematOverlay", SqueakyMousematOverlay::new);
        Loader.load("GardenFeatures", GardenFeatures::load);
        Loader.load("YawPitchDisplay", YawPitchDisplay::register);
		Loader.load("PlotSprayTracker", PlotSprayTracker::load);
    }
}
