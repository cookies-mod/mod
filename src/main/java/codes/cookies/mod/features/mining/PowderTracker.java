package codes.cookies.mod.features.mining;

import java.util.Optional;

import codes.cookies.mod.data.mining.PowderData;
import codes.cookies.mod.data.mining.PowderType;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.events.ChatMessageEvents;
import codes.cookies.mod.events.ScoreboardUpdateEvent;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import lombok.extern.slf4j.Slf4j;

import net.minecraft.text.Text;

/**
 * Manager to handle powder updates and keep track.
 */
@Slf4j
public class PowderTracker {

	public static void load() {
		ScoreboardUpdateEvent.EVENT.register(PowderTracker::updateScoreboard);
		ChatMessageEvents.BEFORE_MODIFY.register(PowderTracker::onChat);
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
		powderType.ifPresent(type -> PowderTracker.updatePowder(type, powder));
	}

	private static void updatePowder(PowderType powderType, String powder) {
		ProfileStorage.getCurrentProfile().ifPresent(profile -> {
			try {
				profile.getPowderData().update(powderType, Integer.parseInt(powder.replaceAll("\\W", "")));
			} catch (NumberFormatException e) {
				log.warn("Failed to parse powder of {} {}", powderType, powder);
			}
		});
	}

}
