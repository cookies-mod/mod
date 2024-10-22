package dev.morazzer.cookies.mod.features.mining;

import dev.morazzer.cookies.mod.features.Loader;
import dev.morazzer.cookies.mod.features.mining.commissions.CommissionCompletionHighlighter;
import dev.morazzer.cookies.mod.features.mining.hollows.MinesOfDivanHelper;
import dev.morazzer.cookies.mod.features.mining.utils.HotmUtils;

/**
 * Utility class to load all mining features.
 */
@SuppressWarnings("MissingJavadoc")
public interface MiningFeatures {

    static void load() {
        Loader.load("HotmUtils", HotmUtils::new);
        Loader.load("CommissionCompletionHighlighter", CommissionCompletionHighlighter::new);
        Loader.load("PuzzlerSolver", PuzzlerSolver::new);
		Loader.load("ForgeFeatures", ForgeFeatures::init);
		Loader.load("MinesOfDivanHelper", MinesOfDivanHelper::init);
    }

}
