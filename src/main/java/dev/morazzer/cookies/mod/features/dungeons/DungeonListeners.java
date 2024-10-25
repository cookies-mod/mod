package dev.morazzer.cookies.mod.features.dungeons;

import java.util.Optional;

import dev.morazzer.cookies.entities.websocket.Packet;
import dev.morazzer.cookies.entities.websocket.packets.DungeonSyncPlayerLocation;
import dev.morazzer.cookies.entities.websocket.packets.DungeonUpdateRoomSecrets;
import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.config.data.HudElementPosition;
import dev.morazzer.cookies.mod.events.ChatMessageEvents;
import dev.morazzer.cookies.mod.events.IslandChangeEvent;
import dev.morazzer.cookies.mod.events.WebsocketEvent;
import dev.morazzer.cookies.mod.features.dungeons.map.DungeonMapRenderer;
import dev.morazzer.cookies.mod.features.dungeons.map.DungeonPhase;
import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;
import dev.morazzer.cookies.mod.features.dungeons.solver.puzzle.PuzzleSolver;
import dev.morazzer.cookies.mod.screen.DungeonMapRepositionScreen;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.FunctionUtils;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

/**
 * All listeners that are used for dungeon features, these will always be invoked on the currently active dungeon
 * instance. So this is more or less a type of middleware.
 */
public class DungeonListeners {

	private static int ticks = 0;

	/**
	 * Registers all listeners.
	 */
	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(DungeonListeners::clientTick);
		HudRenderCallback.EVENT.register(DungeonListeners::hudRenderCallback);
		IslandChangeEvent.EVENT.register(DungeonListeners::onIslandChange);
		Packet.onReceive(DungeonSyncPlayerLocation.class, DungeonListeners::syncPlayerLocation);
		Packet.onReceive(DungeonUpdateRoomSecrets.class, DungeonListeners::updateRoomSecrets);
		ChatMessageEvents.BEFORE_MODIFY.register(DungeonListeners::receiveGameMessage);
		WebsocketEvent.CONNECT.register(DungeonListeners::connectWebsocket);
		WorldRenderEvents.BEFORE_ENTITIES.register(DungeonListeners::beforeEntities);
		UseBlockCallback.EVENT.register(DungeonListeners::rightClickBlock);
	}

	private static void clientTick(MinecraftClient minecraftClient) {
		getInstance().ifPresent(instance -> {
			ticks++;
			if (ticks % 20 == 0) {
				ticks = 0;
			}

			instance.updatePlayers();
			instance.updatePuzzles();
			if (ticks % 5 == 0) {
				instance.periodicalTicks5();
			}
			if (ticks % 2 == 0) {
				instance.syncPlayers();
			}

			instance.getPuzzleSolverInstance().getCurrent().ifPresent(PuzzleSolver::tick);
		});
	}

	private static void hudRenderCallback(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		getInstance().ifPresent(instance -> {
			instance.updatePlayersFromWorld();
			final DungeonMapRenderer mapRenderer = instance.getMapRenderer();
			if (mapRenderer == null) {
				return;
			}
			if (MinecraftClient.getInstance().currentScreen instanceof DungeonMapRepositionScreen ||
                SpiritLeapOverlay.isOpen) {
				return;
			}
			final HudElementPosition position = DungeonConfig.getInstance().hudElementPosition;
			final int size = 6 * DungeonMapRenderer.TOTAL_SIZE - DungeonMapRenderer.HALLWAY_SIZE;
			drawContext.getMatrices().push();
			drawContext.getMatrices().translate(
					position.clampX(size) * MinecraftClient.getInstance().getWindow().getScaledWidth(),
					position.clampY(size) * MinecraftClient.getInstance().getWindow().getScaledHeight(),
					1000);
			drawContext.getMatrices().scale(position.scale, position.scale, 1);
			mapRenderer.render(drawContext);
			drawContext.getMatrices().pop();
		});
	}

	private static void onIslandChange(LocationUtils.Island previous, LocationUtils.Island current) {
		if (current == LocationUtils.Island.CATACOMBS) {
			DungeonFeatures.getInstance().startDungeon(LocationUtils.getLastServer());
		} else {
			DungeonFeatures.getInstance().exitDungeon();
		}
	}

	private static void syncPlayerLocation(DungeonSyncPlayerLocation packet) {
		getInstance().ifPresent(instance -> instance.updatePlayer(packet));
	}

	private static void updateRoomSecrets(DungeonUpdateRoomSecrets packet) {
		getInstance().ifPresent(instance -> {
			final DungeonRoom roomAt = instance.getDungeonMap().getRoomAt(packet.roomMapX, packet.roomMapY);
			if (roomAt != null) {
				if (roomAt.getMaxSecrets() < packet.maxSecrets) {
					roomAt.setMaxSecrets(packet.maxSecrets);
				}
				roomAt.setCollectedSecrets(packet.collectedSecrets);
			}
		});
	}

	private static void receiveGameMessage(Text text, boolean isOverlay) {
		getInstance().ifPresent(instance -> {
			final String string = CookiesUtils.stripColor(text.getString()).trim();
			if (isOverlay) {
				if (!string.endsWith("Secrets")) {
					instance.processSecrets(null);
					return;
				}
				final String s = string.replaceAll(".* (\\d+/\\d+ Secrets).*", "$1");
				final String[] split = s.split(" ");
				instance.processSecrets(split[0]);
			} else {
				if (string.contains("> EXTRA STATS <")) {
					instance.setPhase(DungeonPhase.AFTER);
				}

				instance.getPuzzleSolverInstance()
						.getCurrent()
						.map(FunctionUtils.function(PuzzleSolver::onChatMessage))
						.orElseGet(FunctionUtils::noOp)
						.accept(string);
				instance.getPuzzleSolverInstance()
						.getAll()
						.forEach(puzzleSolver -> puzzleSolver.onUnloadedChatMessage(string));
			}
		});
	}

	private static void connectWebsocket() {
		getInstance().ifPresent(DungeonInstance::subscribe);
	}

	private static void beforeEntities(WorldRenderContext worldRenderContext) {
		getInstance().ifPresent(instance -> {
			worldRenderContext.tickCounter().getTickDelta(false);

			instance.getPuzzleSolverInstance()
					.getCurrent()
					.map(FunctionUtils.function(PuzzleSolver::beforeRender))
					.orElseGet(FunctionUtils::noOp)
					.accept(worldRenderContext.tickCounter().getTickDelta(false));
		});
	}

	private static ActionResult rightClickBlock(
			PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
		getInstance().ifPresent(instance -> instance.getPuzzleSolverInstance()
				.getCurrent()
				.map(FunctionUtils.function(PuzzleSolver::onInteract))
				.orElseGet(FunctionUtils::noOp3)
				.accept(world, blockHitResult, hand));
		return ActionResult.PASS;
	}


	/**
	 * @return The current dungeon instance or null if empty.
	 */
	private static Optional<DungeonInstance> getInstance() {
		return DungeonFeatures.getInstance().getCurrentInstance();
	}
}
