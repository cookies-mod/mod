package codes.cookies.mod.features.farming.garden;

import codes.cookies.mod.features.Loader;
import codes.cookies.mod.features.farming.garden.visitors.VisitorDropProtection;
/**
 * Utility class to load all garden features.
 */
public class GardenFeatures {

    @SuppressWarnings("MissingJavadoc")
    public static void load() {
        Loader.load("PlotPriceBreakdown", PlotPriceBreakdown::new);
        Loader.load("CompostUpgrades", CompostUpgrades::new);
        //Loader.load("VisitorHelper", VisitorHelper::new);
		Loader.load("VisitorDropProtection", VisitorDropProtection::init);
    }

}
