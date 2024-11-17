package codes.cookies.mod.utils.skyblock.tab.widgets.corpse;

/**
 * The different types of supported corpses.
 */
public enum CorpseType {

	LAPIS, UMBER, TUNGSTEN, VANGUARD, UNKNOWN;

	public static CorpseType getCorpseTypeFromString(String string) {
		for (CorpseType type : CorpseType.values()) {
			if (type.name().equalsIgnoreCase(string)) {
				return type;
			}
		}
		return UNKNOWN;
	}

}
