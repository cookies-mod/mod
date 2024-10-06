package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;
import dev.morazzer.cookies.mod.render.Renderable;
import dev.morazzer.cookies.mod.render.WorldRender;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

/**
 * Base class of all puzzle solvers.
 */
public abstract class PuzzleSolver {
	protected static final Identifier DEBUG = DevUtils.createIdentifier("dungeon/puzzles/debug");
	private List<Renderable> debugRenderables = null;
	private final List<Renderable> renderables = new ArrayList<>();
	boolean isLoaded = false;

	public PuzzleSolver() {
		if (isDebugEnabled()) {
			this.debugRenderables = new ArrayList<>();
		}
	}

	public void beforeRender(float tickDelta) {}

	public void onInteract(World world, BlockHitResult blockHitResult, Hand hand) {}

	public void onUnloadedChatMessage(String string) {}

	protected void addRenderable(Renderable renderable) {
		this.renderables.add(renderable);
		if (this.isLoaded) {
			WorldRender.addRenderable(renderable);
		}
	}

	protected void addDebugRenderable(Renderable renderable) {
		if (this.debugRenderables == null) {
			if (!isDebugEnabled()) {
				return;
			}
			this.debugRenderables = new ArrayList<>();
		}
		this.debugRenderables.add(renderable);
		WorldRender.addRenderable(renderable);
	}

	protected static boolean isDebugEnabled() {
		return DevUtils.isEnabled(DEBUG);
	}

	protected void clearDebugRenderables() {
		if (this.debugRenderables == null) {
			return;
		}
		this.debugRenderables.forEach(WorldRender::removeRenderable);
		this.debugRenderables.clear();
	}

	protected void clearRenderables() {
		this.removeRenderables();
		this.renderables.clear();
	}

	protected void removeRenderables() {
		this.renderables.forEach(WorldRender::removeRenderable);
	}

	@MustBeInvokedByOverriders
	protected void onRoomEnter(DungeonRoom dungeonRoom) {
		this.isLoaded = true;
		this.renderables.forEach(WorldRender::addRenderable);
	}

	@MustBeInvokedByOverriders
	protected void onRoomExit() {
		this.isLoaded = false;
		this.removeRenderables();
		this.clearDebugRenderables();
	}

	protected void resetPuzzle() {
		this.clearRenderables();
		this.clearDebugRenderables();
	}

	public void onChatMessage(String message) {}

	public void tick() {}

	protected Optional<ClientWorld> getWorld() {
		return Optional.ofNullable(MinecraftClient.getInstance().world);
	}

}
