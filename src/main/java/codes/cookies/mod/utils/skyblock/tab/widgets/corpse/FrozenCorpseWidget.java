package codes.cookies.mod.utils.skyblock.tab.widgets.corpse;

import java.util.ArrayList;
import java.util.List;

import codes.cookies.mod.utils.skyblock.tab.PlayerListReader;
import codes.cookies.mod.utils.skyblock.tab.widgets.PlayerListWidget;
import lombok.Getter;

/**
 * Widget with information about what corpses are present in the mineshaft.
 */
@Getter
public class FrozenCorpseWidget extends PlayerListWidget {
	private final List<CorpseEntry> corpses = new ArrayList<>();

	public static String getTitle() {
		return "Frozen Corpses";
	}

	public static boolean doesMatch(String s) {
		return s.startsWith(getTitle());
	}

	@Override
	protected void read(PlayerListReader reader) {
		int amount = 0;
		while (reader.canRead() && !reader.peek().isBlank() && !reader.isTitle()) {
			if (!addCorpse(reader.peek())) {
				return;
			}
			if (amount > 5) {
				return;
			}
			amount++;
			reader.skip();
		}
	}

	private boolean addCorpse(String peek) {
		if (!peek.contains(":")) {
			return false;
		}

		final String[] parts = peek.split(":");
		if (parts.length != 2) {
			return false;
		}
		if (!"looted".equalsIgnoreCase(parts[1].trim()) && !"not looted".equalsIgnoreCase(parts[1].trim())) {
			return false;
		}

		CorpseType corpseType = CorpseType.getCorpseTypeFromString(parts[0].trim());
		boolean found = "looted".equalsIgnoreCase(parts[1].trim());

		corpses.add(new CorpseEntry(corpseType, found));

		return true;
	}
}
