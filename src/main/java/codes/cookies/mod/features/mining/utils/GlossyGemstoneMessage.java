package codes.cookies.mod.features.mining.utils;

import codes.cookies.mod.config.categories.mining.MiningConfig;
import codes.cookies.mod.events.SackContentsChangeCallback;
import codes.cookies.mod.features.mining.fiesta.MiningFiestaTracker;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.cookies.CookiesUtils;

public class GlossyGemstoneMessage {

	public static void register() {
		SackContentsChangeCallback.DELTA_CALLBACK.register(GlossyGemstoneMessage::update);
	}

	public static void update(RepositoryItem repositoryItem, Integer amount) {

		if (!repositoryItem.getInternalId().equals("GLOSSY_GEMSTONE")) {
			return;
		}

		if (amount == null) {
			return;
		}

		if (amount >= 0) {
			MiningFiestaTracker.trackGlossyGemstone(amount);
		}

		if (!MiningConfig.getInstance().glossyGemstoneMessages.getValue()) {
			return;
		}

		if (amount > 1) {
			CookiesUtils.sendWhiteMessage("Found %s glossy gemstones!".formatted(amount));
		} else if (amount == 1) {
			CookiesUtils.sendWhiteMessage("Found %s glossy gemstone!".formatted(amount));
		}
	}

}
