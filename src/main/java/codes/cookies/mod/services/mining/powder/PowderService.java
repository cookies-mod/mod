package codes.cookies.mod.services.mining.powder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import codes.cookies.mod.config.categories.mining.powder.PowderTrackerHudFoldable;
import codes.cookies.mod.config.categories.mining.powder.ShaftTrackingType;
import codes.cookies.mod.data.mining.PowderType;
import codes.cookies.mod.events.ChatMessageEvents;
import codes.cookies.mod.events.mining.MineshaftEvents;
import codes.cookies.mod.events.mining.PowderUpdateEvent;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class PowderService {

	private static final Map<PowderType, Map<Long, Integer>> powderTimeouts = new HashMap<>();
	private static final Map<PowderType, PowderEntry> powderEntries = new HashMap<>();

	static {
		PowderUpdateEvent.EVENT.register(PowderService::track);
		MineshaftEvents.JOIN.register(PowderService::countShaftJoin);
		ChatMessageEvents.BEFORE_MODIFY.register(PowderService::onMessage);
		for (PowderType value : PowderType.values()) {
			powderTimeouts.put(value, new HashMap<>());
			powderEntries.put(value, new PowderEntry());
		}
	}

	private static void onMessage(Text text, boolean overlay) {
		if (overlay) {
			return;
		}
		if (!LocationUtils.Island.CRYSTAL_HOLLOWS.isActive() && !LocationUtils.Island.DWARVEN_MINES.isActive()) {
			return;
		}
		String literalText = text.getString();
		if (literalText == null || literalText.isEmpty()) {
			return;
		}

		if ("You uncovered a treasure chest!".equalsIgnoreCase(literalText)) {
			powderEntries.get(PowderType.GEMSTONE).updateOther(1);
		} else if ("WOW! You found a Glacite Mineshaft portal!".equalsIgnoreCase(literalText)) {
			if (PowderTrackerHudFoldable.getConfig().trackingType.getValue() == ShaftTrackingType.FIND) {
				powderEntries.get(PowderType.GLACITE).updateOther(1);
			}
			MineshaftEvents.FIND.invoker().run();
		}
	}

	private static void countShaftJoin() {
		if (PowderTrackerHudFoldable.getConfig().trackingType.getValue() == ShaftTrackingType.ENTER) {
			powderEntries.get(PowderType.GLACITE).updateOther(1);
		}
	}

	private static void track(PowderType powderType, int amount, int delta) {
		final Map<Long, Integer> longIntegerCache = powderTimeouts.get(powderType);
		invalidateOutdated(longIntegerCache);
		longIntegerCache.put(System.currentTimeMillis(), delta);
		getPowderEntry(powderType).update(delta);
	}

	public static double getAverage(PowderType powderType) {
		invalidateOutdated(powderTimeouts.get(powderType));
		return powderTimeouts.get(powderType).values().stream().mapToInt(i -> i).average().orElse(0);
	}

	public static Optional<PowderType> getCurrentlyActivePowderType() {
		return Arrays.stream(PowderType.values())
				.map(type -> new Pair<>(type, getAverage(type)))
				.filter(pair -> pair.getRight() != 0)
				.sorted(Comparator.<Pair<PowderType, Double>>comparingDouble(Pair::getRight).reversed())
				.map(Pair::getLeft)
				.findFirst();
	}

	public static PowderEntry getPowderEntry(PowderType powderType) {
		return powderEntries.get(powderType);
	}

	public static void invalidateOutdated(Map<Long, Integer> list) {
		list.keySet()
				.removeIf(timeAdded -> timeAdded + PowderTrackerHudFoldable.getConfig()
						.getTimeoutTime() < System.currentTimeMillis());
	}

}
