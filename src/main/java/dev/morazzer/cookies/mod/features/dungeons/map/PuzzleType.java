package dev.morazzer.cookies.mod.features.dungeons.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing all supported puzzles.
 */
@RequiredArgsConstructor
@Getter
public enum PuzzleType {

	QUIZ("Quiz"), 
	TIC_TAC_TOE("Tic Tac Toe"), 
	WEIRDOS("Three Weirdos"), 
	ICE_PATH("Ice Path"), 
	ICE_FILL("Ice Fill"), 
	HIGHER_LOWER("Higher Or Lower", "Higher Lower"), 
	CREEPER("Creeper Beams", "Creeper"), 
	WATERBOARD("Water Board"), 
	BOULDER("Boulder"), 
	MAZE("Teleport Maze", "Maze"), 
	UNKNOWN("Unknown");

	PuzzleType(String name) {
		this(name, name);
	}

	private final String tabName;
	private final String displayName;

	public static PuzzleType ofName(String puzzleName) {
		for (PuzzleType value : values()) {
			if (value.tabName.equals(puzzleName)) {
				return value;
			}
		}
		return UNKNOWN;
	}
}
