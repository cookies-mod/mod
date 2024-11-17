package codes.cookies.mod.features.misc.timer;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Timer for the primal fear cooldown.
 */
public class FearTimer extends Timer {
	private static final SbEntityToast.ImageData DATA = new SbEntityToast.ImageData(
			12,
			25,
			10,
			4,
			Identifier.of("cookies-mod", "textures/mobs/primal_fear.png"));
	private long lastFearSpawnedAt = -1;

	public FearTimer() {
		super(ConfigManager.getConfig().miscConfig.notificationFoldable, "fear");
	}

	@Override
	public void onChatMessage(String message) {
		if (message.equals("FEAR. A Primal Fear has been summoned!")) {
			lastFearSpawnedAt = System.currentTimeMillis();
			hasBeenAlerted = false;
		}
	}

	private int getTimeToWait() {
		return (int) (360 - 3 * getFear());
	}

	private double getFear() {
		return ProfileStorage.getCurrentProfile()
				.map(profile -> profile.getProfileStats().getStat("fear"))
				.map(optionalDouble -> optionalDouble.orElse(0))
				.orElse(0d);
	}

	@Override
	public Text getNotificationMessage() {
		return Text.literal("Primal fear in " + getTime() + "s!").formatted(Formatting.YELLOW);
	}

	@Override
	public Text getChatMessage() {
		return CookiesUtils.createPrefix(0xFFA933DC).append("You can spawn a primal fear soon! (10s)");
	}

	@Override
	public int getTime() {
		int timeDelta = (int) ((System.currentTimeMillis() - lastFearSpawnedAt) / 1000);
		return getTimeToWait() - timeDelta;
	}

	@Override
	SbEntityToast.ImageData getData() {
		return DATA;
	}
}
