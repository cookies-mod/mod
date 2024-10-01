package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import dev.morazzer.cookies.mod.features.dungeons.map.PuzzleType;

/**
 * Base class of all puzzle solvers.
 */
public abstract class PuzzleSolver {

	protected abstract PuzzleType getType();

	protected void enterRoom() {}

	protected void exitRoom() {}

	protected void resetPuzzle() {}

	public void onChatMessage(String message) {}

}
