package codes.cookies.mod.features.mining.utils;

import codes.cookies.mod.config.categories.mining.MiningConfig;
import codes.cookies.mod.events.SackContentsChangeCallback;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.cookies.CookiesUtils;

public class GlossyGemstoneMessage {

	public static void register() {
		SackContentsChangeCallback.DELTA_CALLBACK.register(GlossyGemstoneMessage::update);
	}

	public static void update(RepositoryItem repositoryItem, Integer integer) {
		if (!MiningConfig.getInstance().glossyGemstoneMessages.getValue()) {
			return;
		}

		if (!repositoryItem.getInternalId().equals("GLOSSY_GEMSTONE")) {
			return;
		}

		if (integer == null) {
			return;
		}

		if (integer > 1) {
			CookiesUtils.sendWhiteMessage("Found %s glossy gemstones!".formatted(integer));
		} else if (integer == 1) {
			CookiesUtils.sendWhiteMessage("Found %s glossy gemstone!".formatted(integer));
		}
	}

}
