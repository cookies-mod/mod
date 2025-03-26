package codes.cookies.mod.features.mining;

import java.util.Optional;

import codes.cookies.mod.data.mining.PowderData;
import codes.cookies.mod.data.mining.PowderType;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.events.ChatMessageEvents;
import codes.cookies.mod.events.PlayerListWidgetEvent;
import codes.cookies.mod.events.ScoreboardUpdateEvent;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.playerlist.widgets.PlayerListWidgets;
import codes.cookies.mod.utils.skyblock.playerlist.widgets.powder.PowderWidget;
import lombok.extern.slf4j.Slf4j;

import net.minecraft.text.Text;

@Slf4j
public class PowderTracker {

	private static long lastMithrilUpdate = -1;
	private static long lastGemstoneUpdate = -1;
	private static long lastGlaciteUpdate = -1;

	public static void load() {
		ScoreboardUpdateEvent.EVENT.register(PowderTracker::updateScoreboard);
		ChatMessageEvents.BEFORE_MODIFY.register(PowderTracker::onChat);
		PlayerListWidgetEvent.register(PlayerListWidgets.POWDER, PowderTracker::parseWidget);
	}

	private static void parseWidget(PowderWidget powderWidget) {
		powderWidget.getMithrilPowder().ifPresent(powder -> updatePowderFromList(PowderType.MITHRIL, powder));
		powderWidget.getGemstonePowder().ifPresent(powder -> updatePowderFromList(PowderType.GEMSTONE, powder));
		powderWidget.getGlacitePowder().ifPresent(powder -> updatePowderFromList(PowderType.GLACITE, powder));
	}

	private static void updateWidgetTime(PowderType powderType) {
		switch (powderType) {
			case MITHRIL -> lastMithrilUpdate = System.currentTimeMillis();
			case GEMSTONE -> lastGemstoneUpdate = System.currentTimeMillis();
			case GLACITE -> lastGlaciteUpdate = System.currentTimeMillis();
		}
	}

	private static long getLastUpdateTime(PowderType powderType) {
		return switch (powderType) {
			case MITHRIL -> lastMithrilUpdate;
			case GEMSTONE -> lastGemstoneUpdate;
			case GLACITE -> lastGlaciteUpdate;
		};
	}

	private static void onChat(Text text, boolean overlay) {
		if (overlay) {
			return;
		}
		String literalText = CookiesUtils.stripColor(text.getString());
		if (literalText == null || literalText.isEmpty()) {
			return;
		}


		if ("Reset your Heart of the Mountain! Your Perks and Abilities have been reset.".equalsIgnoreCase(literalText)) {
			ProfileStorage.getCurrentProfile().map(ProfileData::getPowderData).ifPresent(PowderData::reset);
		}
	}

	private static void updateScoreboard(int line, String text) {
		if (!text.startsWith("᠅")) {
			return;
		}
		final String nameAndPowder = text.substring(2);
		if (!nameAndPowder.contains(" ")) {
			return;
		}

		final String name = nameAndPowder.substring(0, nameAndPowder.indexOf(' ') - 1);
		final String powder = nameAndPowder.substring(nameAndPowder.indexOf(' ') + 1);

		final Optional<PowderType> powderType = PowderType.getByDisplayName(name);
		powderType.ifPresent(type -> PowderTracker.updatePowderFromScoreboard(type, powder));
	}

	private static void updatePowderFromList(PowderType powderType, int powder) {
		updateWidgetTime(powderType);
		updatePowder(powderType, powder);
	}

	private static void updatePowderFromScoreboard(PowderType powderType, String powder) {
		if (getLastUpdateTime(powderType) + 2000 > System.currentTimeMillis()) {
			return;
		}

		try {
			updatePowder(powderType, Integer.parseInt(powder.replaceAll("\\W", "")));
		} catch (NumberFormatException e) {
			log.warn("Failed to parse powder of {} {}", powderType, powder);
		}
	}

	private static void updatePowder(PowderType powderType, int powder) {
		ProfileStorage.getCurrentProfile().ifPresent(profile -> profile.getPowderData().update(powderType, powder));
	}

}
