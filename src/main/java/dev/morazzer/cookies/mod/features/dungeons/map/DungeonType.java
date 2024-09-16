package dev.morazzer.cookies.mod.features.dungeons.map;

/**
 * The different types of dungeons.
 */
public enum DungeonType {

    CATACOMBS, CATACOMBS_MASTERMODE, NONE;

    public static DungeonType of(String type) {
        return switch (type) {
            case "MM The Catacombs" -> CATACOMBS_MASTERMODE;
            case "The Catacombs" -> CATACOMBS;
            default -> NONE;
        };
    }

	/**
	 * @return Whether the player is in the catacombs, this is just for future-proof atm since catacombs is the only dungeon type atm.
	 */
    public boolean isCatacombs() {
        return this == CATACOMBS || this == CATACOMBS_MASTERMODE;
    }

	/**
	 * @return Whether the player is in master mode, this may be used to enable master mode only functionality.
	 */
	public boolean isMasterMode() {
		return this == CATACOMBS_MASTERMODE;
	}

}
