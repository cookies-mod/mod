package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;

import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

/**
 * Base class of all puzzle solvers.
 */
public abstract class PuzzleSolver {

	protected void onRoomEnter(DungeonRoom dungeonRoom) {}

	protected void onRoomExit() {}

	protected void resetPuzzle() {}

	public void onChatMessage(String message) {}

	public void tick() {}

	protected Optional<ClientWorld> getWorld() {
		return Optional.ofNullable(MinecraftClient.getInstance().world);
	}

}
