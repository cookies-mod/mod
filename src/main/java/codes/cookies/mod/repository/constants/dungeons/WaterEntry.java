package codes.cookies.mod.repository.constants.dungeons;

import com.google.gson.JsonArray;
import com.mojang.serialization.DataResult;

import codes.cookies.mod.repository.constants.RepositoryConstantsHelper;

import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import codes.cookies.mod.features.dungeons.solver.puzzle.WaterBoardPuzzleSolver;
import org.jetbrains.annotations.Nullable;

/**
 * Entry in the water board solver list.
 * @param variant The variant of the room.
 * @param closed The closed doors.
 * @param times The times to flick levers at.
 */
public record WaterEntry(
		WaterBoardPuzzleSolver.Variant variant, String closed,
    Map<WaterBoardPuzzleSolver.LeverType, List<Double>> times
) {
	public static final Codec<WaterEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			WaterBoardPuzzleSolver.Variant.CODEC.fieldOf("variant").forGetter(WaterEntry::variant),
			Codec.STRING.fieldOf("type").forGetter(WaterEntry::closed),
			Codec.dispatchedMap(WaterBoardPuzzleSolver.LeverType.CODEC, leverType -> Codec.DOUBLE.listOf())
					.fieldOf("times")
					.forGetter(WaterEntry::times)).apply(instance, WaterEntry::new));
	public static final Codec<List<WaterEntry>> LIST_CODEC = CODEC.listOf();

	public static List<WaterEntry> load(@Nullable JsonArray resolve) {
		final DataResult<List<WaterEntry>> parse = LIST_CODEC.parse(JsonOps.INSTANCE, resolve);
		if (parse.isError()) {
			RepositoryConstantsHelper.LOGGER.warn("Failed to load water times D: {}", parse.error().map(DataResult.Error::message).orElse("<no message>"));
			return List.of();
		}
		return parse.getOrThrow();
	}

}
