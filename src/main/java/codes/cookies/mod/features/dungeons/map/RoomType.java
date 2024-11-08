package codes.cookies.mod.features.dungeons.map;

import lombok.Getter;

/**
 * The different types of rooms supported by the mod.
 */
@Getter
public enum RoomType {

	NORMAL(63, "normal", "miniboss", "rare"),
	SPAWN(30, "spawn"),
	PUZZLE(66, "puzzle"),
	FAIRY(82, "fairy"),
	BLOOD(18, "blood"),
	TRAP(62, "trap"),
	MINIBOSS(74, "gold"),
	UNKNOWN(85);
	private final int color;
	public static final RoomType[] VALUES = values();
	private final String[] name;

	RoomType(int color, String... name) {
		this.color = color;
		this.name = name;
	}

	/**
	 * Get the room type based of the name used in the data.
	 * @param name The name.
	 * @return The type.
	 */
	public static RoomType of(String name) {
		for (RoomType value : VALUES) {
			for (String s : value.name) {
				if (s.equals(name)) {
					return value;
				}
			}
		}
		return UNKNOWN;
	}

	/**
	 * @param color The color to check.
	 * @return Whether the room is of that color or not.
	 */
	public boolean is(byte color) {
		return this.color == color;
	}
}
