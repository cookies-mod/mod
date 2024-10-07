package dev.morazzer.cookies.mod.repository.constants.dungeons;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.WaterBoardPuzzleSolver;
import dev.morazzer.cookies.mod.repository.constants.RepositoryConstantsHelper;

public class DungeonConstants {

	public static List<WaterEntry> waterEntries;
	public static Map<String, List<String>> quizAnswers;

	private static final Codec<Map<String, List<String>>> QUIZ_CODEC =
			Codec.dispatchedMap(Codec.STRING, s -> Codec.STRING.listOf());

	public static void load(Path path) {
		waterEntries =
				WaterEntry.load(RepositoryConstantsHelper.resolve(path.resolve("water_times.json"), JsonArray.class));
		quizAnswers =
				loadQuizAnswers(RepositoryConstantsHelper.resolve(path.resolve("quiz_answers.json"),
						JsonObject.class));
	}

	private static Map<String, List<String>> loadQuizAnswers(JsonObject json) {
		final DataResult<Map<String, List<String>>> parse = QUIZ_CODEC.parse(JsonOps.INSTANCE, json);
		if (parse.isError()) {
			RepositoryConstantsHelper.LOGGER.warn(
					"Failed to load quiz answers D: {}",
					parse.error().map(DataResult.Error::message).orElse("<no message>"));
			return Collections.emptyMap();
		}
		return parse.getOrThrow();
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
