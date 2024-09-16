package dev.morazzer.cookies.mod.utils.skyblock;

import java.util.function.Predicate;

import net.minecraft.client.network.PlayerListEntry;

/**
 * Utils for interactions with the tab list.
 */
public interface TabUtils {

	static boolean isInRange(PlayerListEntry entry, int column, int start, int end) {
		return getColumn(entry) == column && getRow(entry) > start && getRow(entry) < end;
	}

	/**
	 * @param column the entry to check.
	 * @return Whether the entry is in the provided column or not.
	 */
	static Predicate<PlayerListEntry> isInColumn(int column) {
		return entry -> getColumn(entry) == column;
	}

	/**
	 * @param entry The entry to check.
	 * @return Gets the row of the player entry.
	 */
	static int getRow(PlayerListEntry entry) {
		return entry.getProfile().getName().charAt(3) - 97;
	}

	/**
	 * @param entry The entry to check.
	 * @return Gets the column of the player entry.
	 */
	static int getColumn(PlayerListEntry entry) {
		return entry.getProfile().getName().charAt(1) - 65;
	}
}
