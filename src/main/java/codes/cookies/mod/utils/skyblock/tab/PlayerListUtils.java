package codes.cookies.mod.utils.skyblock.tab;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.events.PlayerListEvent;

import codes.cookies.mod.utils.SkyblockUtils;

import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import codes.cookies.mod.utils.skyblock.tab.widgets.PlayerListWidget;
import codes.cookies.mod.utils.skyblock.tab.widgets.PlayerListWidgets;

import net.minecraft.client.network.PlayerListEntry;

/**
 * Utils for interactions with the tab list.
 */
public class PlayerListUtils {
	private static final PlayerListEntrySet playerListEntrySet = new PlayerListEntrySet();
	public static List<PlayerListWidget> widgets = new CopyOnWriteArrayList<>();

	public static void init() {
		PlayerListEvent.EVENT.register(PlayerListUtils::update);
		CookiesMod.getExecutorService().scheduleAtFixedRate(
				PlayerListUtils::updatePlayerListWidgets,
				1,
				1,
				TimeUnit.SECONDS
		);
	}

	private static void updatePlayerListWidgets() {
		try {
			if (!SkyblockUtils.isCurrentlyInSkyblock()) {
				return;
			}
			final PlayerListReader playerListReader = new PlayerListReader(playerListEntrySet.getInfoElements());
			final List<PlayerListWidget> playerListWidgets = PlayerListWidgets.extractAll(playerListReader);
			widgets.clear();
			widgets.addAll(playerListWidgets);
			widgets.forEach(PlayerListWidget::sendEvent);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	private static void update(PlayerListEntry playerListEntry) {
		if (playerListEntry == null) {
			return;
		}
		final String name = playerListEntry.getProfile().getName();
		if (!name.startsWith("!")) {
			return;
		}
		if (playerListEntry.getDisplayName() == null) {
			return;
		}
		playerListEntrySet.replace(getColumn(playerListEntry), getRow(playerListEntry), playerListEntry.getDisplayName().getString());
	}

	public static boolean isInRange(PlayerListEntry entry, int column, int start, int end) {
		return getColumn(entry) == column && getRow(entry) > start && getRow(entry) < end;
	}

	/**
	 * @param column the entry to check.
	 * @return Whether the entry is in the provided column or not.
	 */
	public static Predicate<PlayerListEntry> isInColumn(int column) {
		return entry -> getColumn(entry) == column;
	}

	/**
	 * @param entry The entry to check.
	 * @return Gets the row of the player entry.
	 */
	public static int getRow(PlayerListEntry entry) {
		return entry.getProfile().getName().charAt(3) - 97;
	}

	/**
	 * @param entry The entry to check.
	 * @return Gets the column of the player entry.
	 */
	public static int getColumn(PlayerListEntry entry) {
		return entry.getProfile().getName().charAt(1) - 65;
	}
}
