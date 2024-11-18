package codes.cookies.mod.utils.skyblock.playerlist.widgets.crystal;

import codes.cookies.mod.data.mining.crystal.CrystalStatus;
import codes.cookies.mod.data.mining.crystal.CrystalType;

import java.util.Optional;

/**
 * Represents a crystal line in the player list.
 *
 * @param type The type of the crystal.
 * @param status Whether it is missing or not.
 */
public record CrystalEntry(CrystalType type, CrystalStatus status) {

	/**
	 * Parses the player list line if possible.
	 * @param line The line to parse.
	 * @return The parsed entry.
	 */
	public static Optional<CrystalEntry> createEntryFromString(String line) {
		if (line == null || !line.startsWith(" ") || !line.contains(":")) {
			return Optional.empty();
		}

		String[] split = line.trim().split(":");

		final Optional<CrystalType> type = CrystalType.getCrystalTypeByDisplayName(split[0].trim());
		CrystalStatus status = CrystalStatus.getCrystalStatusFromText(split[1].trim());

		return type.map(crystalType -> new CrystalEntry(crystalType, status));
	}

}
