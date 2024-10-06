package dev.morazzer.cookies.mod.features.dungeons.map;

import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.CreeperBeamPuzzleSolver;
import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.HigherLowerPuzzleSolver;
import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.QuizPuzzleSolver;
import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.ThreeWeirdosPuzzleSolver;

import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.WaterBoardPuzzleSolver;
import dev.morazzer.cookies.mod.utils.dev.FunctionUtils;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;
import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.PuzzleSolver;
import lombok.Getter;

/**
 * Enum representing all supported puzzles.
 */
@Getter
public enum PuzzleType {

	QUIZ("Quiz", FunctionUtils.wrapOptionalSupplier(QuizPuzzleSolver::new)),
	TIC_TAC_TOE("Tic Tac Toe"),
	WEIRDOS("Three Weirdos", FunctionUtils.wrapOptionalSupplier(ThreeWeirdosPuzzleSolver::new)),
	ICE_PATH("Ice Path"),
	ICE_FILL("Ice Fill"),
	HIGHER_LOWER("Higher Or Lower", "Higher Lower", FunctionUtils.wrapOptionalSupplier(HigherLowerPuzzleSolver::new)),
	CREEPER("Creeper Beams", "Creeper", FunctionUtils.wrapOptionalSupplier(CreeperBeamPuzzleSolver::new)),
	WATERBOARD("Water Board", FunctionUtils.wrapOptionalF(WaterBoardPuzzleSolver::new)),
	BOULDER("Boulder"),
	MAZE("Teleport Maze", "Maze"),
	UNKNOWN("Unknown");

	PuzzleType(String name) {
		this(name, name);
	}

	PuzzleType(String tabName, String displayName) {
		this(tabName, displayName, Optional::empty);
	}

	private final String tabName;
	private final String displayName;
	private final Function<DungeonInstance, Optional<PuzzleSolver>> solverFunction;

	PuzzleType(String tabName, Supplier<Optional<PuzzleSolver>> supplier) {
		this(tabName, tabName, supplier);
	}

	PuzzleType(String tabName, String displayName, Supplier<Optional<PuzzleSolver>> supplier) {
		this(tabName, displayName, instance -> supplier.get());
	}

	PuzzleType(String tabName, Function<DungeonInstance, Optional<PuzzleSolver>> solverFunction) {
		this(tabName, tabName, solverFunction);
	}

	PuzzleType(String tabName, String displayName, Function<DungeonInstance, Optional<PuzzleSolver>> solverFunction) {
		this.tabName = tabName;
		this.displayName = displayName;
		this.solverFunction = solverFunction;
	}

	public static PuzzleType ofName(String puzzleName) {
		for (PuzzleType value : values()) {
			if (value.tabName.equals(puzzleName)) {
				return value;
			}
		}
		return UNKNOWN;
	}
}
