package dev.morazzer.cookies.mod.features.dungeons.map;

import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;
import dev.morazzer.cookies.mod.features.dungeons.DungeonPlayer;
import dev.morazzer.cookies.mod.features.dungeons.DungeonPosition;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

import lombok.Setter;

import net.minecraft.block.MapColor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

/**
 * The actual dungeon map, and all data that is needed to display it.
 */
public class DungeonMap {
	private static final Identifier ADD_DEBUG_LINES_ON_MAP = DevUtils.createIdentifier("dungeon/debug_lines_on_map");

	private final DungeonInstance instance;
	private int mapId = -1;
	@Getter
	@Setter
	private Vector2i topLeftPixel;
	private int roomSize;
	private int halfRoomSize;
	private int doorRoomSize;
	private final DungeonRoom[][] roomMap;
	@Getter
	@Setter
	private int roomsInX = 0;
	@Getter
	@Setter
	private int roomsInY = 0;
	@Getter
	private final Set<DungeonDoor> doors = new HashSet<>();
	@Getter
	@Setter
	private Vector2i bottomRightPixel;
	private Vector2i lastMapPosition;
	private long lastRoomSwitch;
	private final Set<DungeonRoom> puzzles = new LinkedHashSet<>();

	public DungeonMap(DungeonInstance instance) {
		this.instance = instance;
		this.roomMap = new DungeonRoom[this.instance.getRoomAmount()][this.instance.getRoomAmount()];
	}

	/**
	 * Update the saved map id.
	 */
	@Contract
	public void updateMap() {
		final ClientPlayerEntity player = this.instance.getPlayer();
		if (player == null) {
			return;
		}
		final ItemStack stack = player.getInventory().getStack(8);
		if (stack.isEmpty()) {
			return;
		}

		if (!stack.isOf(Items.FILLED_MAP) && this.mapId != -1) {
			this.discoverMapInfo();
			return;
		}

		final MapIdComponent mapIdComponent = stack.get(DataComponentTypes.MAP_ID);
		if (mapIdComponent != null && this.mapId == -1) {
			this.mapId = mapIdComponent.id();
		}
		if (mapIdComponent == null) {
			return;
		}
		final MapState mapState = this.getState(mapIdComponent);
		this.processMapState(mapState);
	}

	/**
	 * Gets the dungeon room at the specified coordinate and merge it if there is a pending merge attached to the room.
	 *
	 * @param x The x coordinate of the room-
	 * @param y The y coordinate of the room.
	 * @return The room.
	 */
	@Nullable
	@Contract
	public DungeonRoom getRoomAt(int x, int y) {
		if (x < 0 || y < 0 || x >= this.roomMap.length || y >= this.roomMap.length) {
			return null;
		}
		final DungeonRoom dungeonRoom = this.roomMap[x][y];
		if (dungeonRoom != null && dungeonRoom.getMergedWith() != null) {
			this.setRoom(x, y, dungeonRoom.getMergedWith());
			if (dungeonRoom.equals(dungeonRoom.getMergedWith())) {
				dungeonRoom.setMergedWith(null);
				return dungeonRoom;
			}
			dungeonRoom.getPosition().forEach(dungeonRoom.getMergedWith()::addPosition);
			return dungeonRoom.getMergedWith();
		}
		return dungeonRoom;
	}

	/**
	 * Updates the secret count in the current room.
	 *
	 * @param secretLine The secret line from the actionbar.
	 */
	public void updateSecrets(@Nullable String secretLine) {
		if (this.canUpdateRooms()) {
			final Vector2i playerMapPosition = this.getPlayerRoomMapPosition();
			final DungeonRoom roomAt = this.getRoomAt(playerMapPosition.x, playerMapPosition.y);
			if (roomAt == null) {
				return;
			}
			roomAt.updateSecrets(secretLine, playerMapPosition.x, playerMapPosition.y);
		}
	}

	/**
	 * Update the current room position and set a timout if a room switch was detected.
	 */
	@Contract
	private void updateTimes() {
		final Vector2i playerMapPosition = this.getPlayerRoomMapPosition();
		if (!playerMapPosition.equals(this.lastMapPosition)) {
			this.lastMapPosition = playerMapPosition;
			this.lastRoomSwitch = System.currentTimeMillis();
			this.instance.swapRoom(this.lastMapPosition);
		}
	}

	/**
	 * Whether the player has been in the current room long enough for it to be updated or not.
	 *
	 * @return Whether it can be updated.
	 */
	@Contract
	private boolean canUpdateRooms() {
		this.updateTimes();
		return System.currentTimeMillis() - this.lastRoomSwitch > 1000;
	}

	/**
	 * Gets the player position in the room map.
	 *
	 * @return The position.
	 */
	@NotNull
	@Contract(pure = true)
	public Vector2i getPlayerRoomMapPosition() {
		final Vec3d playerPosition = this.instance.getPlayerPosition();
		int playerMapX = (int) Math.floor((playerPosition.x + 200) / 32);
		int playerMapY = (int) Math.floor((playerPosition.z + 200) / 32);
		return new Vector2i(playerMapX, playerMapY);
	}

	/**
	 * Updates the map based on the item map.
	 */
	@Contract
	private void discoverMapInfo() {
		final MapIdComponent mapIdComponent = new MapIdComponent(this.mapId);
		final MapState state = this.getState(mapIdComponent);
		this.processMapState(state);
	}

	/**
	 * Gets the map state of the map component.
	 *
	 * @param mapIdComponent The map component.
	 * @return The map state.
	 */
	@Contract(pure = true)
	@Nullable
	private MapState getState(@NotNull MapIdComponent mapIdComponent) {
		if (this.instance.getPlayer() == null) {
			return null;
		}
		return this.instance.getPlayer().getWorld().getMapState(mapIdComponent);
	}

	/**
	 * Process the map state and add various things to the map.
	 * Also find top left and bottom right positions of the map.
	 *
	 * @param mapState The map state.
	 */
	@Contract
	private void processMapState(MapState mapState) {
		if (mapState == null) {
			return;
		}
		if (this.instance.getPhase() == DungeonPhase.BEFORE) {
			this.instance.setPhase(DungeonPhase.CLEAR);
		}
		this.updateTimes();
		if (this.topLeftPixel == null) {
			for (int i = 0; i < mapState.colors.length; i++) {
				int colIndex = i % 128;
				int rowIndex = i / 128;
				if ((colIndex % 128) + 15 > 128) {
					continue;
				}

				byte color = mapState.colors[i];
				if (!RoomType.SPAWN.is(color) || !RoomType.SPAWN.is(mapState.colors[i + 7]) ||
					!RoomType.SPAWN.is(mapState.colors[i + 15])) {
					continue;
				}

				int width = 0;
				while (RoomType.SPAWN.is(mapState.colors[i + width])) {
					width++;
				}
				this.roomSize = width;
				this.halfRoomSize = this.roomSize / 2;
				this.doorRoomSize = this.roomSize + 4;

				int x = colIndex % this.doorRoomSize;
				int y = rowIndex % this.doorRoomSize;

				if (this.instance.floor() <= 1) {
					x += this.doorRoomSize;
				}
				if (this.instance.floor() == 0) {
					y += this.doorRoomSize;
				}

				this.topLeftPixel = new Vector2i(x, y);
				int mostRight = this.topLeftPixel.x;
				this.roomsInX = 0;


				while (mostRight < 128 - this.topLeftPixel.x) {
					mostRight += this.doorRoomSize;
					this.roomsInX++;
					if (this.isMapDebug()) {
						mapState.colors[this.getMapIndex(mostRight, 0)] = (byte) MapColor.RED.color;
					}
				}

				this.roomsInY = 0;
				int mostDown = this.topLeftPixel.y;
				while (mostDown < 128 - this.topLeftPixel.y) {
					mostDown += this.doorRoomSize;
					this.roomsInY++;
					if (this.isMapDebug()) {
						mapState.colors[this.getMapIndex(0, mostDown)] = (byte) MapColor.RED.color;
					}
				}

				this.bottomRightPixel = new Vector2i(mostRight, mostDown).sub(4, 4);
				break;
			}
		}

		if (this.topLeftPixel != null) {
			this.scanForRooms(mapState);

			if (this.isMapDebug()) {
				mapState.colors[this.getMapIndex(this.bottomRightPixel.x, this.bottomRightPixel.y)] =
						(byte) MapColor.RED.color;
				mapState.colors[this.getMapIndex(this.topLeftPixel.x - 1, this.topLeftPixel.y - 1)] =
						(byte) MapColor.RED.color;
			}
		}

		this.parseDecorations(mapState.getDecorations());
	}

	/**
	 * Whether the map debug is enabled or not, this will add multiple markers to the item map.
	 */
	@Contract(pure = true)
	public boolean isMapDebug() {
		return DevUtils.isEnabled(ADD_DEBUG_LINES_ON_MAP);
	}

	/**
	 * Scan the map state for rooms/doors and update checkmarks on already existing rooms.
	 *
	 * @param mapState The map state to check.
	 */
	@Contract
	private void scanForRooms(@NotNull MapState mapState) {
		for (int x = 0; x < this.roomMap.length; x++) {
			int roomMapX = this.roomXToMapX(x);
			for (int y = 0; y < this.roomMap.length; y++) {
				int roomMapY = this.roomYToMapY(y);
				this.addDoors(x, y, roomMapX, roomMapY, mapState);
			}
		}
		int puzzleCount = 0;
		for (int x = 0; x < this.roomMap.length; x++) {
			int roomMapX = this.roomXToMapX(x);
			for (int y = 0; y < this.roomMap.length; y++) {
				int roomMapY = this.roomYToMapY(y);
				byte color = mapState.colors[this.roomIndexToMapIndex(x, y)];
				RoomType roomType = this.getType(color);
				if (roomType == RoomType.PUZZLE) {
					final DungeonRoom roomAt = this.getRoomAt(x, y);
					if (roomAt == null) {
						continue;
					}
					if (roomAt.getPuzzleId() == -1) {
						this.puzzles.add(roomAt);
					}
					roomAt.setPuzzleId(puzzleCount++);
				}
				if (roomType != null) {
					this.updateRoom(x, y, roomType);
				}
				this.checkAndSetState(x, y, mapState);
				if (roomType == RoomType.NORMAL) {
					this.updateNormalRoom(x, y, roomMapX, roomMapY, roomType, mapState);
				}
			}
		}
		final List<Pair<PuzzleType, Integer>> knownPuzzles = this.instance.getKnownPuzzles();
		if (knownPuzzles.size() != this.puzzles.size()) {
			return;
		}
		for (DungeonRoom puzzle : this.puzzles) {
			final PuzzleType puzzleType = knownPuzzles.get(puzzle.getPuzzleId()).getLeft();
			if (puzzle.getPuzzleType() == puzzleType) {
				continue;
			}
			puzzle.setPuzzleType(puzzleType);
		}
	}

	/**
	 * Add the doors adjacent to the given dungeon room.
	 *
	 * @param x        The position of the room.
	 * @param y        The position of the room.
	 * @param roomMapX The coordinate on the item map.
	 * @param roomMapY The coordinate on the item map.
	 * @param mapState The map state to process.
	 */
	@Contract
	private void addDoors(int x, int y, int roomMapX, int roomMapY, @NotNull MapState mapState) {
		int roomMiddleX = roomMapX + this.halfRoomSize;
		int roomMiddleY = roomMapY + this.halfRoomSize;

		if (mapState.colors[this.getMapIndex(roomMiddleX - this.halfRoomSize - 2,
				roomMiddleY - this.halfRoomSize / 2)] == 0) {
			DungeonDoor.Type doorType =
					DungeonDoor.Type.ofId(mapState.colors[this.getMapIndex(roomMiddleX - this.halfRoomSize - 2,
							roomMiddleY)]);

			if (doorType != null) {
				this.addDoor(x, y, true, doorType);
			}
		}

		if (mapState.colors[this.getMapIndex(roomMiddleX - this.halfRoomSize / 2,
				roomMiddleY + this.halfRoomSize + 2)] == 0) {
			DungeonDoor.Type doorType = DungeonDoor.Type.ofId(mapState.colors[this.getMapIndex(roomMiddleX,
					roomMiddleY + this.halfRoomSize + 2)]);
			if (doorType != null) {
				this.addDoor(x, y, false, doorType);
			}
		}
	}

	/**
	 * Checks the room checkmarks and updates them if needed.
	 *
	 * @param roomMapX The item map x.
	 * @param roomMapY The item map y.
	 * @param mapState The map state to scan.
	 */
	@Contract
	private void checkAndSetState(int roomMapX, int roomMapY, @NotNull MapState mapState) {
		DungeonRoom room = this.getRoomAt(roomMapX, roomMapY);
		if (room == null) {
			return;
		}

		int roomMiddleX = this.roomXToMapX(roomMapX) + this.halfRoomSize;
		int roomMiddleY = this.roomYToMapY(roomMapY) + this.halfRoomSize;

		final byte color = mapState.colors[this.getMapIndex(roomMiddleX, roomMiddleY)];
		if (room.getCheckmark() == Checkmark.DONE) {
			return;
		}
		Checkmark roomCheckmark = room.getCheckmark();
		Checkmark checkmark = Checkmark.getByColor(color);
		if (roomCheckmark == checkmark) {
			return;
		}
		if (checkmark == Checkmark.OPENED && roomCheckmark == Checkmark.CLEARED) {
			return;
		}

		room.setCheckmark(checkmark);
	}

	/**
	 * Adds a door to the map.
	 *
	 * @param x        The x coordinate.
	 * @param y        The y coordinate.
	 * @param left     Whether the door is left of the room or under it.
	 * @param doorType The type of the door.
	 */
	@Contract
	public void addDoor(int x, int y, boolean left, @NotNull DungeonDoor.Type doorType) {
		for (DungeonDoor door : this.doors) {
			if (door.x() == x && door.y() == y && door.left() == left) {
				DungeonDoor.Type existing = door.type();
				if (DungeonConfig.getInstance().keepWitherDoor.getValue()) {
					if (((existing == DungeonDoor.Type.UNKNOWN && doorType != DungeonDoor.Type.UNKNOWN) ||
						 (existing == DungeonDoor.Type.WITHER && doorType == DungeonDoor.Type.FAIRY))) {
						door.setType(doorType);
					}
				} else {
					door.setType(doorType);
				}
				return;
			}
		}

		final DungeonDoor dungeonDoor = new DungeonDoor(x, y, left, doorType);
		this.doors.add(dungeonDoor);
	}

	/**
	 * Updates a normal type dungeon room and merges all adjacent rooms if they are connected.
	 *
	 * @param x        The x location of the room.
	 * @param y        The y location of the room.
	 * @param roomMapX The x location on the item map.
	 * @param roomMapY The y location on the item map.
	 * @param roomType The type of the room (should always be normal).
	 * @param mapState The map state.
	 */
	private void updateNormalRoom(
			int x, int y, int roomMapX, int roomMapY, @NotNull RoomType roomType, @NotNull MapState mapState) {
		DungeonRoom currentRoom = this.getRoomAt(x, y);

		DungeonRoom oneUp = this.getRoomIfNormal(x, y - 1, roomMapX, roomMapY - 1, mapState);
		DungeonRoom oneLeft = this.getRoomIfNormal(x - 1, y, roomMapX - 1, roomMapY, mapState);
		DungeonRoom oneUpRight = this.getRoomIfNormal(x + 1, y - 1, roomMapX + this.roomSize, roomMapY - 1, mapState);

		// No current room, and no adjacent rooms :c
		if (currentRoom == null && oneUp == null && oneLeft == null) {
			this.setNewRoom(x, y, roomType);
			return;
		}

		if (currentRoom == null) {
			return;
		}

		if (this.isMapDebug()) {
			mapState.colors[(roomMapX + 1) + (roomMapY + 1) * 128] = (byte) currentRoom.getDebugRoomColor();
		}

		RoomType currentRoomType = currentRoom.getRoomType();
		if (currentRoomType == RoomType.UNKNOWN || currentRoom.getCheckmark() == Checkmark.UNKNOWN) {
			currentRoom.setRoomType(roomType);
			currentRoom.setCheckmark(Checkmark.OPENED);
		}

		if (this.isMapDebug()) {
			if (currentRoom.equals(oneLeft)) {
				for (int i = 0; i <= this.doorRoomSize; i++) {
					mapState.colors[this.getMapIndex(roomMapX - i, roomMapY + 1)] =
							(byte) currentRoom.getDebugRoomColor();
				}
			}
			if (currentRoom.equals(oneUp)) {
				for (int i = 0; i <= this.doorRoomSize; i++) {
					mapState.colors[this.getMapIndex(roomMapX + 1, roomMapY + 1 - i)] =
							(byte) currentRoom.getDebugRoomColor();
				}
			}
		}

		// Merge left room with current
		if (oneLeft != null && !currentRoom.equals(oneLeft) && oneLeft.getRoomType() == RoomType.NORMAL) {
			this.setMerged(x - 1, y, currentRoom);
		}
		// Merge top room with current
		if (oneUp != null && !currentRoom.equals(oneUp) && oneUp.getRoomType() == RoomType.NORMAL) {
			this.setMerged(x, y - 1, currentRoom);
		}
		// Merge right top room with current
		if (oneUpRight != null && !currentRoom.equals(oneUpRight) && oneUpRight.getRoomType() == RoomType.NORMAL &&
			currentRoom.equals(oneUp)) {
			this.setMerged(x + 1, y - 1, currentRoom);
		}
	}

	/**
	 * Mark the room to be merged with other room, this is needed for already merged rooms.
	 *
	 * @param x          The x position of the room.
	 * @param y          The y position of the room.
	 * @param mergedWith The room to merge with.
	 */
	@Contract
	private void setMerged(int x, int y, @NotNull DungeonRoom mergedWith) {
		final DungeonRoom roomAt = this.getRoomAt(x, y);
		if (roomAt != null) {
			roomAt.setMergedWith(mergedWith);
		}
		this.setRoom(x, y, mergedWith);
	}

	/**
	 * Gets the room at the given coordinate if it's a normal room.
	 *
	 * @param x        The x coordinate in the room map.
	 * @param y        The y coordinate in the room map.
	 * @param roomMapX The x coordinate on the item map.
	 * @param roomMapY The y coordinate on the item map.
	 * @param mapState The map state.
	 * @return The room, or null if the room is a non-normal room or {@code x/y} is out of bounds.
	 */
	@Nullable
	private DungeonRoom getRoomIfNormal(int x, int y, int roomMapX, int roomMapY, @NotNull MapState mapState) {
		if (x < 0 || y < 0 || x >= this.roomMap.length || y >= this.roomMap.length) {
			return null;
		}
		if (this.isMapDebug()) {
			mapState.colors[this.getMapIndex(roomMapX, roomMapY - 1)] = 31;
		}
		if (RoomType.NORMAL.is(mapState.colors[this.getMapIndex(roomMapX, roomMapY)])) {
			return this.getRoomAt(x, y);
		}
		return null;
	}

	/**
	 * Maps the x and y coordinate to the corresponding item map index.
	 *
	 * @param x The item map x.
	 * @param y The item map y.
	 * @return The item map index.
	 */
	@Contract(pure = true)
	private int roomIndexToMapIndex(int x, int y) {
		return this.getMapIndex(this.roomXToMapX(x), this.roomYToMapY(y));
	}

	/**
	 * Map the room map x to the item map x.
	 *
	 * @param x The room map x.
	 * @return The item map x.
	 */
	@Contract(pure = true)
	private int roomXToMapX(int x) {
		return this.topLeftPixel.x + x * this.doorRoomSize;
	}

	/**
	 * Map the room map y to the item map y.
	 *
	 * @param y The room map y.
	 * @return The item map y.
	 */
	@Contract(pure = true)
	private int roomYToMapY(int y) {
		return this.topLeftPixel.y + y * this.doorRoomSize;
	}

	/**
	 * Get the item map index for the provided coordinates.
	 *
	 * @param x The item map x.
	 * @param y The item map y.
	 * @return The item map index.
	 */
	@Contract(pure = true)
	private int getMapIndex(int x, int y) {
		return MathUtils.clamp(x + y * 128, 0, 128 * 128 - 1);
	}

	/**
	 * Update the room type of the room at the given coordinate.
	 *
	 * @param x        The x coordinate of the room.
	 * @param y        The y coordinate of the room.
	 * @param roomType The new room type.
	 */
	@Contract
	private void updateRoom(int x, int y, @NotNull RoomType roomType) {
		if (roomType == RoomType.BLOOD && this.instance.getPhase().ordinal() < DungeonPhase.BLOOD.ordinal()) {
			this.instance.setPhase(DungeonPhase.BLOOD);
		}

		DungeonRoom dungeonRoom = this.getRoomAt(x, y);
		if (dungeonRoom == null) {
			this.setNewRoom(x, y, roomType);
		} else {
			if (dungeonRoom.getRoomType() != roomType || dungeonRoom.getRoomType() == RoomType.UNKNOWN) {
				dungeonRoom.setRoomType(roomType);
				dungeonRoom.setCheckmark(Checkmark.OPENED);
			}
			if (dungeonRoom.getCheckmark() == Checkmark.UNKNOWN && dungeonRoom.getRoomType() != RoomType.UNKNOWN) {
				dungeonRoom.setCheckmark(Checkmark.OPENED);
			}
		}
	}

	/**
	 * Sets a new room with the give type at the given coordinate.
	 *
	 * @param x        The room map x.
	 * @param y        The room map y.
	 * @param roomType The room type for the new room.
	 */
	private void setNewRoom(int x, int y, @NotNull RoomType roomType) {
		DungeonRoom dungeonRoom = new DungeonRoom(this.instance, roomType);
		dungeonRoom.setCheckmark(roomType == RoomType.UNKNOWN ? Checkmark.UNKNOWN : Checkmark.OPENED);
		this.setRoom(x, y, dungeonRoom);
	}

	/**
	 * Sets the room at the give position and add the postion to the rooms positions.
	 *
	 * @param x           The x to set the room at.
	 * @param y           The y to set the room at.
	 * @param dungeonRoom The room to set at the given position.
	 */
	public void setRoom(int x, int y, @NotNull DungeonRoom dungeonRoom) {
		if (x < 0 || y < 0 || x >= this.roomMap.length || y >= this.roomMap.length) {
			return;
		}
		final DungeonPosition dungeonPosition = new DungeonPosition(0, 0, this.instance);
		dungeonPosition.setRoomMapX(x);
		dungeonPosition.setRoomMapY(y);
		dungeonRoom.addPosition(dungeonPosition);
		this.roomMap[x][y] = dungeonRoom;
	}

	/**
	 * Get the room type based on the color.
	 *
	 * @param color The color.
	 * @return The room type.
	 */
	@Nullable
	@Contract(pure = true)
	private RoomType getType(byte color) {
		for (RoomType value : RoomType.VALUES) {
			if (value.is(color)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Parses the decorations and updates the players if needed.
	 *
	 * @param decorations The decorations.
	 */
	private void parseDecorations(@NotNull Iterable<MapDecoration> decorations) {
		int index = 0;
		for (MapDecoration decoration : decorations) {
			index = this.instance.applyOffset(index);
			final DungeonPlayer player = this.instance.getPlayer(index);
			if (player == null) {
				continue;
			}
			player.setPosition(decoration.x(), decoration.z());
			player.setRotation(decoration.rotation());
			index++;
		}
	}

}
