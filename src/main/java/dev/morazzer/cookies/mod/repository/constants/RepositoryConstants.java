package dev.morazzer.cookies.mod.repository.constants;

import dev.morazzer.cookies.mod.repository.constants.dungeons.DungeonConstants;

import java.nio.file.Path;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Utility class to load all constants.
 */
@SuppressWarnings("MissingJavadoc")
public class RepositoryConstants {

	public static Hotm hotm;
	public static PlotPrice plotPrice;
	public static ComposterUpgrades composterUpgrades;
	public static Warps warps;

	public static void load(Path path) {
		hotm = new Hotm(RepositoryConstantsHelper.resolve(path.resolve("hotm.json"), JsonObject.class));
		plotPrice = new PlotPrice(RepositoryConstantsHelper.resolve(path.resolve("plot_cost.json"), JsonObject.class));
		composterUpgrades = new ComposterUpgrades(RepositoryConstantsHelper.resolve(
				path.resolve("compost_upgrades.json"),
				JsonObject.class));
		warps = new Warps(RepositoryConstantsHelper.resolve(path.resolve("warps.json"), JsonArray.class));
		DungeonConstants.load(path.resolve("dungeons"));
	}

}
