package dev.morazzer.cookies.mod.repository.constants.dungeons;

import com.google.gson.JsonArray;
import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.WaterBoardPuzzleSolver;

import java.nio.file.Path;
import java.util.List;

import dev.morazzer.cookies.mod.repository.constants.RepositoryConstantsHelper;

import java.util.Optional;

public class DungeonConstants {

	public static List<WaterEntry> waterEntries;

	public static void load(Path path) {
		waterEntries =
				WaterEntry.load(RepositoryConstantsHelper.resolve(path.resolve("water_times.json"), JsonArray.class));
	}

	public static Optional<WaterEntry> getFor(WaterBoardPuzzleSolver.Variant variant, String type) {
		for (WaterEntry entry : waterEntries) {
			if (entry.variant().equals(variant) && entry.closed().equals(type)) {
				return Optional.of(entry);
			}
		}
		return Optional.empty();
	}
}
