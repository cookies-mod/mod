package codes.cookies.mod.features.mining.fiesta;

import java.util.ArrayList;
import java.util.List;

import codes.cookies.mod.events.ChatMessageEvents;
import codes.cookies.mod.events.MiningFiestaEvents;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import net.minecraft.text.Text;

public class MiningFiestaTracker {

	private static final List<Integer> pristineDrops = new ArrayList<>();
	private static int glossyGemstone;

	public static void register() {
		ChatMessageEvents.register(MiningFiestaTracker::trackPristine, "cookies-regex:PRISTINE! You found .*? x\\d+!");
		MiningFiestaEvents.START.register(MiningFiestaTracker::reset);
		MiningFiestaEvents.STOP.register(MiningFiestaTracker::finishFiesta);
	}

	private static void reset() {
		pristineDrops.clear();
		glossyGemstone = 0;
	}

	private static void finishFiesta() {
		CookiesUtils.sendMessage(Text.literal("§m           §r{ Cookies Mod }§m           §r")
				.withColor(Constants.MAIN_COLOR));
		CookiesUtils.sendRawMessage("");
		CookiesUtils.sendRawMessage("Total Pristine Drops: " + pristineDrops.size());
		CookiesUtils.sendRawMessage("Flawed Through Pristine: " + pristineDrops.stream()
				.mapToInt(Integer::intValue)
				.sum());
		CookiesUtils.sendRawMessage("Glossy Gemstones Obtained: " + glossyGemstone);
		CookiesUtils.sendRawMessage("Active for: " + CookiesUtils.formattedMs((System.currentTimeMillis() - MiningFiesta.getTimeStarted())));
		CookiesUtils.sendRawMessage("");
		CookiesUtils.sendMessage(Text.literal("§m                                         §r").withColor(Constants.MAIN_COLOR));
	}

	private static void trackPristine(String message) {
		if (!MiningFiesta.isActive()) {
			return;
		}

		final String literalAmount = message.replaceAll("\\D", "");
		if (literalAmount.isEmpty()) {
			return;
		}

		final int amount;
		try {
			amount = Integer.parseInt(literalAmount);
		} catch (NumberFormatException e) {
			return;
		}

		pristineDrops.add(amount);
	}

	public static void trackGlossyGemstone(int amount) {
		MiningFiesta.startIfNotActive();
		if (!MiningFiesta.isActive()) {
			return;
		}

		glossyGemstone += amount;
	}

}
