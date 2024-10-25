package dev.morazzer.cookies.mod.features.dungeons.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import dev.morazzer.cookies.entities.websocket.packets.DungeonUpdateRoomSecrets;
import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;
import dev.morazzer.cookies.mod.features.dungeons.DungeonPosition;
import dev.morazzer.cookies.mod.render.Renderable;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.maths.InterpolatedInteger;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2i;

/**
 * A dungeon room, this holds various information that is needed to render and display data associated with this room.
 */
@Getter
@Setter
public class DungeonRoom {
	/**
	 * A debug counter to display color, this isn't used without the debug flag enabled.
	 */
	private static int counter = 0;
	private final int debugRoomColor;

	private final DungeonInstance instance;
	private boolean isActive;
	private RoomType roomType;
	private PuzzleType puzzleType;
	private Checkmark checkmark;
	private DungeonRoom mergedWith;
	private Collection<DungeonPosition> position = new ArrayList<>();
	private int x;
	private int y;
	private int width;
	private int height;
	private int rotation;
	private String shape;
	private List<Renderable> waypoints = null;
	private int maxSecrets = -1;
	private int collectedSecrets = -1;
	private int puzzleId = -1;
	boolean isPuzzleDirty = false;
	@Getter
	private long becamePuzzleAt = -1;

	public DungeonRoom(DungeonInstance instance, RoomType roomType) {
		this.debugRoomColor = counter++;
		this.instance = instance;
		this.roomType = roomType;
		this.checkmark = Checkmark.UNKNOWN;
	}

	/**
	 * Get the secret string of the current room.
	 *
	 * @return The secret string of the room.
	 */
	public String getSecretString() {
		if (this.maxSecrets == 0) {
			return null;
		}
		if (this.maxSecrets == -1 && this.collectedSecrets == -1) {
			return "?/?";
		} else if (this.maxSecrets == -1) {
			return this.collectedSecrets + "/?";
		} else if (this.collectedSecrets == -1) {
			return "?/" + this.maxSecrets;
		}
		return this.collectedSecrets + "/" + this.maxSecrets;
	}

	/**
	 * @return Whether the room a {@code (x,y)} is the same as the current room.
	 */
	public boolean isAt(int x, int y) {
		if (x < 0 && y < 0) {
			return false;
		}
		return this.equals(this.instance.getDungeonMap().getRoomAt(x, y));
	}

	/**
	 * Find the shape of the room, this is used for unknown rooms.
	 *
	 * @return The shape.
	 */
	public String findShape() {
		if (this.position.size() == 1) {
			return "1x1";
		} else if (this.position.size() == 2) {
			return "1x2";
		}

		final List<Integer> xList =
				this.position.stream().map(DungeonPosition.target(DungeonPosition::getRoomMapX)).distinct().toList();
		final List<Integer> yList =
				this.position.stream().map(DungeonPosition.target(DungeonPosition::getRoomMapY)).distinct().toList();

		int xSize = xList.size();
		int ySize = yList.size();

		if (xSize == 2 && ySize == 2 && this.position.size() == 4) {
			return "2x2";
		}
		if (Math.min(xSize, ySize) == 1) {
			final int max = Math.max(xSize, ySize);
			if (max == 3) {
				return "1x3";
			} else if (max == 4) {
				return "1x4";
			}
		}

		return "L";
	}

	/**
	 * Whether the room can have secrets or not.
	 */
	public boolean canHaveSecrets() {
		return switch (this.roomType) {
			case NORMAL, TRAP -> true;
			default -> false;
		};
	}

	/**
	 * Updates the secrets of the room.
	 *
	 * @param s The secret line.
	 * @param x The x of the room.
	 * @param y The y of the room.
	 */
	public void updateSecrets(String s, int x, int y) {
		if (!this.canHaveSecrets()) {
			return;
		}

		final int collected, max;
		if (s == null) {
			collected = Math.max(0, this.collectedSecrets);
			max = Math.max(0, this.maxSecrets);
		} else {
			final String[] split = s.split("/");
			collected = Integer.parseInt(split[0]);
			max = Integer.parseInt(split[1]);
		}
		int newMax = Math.max(this.maxSecrets, max);

		if (this.maxSecrets != newMax || this.collectedSecrets != collected) {
			this.updateSecrets(x, y, collected, newMax);
		}

		this.maxSecrets = max;
		this.collectedSecrets = collected;
	}

	public void markPuzzleDirty() {
		this.isPuzzleDirty = true;
	}

	public void markPuzzleClean() {
		this.isPuzzleDirty = false;
	}

	@SuppressWarnings("SuspiciousGetterSetter")
	public boolean isPuzzleDirty() {
		return this.isPuzzleDirty;
	}

	/**
	 * Sends a message to the other clients including information about secrets in the current room.
	 */
	private void updateSecrets(int x, int y, int collected, int newMax) {
		this.instance.send(new DungeonUpdateRoomSecrets(x, y, collected, newMax));
	}

	/**
	 * Finds the minimum coordinates in worlds space.
	 */
	private void findMin() {
		for (DungeonPosition dungeonPosition : this.position) {
			if (this.x > DungeonPosition.target(dungeonPosition::getWorldX)) {
				this.x = DungeonPosition.target(dungeonPosition::getWorldX);
			}
			if (this.y > DungeonPosition.target(dungeonPosition::getWorldY)) {
				this.y = DungeonPosition.target(dungeonPosition::getWorldY);
			}
		}
	}

	/**
	 * Gets the max value for the given field.
	 *
	 * @param mapper A mapper.
	 * @return The max value
	 */
	private int getMax(BiFunction<DungeonPosition, Function<InterpolatedInteger, Integer>, Integer> mapper) {
		return this.position.stream()
				.max(Comparator.comparingInt(DungeonPosition.toInt(DungeonPosition.target(mapper))))
				.map(DungeonPosition.target(mapper))
				.orElse(0);
	}

	/**
	 * Gets the min value for the given field.
	 *
	 * @param mapper A mapper.
	 * @return The min value
	 */
	private int getMin(BiFunction<DungeonPosition, Function<InterpolatedInteger, Integer>, Integer> mapper) {
		return this.position.stream()
				.min(Comparator.comparingInt(DungeonPosition.toInt(DungeonPosition.target(mapper))))
				.map(DungeonPosition.target(mapper))
				.orElse(0);
	}

	/**
	 * Find the size of the current room.
	 */
	public void findSize() {
		int maxX = this.getMax(DungeonPosition::getWorldX);
		int minX = this.getMin(DungeonPosition::getWorldX);
		int maxY = this.getMax(DungeonPosition::getWorldY);
		int minY = this.getMin(DungeonPosition::getWorldY);

		this.width = 30 + 32 * (maxX - minX);
		this.height = 30 + 32 * (maxY - minY);
	}

	/**
	 * Add the position to the rooms positions, if not already in there.
	 *
	 * @param dungeonPosition The dungeon position to add.
	 */
	public void addPosition(DungeonPosition dungeonPosition) {
		if (this.position.contains(dungeonPosition)) {
			return;
		}
		this.position.add(dungeonPosition);
		this.shape = this.findShape();
		this.findMin();
		this.findSize();
	}

	/**
	 * Set the room type.
	 *
	 * @param roomType The room type.
	 */
	public void setRoomType(RoomType roomType) {
		if (this.roomType == roomType) {
			return;
		}
		if (roomType == RoomType.PUZZLE && this.becamePuzzleAt == -1) {
			this.becamePuzzleAt = System.currentTimeMillis();
		}
		if ((this.roomType == null || this.roomType == RoomType.UNKNOWN) && roomType != RoomType.UNKNOWN) {
			this.setCheckmark(Checkmark.OPENED);
		}
		this.roomType = roomType;
		if (roomType == RoomType.UNKNOWN) {
			this.setCheckmark(Checkmark.UNKNOWN);
		}
	}

	/**
	 * Set the checkmark of the room.
	 *
	 * @param checkmark The checkmark.
	 */
	public void setCheckmark(Checkmark checkmark) {
		if (checkmark == Checkmark.UNKNOWN && this.roomType != RoomType.UNKNOWN) {
			return;
		}
		if (checkmark == Checkmark.FAILED && this.roomType != RoomType.PUZZLE) {
			return;
		}
		this.checkmark = checkmark;
	}

	/**
	 * @return The text color of the room, this may be different based on room type.
	 */
	public int getRoomTextColor() {
		if (!DungeonConfig.getInstance().showRoomStatusAsTextColor.getValue()) {
			return -1;
		}

		if (this.roomType == RoomType.PUZZLE) {
			return switch (this.checkmark) {
				case DONE -> Constants.SUCCESS_COLOR;
				case FAILED -> Constants.FAIL_COLOR;
				default -> {
					if (this.getPuzzleType() == null) {
						yield 0xFFAAAAAA;
					}
					yield -1;
				}
			};
		}
		return switch (this.checkmark) {
			case OPENED -> {
				if (DungeonConfig.getInstance().showTrapAsCleared.getValue() && this.roomType == RoomType.TRAP) {
					yield -1;
				}
				yield Constants.FAIL_COLOR;
			}
			case DONE -> Constants.SUCCESS_COLOR;
			case CLEARED -> {
				if (this.canHaveSecrets() && this.maxSecrets != -1 && this.collectedSecrets >= this.maxSecrets) {
					yield Constants.SUCCESS_COLOR;
				}
				yield -1;
			}
			default -> -1;
		};
	}

	/**
	 * Sets the puzzle type and invokes the solver loading.
	 *
	 * @param puzzleType The puzzle type.
	 */
	public void setPuzzleType(PuzzleType puzzleType) {
		this.puzzleType = puzzleType;
		if (this.instance.getCurrentRoom() == this) {
			this.instance.loadPuzzle(this);
		}
	}

	public Optional<Vector2i> getTopLeft() {
		if (this.position.size() > 1) {
			return Optional.empty();
		}

		return this.position.stream().findFirst().map(DungeonRoom::toRoomPosition);
	}

	public Optional<Vector2i> getCenter() {
		return this.getTopLeft().map(vector2i -> vector2i.add(15, 15));
	}

	private static Vector2i toRoomPosition(DungeonPosition dungeonPosition) {
		return new Vector2i(DungeonPosition.target(dungeonPosition::getWorldX),
				DungeonPosition.target(dungeonPosition::getWorldY));
	}
}
