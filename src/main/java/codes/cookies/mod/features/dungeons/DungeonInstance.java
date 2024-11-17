package codes.cookies.mod.features.dungeons;

import dev.morazzer.cookies.entities.websocket.Packet;
import dev.morazzer.cookies.entities.websocket.packets.DungeonJoinPacket;
import dev.morazzer.cookies.entities.websocket.packets.DungeonLeavePacket;
import dev.morazzer.cookies.entities.websocket.packets.DungeonSyncPlayerLocation;
import codes.cookies.mod.api.ws.WebsocketConnection;
import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.events.dungeon.DungeonEvents;
import codes.cookies.mod.features.dungeons.map.DungeonMap;
import codes.cookies.mod.features.dungeons.map.DungeonMapRenderer;
import codes.cookies.mod.features.dungeons.map.DungeonPhase;
import codes.cookies.mod.features.dungeons.map.DungeonRoom;
import codes.cookies.mod.features.dungeons.map.DungeonType;
import codes.cookies.mod.features.dungeons.map.PuzzleType;
import codes.cookies.mod.features.dungeons.solver.puzzle.PuzzleSolverInstance;
import codes.cookies.mod.features.dungeons.solver.puzzle.WaterBoardPuzzleSolver;
import codes.cookies.mod.utils.skyblock.PartyUtils;
import codes.cookies.mod.utils.skyblock.tab.PlayerListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

import java.util.regex.Pattern;

import lombok.Getter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.joml.Vector2i;

/**
 * This class represents a dungeon instance, this contains a set of immutable information about the dungeon. <br>
 * The dungeon instance is responsible for all actions that are within a dungeon, so everything that happens needs to
 * be invoked here or somewhere down the line from here.
 */
public final class DungeonInstance {
	private static final Pattern PLAYER_PATTERN =
			Pattern.compile("^\\[(\\d+)] (?:\\[\\w+] )?(\\w+) .?[♲Ⓑ ]? ?\\((\\w+)(?: (\\w+))?\\)$");
	private final DungeonType type;
	private final int floor;
	private final String serverId;
	@Getter
	private DungeonPlayer currentPlayer;
	@Getter
	private final DungeonPlayer[] players = new DungeonPlayer[5];
	private final Map<String, Integer> playerIdMap = new HashMap<>();
	private String playerNick;
	@Getter
	private DungeonPhase phase = DungeonPhase.BEFORE;
	@Getter
	private final DungeonMap dungeonMap;
	@Getter
	private DungeonMapRenderer mapRenderer;
	private final UUID partyLeader;
	private final boolean relayToBackend;
	@Getter
	private final boolean debugInstance;
	private String[] puzzles = null;
	@Getter
	private final List<Pair<PuzzleType, Integer>> knownPuzzles = new ArrayList<>();
	@Getter
	private long lastPuzzleUpdate = -1;
	@Getter
	private final PuzzleSolverInstance puzzleSolverInstance;
	@Getter
	private DungeonRoom currentRoom;
	@Getter
	private final long timeStarted;

	public DungeonInstance(DungeonType type, int floor, String serverId) {
		this.type = type;
		this.floor = floor;
		this.serverId = serverId;
		DungeonFeatures.sendDebugMessage("Created dungeon instance, %s:%s for server %s".formatted(type,
				floor,
				serverId));
		this.dungeonMap = new DungeonMap(this);
		this.partyLeader = PartyUtils.getPartyLeader();
		if (serverId.contains("cookies_internal_")) {
			this.relayToBackend = false;
			this.debugInstance = true;
		} else {
			this.debugInstance = false;
			this.relayToBackend = this.partyLeader != null && DungeonConfig.getInstance().relayToBackend.getValue();
		}
		this.timeStarted = System.currentTimeMillis();
		this.puzzleSolverInstance = new PuzzleSolverInstance(this);
	}

	/**
	 * @return The amount of rooms on the current floor.
	 */
	public int getRoomAmount() {
		return switch (this.floor) {
			case 0 -> 4;
			case 1, 2, 3 -> 5;
			case 4, 5, 6, 7 -> 6;
			default -> -1;
		};
	}

	public boolean isLastColumnPuzzlesOnly() {
		return switch (this.floor) {
			case 4,5,6 -> true;
			default -> false;
		};
	}

	public DungeonType type() {
		return this.type;
	}

	public int floor() {
		return this.floor;
	}

	public String serverId() {
		return this.serverId;
	}

	/**
	 * Unloads various features to save memory and also lets the backend know of the disconnect.
	 */
	public void unload() {
		this.send(new DungeonLeavePacket());
		DungeonFeatures.sendDebugMessage("Unloading %s".formatted(this.serverId));
		this.puzzleSolverInstance.unloadCurrent();
		this.mapRenderer = null;
	}

	/**
	 * Sends a packet to the backend if the player is not in a solo dungeon.
	 *
	 * @param packet The packet to send.
	 */
	public void send(Packet<?> packet) {
		if (!this.relayToBackend) {
			return;
		}
		WebsocketConnection.sendMessageAsync(packet);
	}

	/**
	 * Loads the instance as the current one, this will register the map renderer and also inform the backend about
	 * the dungeon join.
	 */
	public void load() {
		this.subscribe();
		this.mapRenderer = new DungeonMapRenderer(this);
	}

	/**
	 * Called when the instance is removed from the cache.
	 */
	public void destroy() {
		WaterBoardPuzzleSolver.LeverType.remove(this);
	}

	/**
	 * Processes the secrets for the current room.
	 *
	 * @param s The secret part of the action bar.
	 */
	public void processSecrets(String s) {
		this.dungeonMap.updateSecrets(s);
	}

	/**
	 * Syncs all players through the backend. <br>
	 * This will not sync (a player) if any of these conditions are meet.
	 * <ul>
	 *     <li>The dungeon hasn't started yet, or the party is in the boss fight.</li>
	 *     <li>The player uses the mod.</li>
	 *     <li>The player is out of render distance.</li>
	 * </ul>
	 */
	public void syncPlayers() {
		final ClientPlayerEntity player = this.getPlayer();
		final ClientWorld world = MinecraftClient.getInstance().world;
		if (player == null || world == null) {
			return;
		}
		if (this.phase == DungeonPhase.BEFORE || this.phase == DungeonPhase.BOSS || this.phase == DungeonPhase.AFTER) {
			return;
		}

		for (AbstractClientPlayerEntity worldPlayer : world.getPlayers()) {
			if (worldPlayer == null) {
				continue;
			}
			final String name = this.getName(worldPlayer);
			final DungeonPlayer dungeonPlayer = this.getPlayer(name);
			if (dungeonPlayer == null) {
				continue;
			}
			if (dungeonPlayer.isSkip()) {
				continue;
			}
			if (!dungeonPlayer.wasRecentlyUpdateLocal()) {
				continue;
			}
			if (dungeonPlayer.isUsingMod() && !dungeonPlayer.isSelf()) {
				continue;
			}

			this.send(new DungeonSyncPlayerLocation(dungeonPlayer.getName(),
					dungeonPlayer.getX(),
					dungeonPlayer.getY(),
					dungeonPlayer.getRotation().getTarget()));
		}
	}

	/**
	 * Called when the mod receives a player sync from the backend server.
	 *
	 * @param packet The packet with information about the player.
	 */
	public void updatePlayer(DungeonSyncPlayerLocation packet) {
		final DungeonPlayer player = this.getPlayer(packet.username);
		if (player == null) {
			return;
		}
		if (player.getLastSocketUpdate() > packet.timestamp) {
			return;
		}
		player.updatePositionSocket(packet.x, packet.y);
		player.updateRotationSocket(packet.rotation);
	}

	/**
	 * Subscribes to packets related to the current dungeon session.
	 */
	public void subscribe() {
		this.send(new DungeonJoinPacket(this.serverId, this.partyLeader));
	}

	/**
	 * Updates the list of puzzles based on the info in tab.
	 */
	public void updatePuzzles() {
		if (this.phase == DungeonPhase.BEFORE) {
			return;
		}
		final ClientPlayerEntity player = this.getPlayer();
		if (player == null) {
			return;
		}
		final Collection<PlayerListEntry> listedPlayerListEntries = player.networkHandler.getListedPlayerListEntries();
		final List<PlayerListEntry> list = listedPlayerListEntries.stream()
				.filter(PlayerListUtils.isInColumn(2))
				.sorted(Comparator.comparingInt(PlayerListUtils::getRow))
				.filter(entry -> PlayerListUtils.getRow(entry) >= 7 && PlayerListUtils.getRow(entry) <= 12)
				.toList();

		list.forEach(this::handlePuzzleEntry);
	}

	/**
	 * Attempts to load the puzzle solver for the given room.
	 * @param dungeonRoom The puzzle room.
	 */
	public void loadPuzzle(DungeonRoom dungeonRoom) {
		this.puzzleSolverInstance.onEnterPuzzleRoom(dungeonRoom);
	}

	/**
	 * Attempts to unload the puzzle solver for the given room.
	 * @param dungeonRoom The puzzle room.
	 */
	public void unloadPuzzle(DungeonRoom dungeonRoom) {
		this.puzzleSolverInstance.onExitRoom(dungeonRoom);
	}

	/**
	 * Swaps the current room and invokes puzzle solvers.
	 * @param lastMapPosition The current room.
	 */
	public void swapRoom(Vector2i lastMapPosition) {
		this.unloadPuzzle(this.currentRoom);
		this.currentRoom = this.dungeonMap.getRoomAt(lastMapPosition.x, lastMapPosition.y);
		this.loadPuzzle(this.currentRoom);
	}

	private void handlePuzzleEntry(PlayerListEntry playerListEntry) {
		final Text displayName = playerListEntry.getDisplayName();
		if (displayName == null) {
			return;
		}

		this.handlePuzzleEntry(displayName, PlayerListUtils.getRow(playerListEntry));
	}

	private void handlePuzzleEntry(Text text, int row) {
		String literal = text.getString().trim();
		if (literal.isEmpty() || literal.isBlank()) {
			return;
		}
		if (literal.startsWith("Puzzles")) {
			if (this.puzzles == null) {
				this.puzzles = new String[Integer.parseInt(literal.replaceAll("\\D", ""))];
			}
			return;
		}
		if (this.puzzles == null) {
			return;
		}
		final int index = row - 8;
		final String puzzleName = literal.substring(0, literal.lastIndexOf(":"));
		if (index >= this.puzzles.length) {
			return;
		}
		if (this.puzzles[index] == null) {
			this.puzzles[index] = puzzleName;
		}
		if (this.puzzles[index].equals(puzzleName)) {
			return;
		}
		this.puzzles[index] = puzzleName;
		this.knownPuzzles.removeIf(pair -> pair.getRight() == index);
		this.knownPuzzles.add(new Pair<>(PuzzleType.ofName(puzzleName), index));
		this.knownPuzzles.sort(Comparator.comparingInt(Pair::getRight));
		this.lastPuzzleUpdate = System.currentTimeMillis();
	}

	/**
	 * Called every 5 ingame ticks, this should be used to perform various performance intensive methods.
	 */
	public void periodicalTicks5() {
		if (this.getPhase() == DungeonPhase.BLOOD &&
			(this.getPlayerPosition().x > -200+(32 * dungeonMap.getRoomsInX()) || this.getPlayerPosition().z > -200+(32 * dungeonMap.getRoomsInY()))) {
			this.setPhase(DungeonPhase.BOSS);
			return;
		}

		if (this.getPhase() == DungeonPhase.BEFORE || this.getPhase() == DungeonPhase.CLEAR ||
			this.getPhase() == DungeonPhase.BLOOD) {
			this.dungeonMap.updateMap();
		}
	}

	/**
	 * @return The current player position, or {@link Vec3d#ZERO} in case the player is null.
	 */
	public Vec3d getPlayerPosition() {
		if (this.getPlayer() == null) {
			return Vec3d.ZERO;
		}
		return this.getPlayer().getPos();
	}

	/**
	 * @return The player.
	 */
	@Nullable
	public ClientPlayerEntity getPlayer() {
		return MinecraftClient.getInstance().player;
	}

	/**
	 * @param clientPlayer The player to the get name of.
	 * @return The name of the provided player.
	 */
	private String getName(AbstractClientPlayerEntity clientPlayer) {
		if (clientPlayer == null) {
			return "?";
		}

		return clientPlayer.getName().getString();
	}

	/**
	 * Update the players from the client world, this has the highest priority and will be used whenever possible.
	 */
	public void updatePlayersFromWorld() {
		for (DungeonPlayer player : this.players) {
			if (player == null) {
				continue;
			}
			if (MinecraftClient.getInstance().world == null) {
				return;
			}
			if (!MinecraftClient.getInstance().world.getPlayers().contains(player.getPlayer())) {
				continue;
			}
			if (player.getPlayer() == null) {
				continue;
			}

			player.updatePositionLocal((int) player.getPlayer().getX(), (int) player.getPlayer().getZ());
			player.updateRotationLocal(player.getPlayer().getHeadYaw());
		}
	}

	/**
	 * Update all players according to the tab list.
	 */
	public void updatePlayers() {
		final ClientPlayerEntity player = this.getPlayer();
		if (player == null) {
			return;
		}
		final Collection<PlayerListEntry> listedPlayerListEntries = player.networkHandler.getListedPlayerListEntries();
		final List<PlayerListEntry> list = listedPlayerListEntries.stream()
				.filter(PlayerListUtils.isInColumn(0))
				.sorted(Comparator.comparingInt(PlayerListUtils::getRow))
				.toList();

		if (list.isEmpty()) {
			return;
		}

		int index = 0;

		for (PlayerListEntry playerListEntry : list) {
			final Text displayName = playerListEntry.getDisplayName();
			if (displayName == null) {
				continue;
			}
			final String listEntry = displayName.getString();

			if (!listEntry.startsWith("[") || !listEntry.matches("\\[\\d+].*")) {
				continue;
			}

			final Matcher matcher = PLAYER_PATTERN.matcher(listEntry);
			if (!matcher.find()) {
				continue;
			}

			String name = matcher.group(2);
			String clazz = matcher.group(3);
			String clazzLevel = matcher.group(4);


			if (this.getPlayer().getName().getString().equals(name)) {
				if (this.currentPlayer == null) {
					this.currentPlayer = new DungeonPlayer(this, name, clazz, clazzLevel);
				}
				this.currentPlayer.setPlayerListEntry(playerListEntry);
				this.currentPlayer.update(name, clazz, clazzLevel);
				continue;
			}

			if (this.players[index] == null || this.players[index] == this.currentPlayer) {
				this.players[index] = new DungeonPlayer(this, name, clazz, clazzLevel);
			}
			this.players[index].setPlayerListEntry(playerListEntry);
			this.playerIdMap.put(name, index);
			this.players[index].update(name, clazz, clazzLevel);
			index++;
		}

		if (this.currentPlayer != null) {
			if (this.players[index] == null || this.players[index] != this.currentPlayer) {
				this.players[index] = this.currentPlayer;
			}
			this.playerIdMap.put(this.currentPlayer.getName(), index);
		} else if (this.playerNick == null) {
			for (PlayerListEntry playerListEntry : player.networkHandler.getPlayerList()) {
				if (playerListEntry.getProfile().getId().equals(player.getUuid())) {
					this.playerNick =
							Optional.ofNullable(playerListEntry.getDisplayName()).map(Text::getString).orElse(null);
					break;
				}
			}
		}
	}

	/**
	 * @param index The index to get.
	 * @return Gets the player at the specified index, the client will always be the last index.
	 */
	@Nullable
	public DungeonPlayer getPlayer(final int index) {
		if (this.players.length <= index) {
			return null;
		}
		return this.players[index];
	}

	/**
	 * @param name The name to get.
	 * @return The dungeon player associated with the provided name.
	 */
	@Nullable
	public DungeonPlayer getPlayer(final String name) {
		final int playerIndex = this.getPlayerIndex(name);
		if (playerIndex == -1) {
			return null;
		}
		return this.players[playerIndex];
	}

	/**
	 * Applies an offset to the index if a player was marked as skip (dead).
	 *
	 * @param start The current index.
	 * @return The new index.
	 */
	public int applyOffset(@Range(from = 0, to = 4) int start) {
		if (start < this.players.length - 1 && this.players[start] != null && this.players[start].isSkip()) {
			return this.applyOffset(start + 1);
		}
		return start;
	}

	/**
	 * @param playerName The name to get the index of.
	 * @return The index of the provided name or -1 if no index is present.
	 */
	private int getPlayerIndex(String playerName) {
		return this.playerIdMap.getOrDefault(playerName, -1);
	}

	/**
	 * Sets the new phase and invokes the phase change event.
	 *
	 * @param phase The phase to change to.
	 */
	public void setPhase(DungeonPhase phase) {
		DungeonEvents.DUNGEON_PHASE_CHANGE.invoker().accept(phase);
		DungeonFeatures.sendDebugMessage("Change phase to " + phase);
		this.phase = phase;
	}


}
