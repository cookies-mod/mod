package codes.cookies.mod.utils.skyblock.tab.widgets.corpse;

import codes.cookies.mod.utils.skyblock.tab.PlayerListReader;
import codes.cookies.mod.utils.skyblock.tab.widgets.PlayerListWidget;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget with information about what corpses are present in the mineshaft.
 */
@Getter
public class FrozenCorpseWidget extends PlayerListWidget {
	private final List<CorpseEntry> corpses = new ArrayList<>();

	public static String getTitle() {
		return "Frozen Corpses";
	}

	@Override
	protected void read(PlayerListReader reader) {
		while (reader.canRead() && !reader.peek().isBlank() && !reader.isTitle()) {
			if (!addCorpse(reader.peek())) {
				return;
			}
			reader.skip();
		}
	}

	private boolean addCorpse(String peek) {
		if (!peek.contains(":")) {
			return false;
		}

		final String[] parts = peek.split(":");

		CorpseType corpseType = CorpseType.getCorpseTypeFromString(parts[0].trim());
		boolean found = "looted".equals(parts[1]);

		corpses.add(new CorpseEntry(corpseType, found));

		return true;
	}

	public static boolean doesMatch(String s) {
		return s.startsWith(getTitle());
	}
}
