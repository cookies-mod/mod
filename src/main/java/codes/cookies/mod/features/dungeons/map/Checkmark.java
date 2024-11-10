package codes.cookies.mod.features.dungeons.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.minecraft.block.MapColor;
import net.minecraft.util.Identifier;

/**
 * Represents the different types of checkmarks supported by the map.
 */
@Getter
@RequiredArgsConstructor
public enum Checkmark {

	UNKNOWN(119, texture("question_mark.png")),
	OPENED(-1, null),
	CLEARED(34, texture("checkmark_white.png")),
	FAILED(18, texture("cross.png")),
	DONE(30, texture("checkmark.png"));

	private static Identifier texture(String path) {
		return Identifier.of("cookies-mod", "textures/dungeon/map/" + path);
	}

	private final int color;
	private final Identifier identifier;
	private static final Checkmark[] VALUES = values();

	/**
	 * Gets the checkmark that is associated with the map color.
	 *
	 * @param color The map color.
	 * @return The checkmark.
	 */
	public static Checkmark getByColor(int color) {
		for (Checkmark value : VALUES) {
			if (value.color == color || value.color == MapColor.get(color >> 2).color) {
				return value;
			}
		}
		return OPENED;
	}
}
