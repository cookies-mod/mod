package dev.morazzer.cookies.mod.features.misc.timer;

import java.util.OptionalDouble;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class NotificationManager {
	private static final Identifier DEBUG = DevUtils.createIdentifier("notifications/fear");
	private static long lastFearSpawnedAt = -1;
	private static boolean hasBeenAlerted = true;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(NotificationManager::afterTick);
		HudRenderCallback.EVENT.register(NotificationManager::debugRendering);
		ClientReceiveMessageEvents.ALLOW_GAME.register(NotificationManager::onMessage);
	}

	private static boolean isFearNotificationsDisabled() {
		return !ConfigManager.getConfig().miscConfig.notificationFoldable.enablePrimalFearNotifications.getValue();
	}

	private static NotificationType getNotificationType() {
		return ConfigManager.getConfig().miscConfig.notificationFoldable.type.getValue();
	}

	private static boolean shouldPlaySound() {
		return ConfigManager.getConfig().miscConfig.notificationFoldable.enableSound.getValue();
	}

	private static void debugRendering(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (isFearNotificationsDisabled()) {
			return;
		}
		if (DevUtils.isDisabled(DEBUG)) {
			return;
		}
		ProfileStorage.getCurrentProfile().ifPresent(profile -> {
			final OptionalDouble fear = profile.getProfileStats().getStat("fear");

			final double fearAmount = fear.orElse(0.0);
			int timeDelta = (int) ((System.currentTimeMillis() - lastFearSpawnedAt) / 1000);
			int timeToWait = (int) (360 - 3 * fearAmount);
			drawContext.drawText(
					MinecraftClient.getInstance().textRenderer,
					"Fear in: " + (timeToWait - timeDelta),
					3,
					20,
					-1,
					true);
		});
	}

	private static boolean onMessage(Text text, boolean overlay) {
		if (!isFearNotificationsDisabled() && !overlay) {
			final String string = text.getString();
			if (CookiesUtils.stripColor(string).trim().equals("FEAR. A Primal Fear has been summoned!")) {
				lastFearSpawnedAt = System.currentTimeMillis();
				hasBeenAlerted = false;
			}
		}

		return true;
	}

	private static void afterTick(MinecraftClient minecraftClient) {
		if (isFearNotificationsDisabled()) {
			return;
		}

		lastFearSpawnedAt++;
		ProfileStorage.getCurrentProfile().ifPresent(profile -> {
			final OptionalDouble fear = profile.getProfileStats().getStat("fear");

			final double fearAmount = fear.orElse(0.0D);
			int timeToWait = (int) (360 - 3 * fearAmount) * 1000;
			if (!hasBeenAlerted && lastFearSpawnedAt + (timeToWait - 10000) < System.currentTimeMillis()) {
				switch (getNotificationType()) {
					case BOTH -> sendAll(timeToWait);
					case CHAT -> sendChatWithSound();
					case TOAST -> sendToastWithSound(timeToWait);
				}

				hasBeenAlerted = true;
			}
		});
	}

	public static void sendAll(int timeToWait) {
		sendChat();
		sendToast(timeToWait);
		playSound();
	}

	public static void playSound() {
		if (shouldPlaySound()) {
			SoundUtils.playSound(SoundEvents.BLOCK_BELL_USE, 1, 2);
		}
	}

	public static void sendChat() {
		CookiesUtils.sendMessage(CookiesUtils.createPrefix(0xFFA933DC)
				.append("You can spawn a primal fear soon! (10s)"));
	}

	public static void sendChatWithSound() {
		sendChat();
		playSound();
	}

	public static void sendToast(int timeToWait) {
		MinecraftClient.getInstance().getToastManager().add(
				new SbEntityToast(
						Identifier.of("cookies-mod", "textures/mobs/primal_fear.png"),
						() -> {
							int timeDelta = (int) ((System.currentTimeMillis() - lastFearSpawnedAt) / 1000);
							return Text.literal("Primal fear in " + ((timeToWait / 1000) - timeDelta) + "s!")
									.formatted(Formatting.YELLOW);
						}, 3000)
		);
	}

	public static void sendToastWithSound(int timeToWait) {
		sendToast(timeToWait);
		playSound();
	}

	public enum NotificationType {
		CHAT, TOAST, BOTH
	}

}
