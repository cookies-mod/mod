package codes.cookies.mod.utils.skyblock.playerlist.widgets.crystal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import codes.cookies.mod.data.mining.crystal.CrystalStatus;
import codes.cookies.mod.data.mining.crystal.CrystalType;
import codes.cookies.mod.utils.skyblock.playerlist.PlayerListReader;
import codes.cookies.mod.utils.skyblock.playerlist.widgets.PlayerListWidget;
import lombok.Getter;

/**
 * Contains information about different crystals and their status. Also supports the settings that are available for it.
 */
public class CrystalWidget extends PlayerListWidget {

	@Getter
	private final List<CrystalEntry> crystals = new ArrayList<>();
	private boolean showNucleusCrystals = false;
	private boolean showJasperAndRubyCrystals = false;
	private boolean showGlaciteCrystals = false;

	public static boolean doesMatch(String title) {
		return "crystals:".equalsIgnoreCase(title);
	}

	@Override
	protected void read(PlayerListReader reader) {
		while (reader.canRead() && !reader.isTitle()) {
			final Optional<CrystalEntry> entryFromString = CrystalEntry.createEntryFromString(reader.read());
			if (entryFromString.isEmpty()) {
				return;
			}
			final CrystalEntry crystalEntry = entryFromString.get();
			this.crystals.add(crystalEntry);
			switch (crystalEntry.type().getCrystalOrigin()) {
				case CRYSTAL_HOLLOWS -> this.showNucleusCrystals = true;
				case MISC -> this.showJasperAndRubyCrystals = true;
				case CRIMSON_ISLE, GLACITE_TUNNELS -> this.showGlaciteCrystals = true;
			}
		}
	}

	public Optional<CrystalStatus> getCrystalStatusByType(CrystalType crystalType) {
		return crystals.stream()
				.filter(crystalEntry -> crystalEntry.type() == crystalType)
				.map(CrystalEntry::status)
				.findFirst();
	}

	public boolean doesShowNucleusCrystals() {
		return this.showNucleusCrystals;
	}

	public boolean doesShowJasperAndRubyCrystals() {
		return this.showJasperAndRubyCrystals;
	}

	public boolean doesShowGlaciteCrystals() {
		return this.showGlaciteCrystals;
	}
}
