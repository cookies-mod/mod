package codes.cookies.mod.features.mining.shafts;

import codes.cookies.mod.config.categories.mining.MiningConfig;
import codes.cookies.mod.events.IslandChangeEvent;
import codes.cookies.mod.events.ScoreboardUpdateEvent;
import codes.cookies.mod.events.mining.MineshaftEvents;
import codes.cookies.mod.repository.constants.mining.ShaftCorpseLocations;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import lombok.Getter;

import java.util.Optional;

public class ShaftFeatures {
	private static boolean isInShaft = false;
	@Getter
	public static long lastShaftFoundAt = -1;
	private static ShaftCorpseLocations.ShaftLocations locations;

	public static Optional<ShaftCorpseLocations.ShaftLocations> getCurrentMineshaftLocations() {
		return Optional.ofNullable(locations);
	}

	public static void load() {
		IslandChangeEvent.EVENT.register(ShaftFeatures::swapIsland);
		ScoreboardUpdateEvent.EVENT.register(ShaftFeatures::updateLine);
	}

	private static void swapIsland(LocationUtils.Island previous, LocationUtils.Island current) {
		if (isInShaft) {
			MineshaftEvents.LEAVE.invoker().run();
		}
		isInShaft = false;
		locations = null;


		if (current == LocationUtils.Island.MINESHAFT) {
			isInShaft = true;
			MineshaftEvents.JOIN.invoker().run();
		}
	}

	private static void updateLine(int index, String line) {
		if (!MiningConfig.getInstance().shaftConfig.enable.getValue()) {
			return;
		}
		if (!isInShaft) {
			return;
		}
		if (locations != null) {
			return;
		}
		final String server = LocationUtils.getServerDisplayName().orElse("");
		if (!line.contains(server)) {
			return;
		}
		final String[] split = line.split(server);
		if (split.length != 2) {
			return;
		}
		final String shaftType = split[1].trim();

		locations = ShaftCorpseLocations.getCachedOrCreate(shaftType);
		if (!locations.cached()) {
			CookiesUtils.sendFailedMessage("No cached data found, creating new one!");
		}
		MineshaftEvents.JOIN_SHAFT.invoker().accept(locations);
	}
}
