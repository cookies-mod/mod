package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;
import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;
import dev.morazzer.cookies.mod.features.dungeons.map.PuzzleType;
import dev.morazzer.cookies.mod.utils.dev.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The manager for all puzzle solvers.
 */
public class PuzzleSolverInstance {

	static final Logger LOGGER = LoggerFactory.getLogger(PuzzleSolverInstance.class);

	private final Map<PuzzleType, PuzzleSolver> solverMap = new HashMap<>();
	private PuzzleSolver current;

	public PuzzleSolverInstance(DungeonInstance dungeonInstance) {
		for (PuzzleType puzzleType : PuzzleType.values()) {
			final Optional<PuzzleSolver> apply = puzzleType.getSolverFunction().apply(dungeonInstance);
			if (apply.isEmpty()) {
				LOGGER.warn("No logger present for {}", puzzleType);
				continue;
			}
			final PuzzleSolver puzzleSolver = apply.get();
			if (this.solverMap.containsKey(puzzleType)) {
				LOGGER.warn("Duplicate solver for puzzle type {}", puzzleType);
			}
			this.solverMap.put(puzzleType, puzzleSolver);
		}
	}

	public void onEnterPuzzleRoom(DungeonRoom dungeonRoom) {
		this.getSolver(dungeonRoom)
				.map(FunctionUtils.function(this::enter))
				.orElseGet(FunctionUtils::noOp)
				.accept(dungeonRoom);
	}

	public Optional<PuzzleSolver> getSolver(DungeonRoom dungeonRoom) {
		return Optional.ofNullable(dungeonRoom).map(DungeonRoom::getPuzzleType).flatMap(this::getSolver);
	}

	private void enter(PuzzleSolver puzzleSolver, DungeonRoom dungeonRoom) {
		this.current = puzzleSolver;
		puzzleSolver.onRoomEnter(dungeonRoom);
	}

	public Optional<PuzzleSolver> getSolver(PuzzleType puzzleType) {
		final Optional<PuzzleSolver> puzzleSolver = Optional.ofNullable(this.solverMap.get(puzzleType));
		if (puzzleSolver.isEmpty()) {
			LOGGER.warn("No solver found for type {}", puzzleType);
		}
		return puzzleSolver;
	}

	public void onExitRoom(DungeonRoom dungeonRoom) {
		this.getSolver(dungeonRoom).ifPresent(this::exit);
	}

	private void exit(PuzzleSolver puzzleSolver) {
		this.current = null;
		puzzleSolver.onRoomExit();
	}

	public Optional<PuzzleSolver> getCurrent() {
		return Optional.ofNullable(this.current);
	}
}
