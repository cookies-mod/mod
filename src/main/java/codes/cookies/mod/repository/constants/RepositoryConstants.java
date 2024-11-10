package codes.cookies.mod.repository.constants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import codes.cookies.mod.repository.constants.dungeons.DungeonConstants;

import net.minecraft.util.math.BlockPos;

/**
 * Utility class to load all constants.
 */
@SuppressWarnings("MissingJavadoc")
public class RepositoryConstants {

	public static Hotm hotm;
	public static PlotPrice plotPrice;
	public static ComposterUpgrades composterUpgrades;
	public static Warps warps;
	public static List<BlockPos> modLocations;
	public static MuseumData museumData;

	public static void load(Path path) {
		hotm = new Hotm(RepositoryConstantsHelper.resolve(path.resolve("hotm.json"), JsonObject.class));
		plotPrice = new PlotPrice(RepositoryConstantsHelper.resolve(path.resolve("plot_cost.json"), JsonObject.class));
		composterUpgrades =
				new ComposterUpgrades(RepositoryConstantsHelper.resolve(path.resolve("compost_upgrades.json"),
						JsonObject.class));
		warps = new Warps(RepositoryConstantsHelper.resolve(path.resolve("warps.json"), JsonArray.class));
		modLocations = loadModLocations(RepositoryConstantsHelper.resolve(path.resolve("mod_locations.json"),
            JsonArray.class));
		museumData = MuseumData.load(RepositoryConstantsHelper.resolve(path.resolve("museum_data.json"),
				JsonObject.class));
		DungeonConstants.load(path.resolve("dungeons"));
	}

	private static List<BlockPos> loadModLocations(JsonArray json) {
		final DataResult<List<BlockPos>> parse = BlockPos.CODEC.listOf().parse(JsonOps.INSTANCE, json);
		if (parse.isError()) {
			RepositoryConstantsHelper.LOGGER.warn("Failed to load mod locations D: {}",
					parse.error().map(DataResult.Error::message).orElse("<no message>"));
			return Collections.emptyList();
		}
		return new ArrayList<>(parse.getOrThrow());
	}
}
