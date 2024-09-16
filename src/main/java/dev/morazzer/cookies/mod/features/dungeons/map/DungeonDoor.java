package dev.morazzer.cookies.mod.features.dungeons.map;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A door on the Dungeon map, the positioning is the coordinate of the room that is either right next to it or above it.
 * <br>
 * When left is ture the door will be placed on the left side of the room, else it will be placed under the room.
 */
public final class DungeonDoor {
	private final int x;
	private final int y;
	private final boolean left;
	@Setter
	private Type type;

	/**
	 * Creates a new dungeon door for the room at coordinates (`x,y`).
	 *
	 * @param x The x position of the room.
	 * @param y The y position of the room.
	 * @param left Whether to door is on the left side or under the room.
	 * @param type The type of the door.
	 */
	public DungeonDoor(
			int x, int y, boolean left, Type type) {
		this.x = x;
		this.y = y;
		this.left = left;
		this.type = type;
	}

	public int x() {return this.x;}

	public int y() {return this.y;}

	public boolean left() {return this.left;}

	public Type type() {return this.type;}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (DungeonDoor) obj;
		return this.x == that.x && this.y == that.y && this.left == that.left && Objects.equals(this.type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y, this.left, this.type);
	}

	@Override
	public String toString() {
		return "DungeonDoor[" + "x=" + this.x + ", " + "y=" + this.y + ", " + "left=" + this.left + ", " + "type=" +
			   this.type + ']';
	}

	/**
	 * The different types that a door can have, this mostly is equivalent to {@link RoomType} and may be removed in the future.
	 */
	@RequiredArgsConstructor
	public enum Type {
		NORMAL(RoomType.NORMAL.getColor()),
		WITHER(119),
		BLOOD(RoomType.BLOOD.getColor()),
		PUZZLE(RoomType.PUZZLE.getColor()),
		TRAP(RoomType.TRAP.getColor()),
		MINIBOSS(RoomType.MINIBOSS.getColor()),
		FAIRY(RoomType.FAIRY.getColor()),
		UNKNOWN(RoomType.UNKNOWN.getColor());

		private final int id;
		private static final Type[] VALUES = values();

		public static Type ofId(int id) {
			for (Type value : VALUES) {
				if (value.id == id) {
					return value;
				}
			}
			return null;
		}
	}

}
