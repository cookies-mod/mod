package dev.morazzer.cookies.mod.features.farming.garden;

import dev.morazzer.cookies.mod.features.Loader;

/**
 * Utility class to load all garden features.
 */
public class GardenFeatures {

    @SuppressWarnings("MissingJavadoc")
    public static void load() {
        Loader.load("PlotPriceBreakdown", PlotPriceBreakdown::new);
        Loader.load("CompostUpgrades", CompostUpgrades::new);
        //Loader.load("VisitorHelper", VisitorHelper::new);
    }

}
