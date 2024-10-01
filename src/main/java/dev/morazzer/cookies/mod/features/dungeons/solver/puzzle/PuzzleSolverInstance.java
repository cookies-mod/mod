package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;
import dev.morazzer.cookies.mod.features.dungeons.map.PuzzleType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The manager for all puzzle solvers.
 */
public class PuzzleSolverInstance {

	static final Logger LOGGER = LoggerFactory.getLogger(PuzzleSolverInstance.class);
	private static final List<Function<DungeonInstance, PuzzleSolver>> SOLVERS = Collections.emptyList();

	private final Map<PuzzleType, PuzzleSolver> solverMap = new HashMap<>();

	public PuzzleSolverInstance(DungeonInstance dungeonInstance) {
		for (Function<DungeonInstance, PuzzleSolver> solver : SOLVERS) {
			final PuzzleSolver apply = solver.apply(dungeonInstance);
			if (this.solverMap.containsKey(apply.getType())) {
				LOGGER.warn("Duplicate solver for puzzle type {}", apply.getType());
			}
			this.solverMap.put(apply.getType(), apply);
		}
	}

	public void onEnterPuzzleRoom(DungeonRoom dungeonRoom) {
		this.getSolver(dungeonRoom).ifPresent(PuzzleSolver::enterRoom);
	}

	public void onExitRoom(DungeonRoom dungeonRoom) {
		this.getSolver(dungeonRoom).ifPresent(PuzzleSolver::exitRoom);
	}

	public Optional<PuzzleSolver> getSolver(PuzzleType puzzleType) {
		final Optional<PuzzleSolver> puzzleSolver = Optional.ofNullable(this.solverMap.get(puzzleType));
		if (puzzleSolver.isEmpty()) {
			LOGGER.warn("No solver found for type {}", puzzleType);
		}
		return puzzleSolver;
	}

	public Optional<PuzzleSolver> getSolver(DungeonRoom dungeonRoom) {
		return Optional.ofNullable(dungeonRoom).map(DungeonRoom::getPuzzleType).flatMap(this::getSolver);
	}
}
