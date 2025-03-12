package codes.cookies.mod.features.mining;

import codes.cookies.mod.features.Loader;
import codes.cookies.mod.features.mining.commissions.CommissionCompletionHighlighter;
import codes.cookies.mod.features.mining.hollows.MinesOfDivanHelper;
import codes.cookies.mod.features.mining.shafts.CorpseWaypoints;
import codes.cookies.mod.features.mining.shafts.MineshaftAnnouncements;
import codes.cookies.mod.features.mining.shafts.ShaftFeatures;
import codes.cookies.mod.features.mining.utils.GlossyGemstoneMessage;
import codes.cookies.mod.features.mining.utils.HotmUtils;

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
		Loader.load("CorpseWaypoints", CorpseWaypoints::register);
		Loader.load("PowderTracker", PowderTracker::load);
		Loader.load("ShaftFeatures", ShaftFeatures::load);
		Loader.load("MineshaftCorpseMessages", MineshaftAnnouncements::register);
		Loader.load("GlossyGemstoneMessage", GlossyGemstoneMessage::register);
    }

}
