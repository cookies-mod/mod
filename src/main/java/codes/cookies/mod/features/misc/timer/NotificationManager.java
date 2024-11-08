package codes.cookies.mod.features.misc.timer;

import java.util.ArrayList;
import java.util.List;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class NotificationManager {
	private static final Identifier DEBUG = DevUtils.createIdentifier("timer/enable_debug");
	static List<Timer> timers = new ArrayList<>();

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(NotificationManager::afterTick);
		HudRenderCallback.EVENT.register(NotificationManager::debugRendering);
		ClientReceiveMessageEvents.ALLOW_GAME.register(NotificationManager::onMessage);
		timers = List.of(
				new FearTimer(),
				new PestTimer()
		);
	}

	private static void debugRendering(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (DevUtils.isDisabled(DEBUG)) {
			return;
		}
		int y = 0;
		for (Timer timer : timers) {
			if (timer.getDebug().isPresent()) {
				drawContext.drawText(
						MinecraftClient.getInstance().textRenderer,
						timer.getDebug().get(),
						3,
						20 + (y++ * 10),
						-1,
						true);
			}
		}
	}

	private static boolean onMessage(Text text, boolean overlay) {
		final String string = text.getString();
		final String literalMessage = CookiesUtils.stripColor(string).trim();
		for (Timer timer : timers) {
			if (timer.isDeactivated()) {
				continue;
			}
			timer.onChatMessage(literalMessage);
		}

		return true;
	}

	private static void afterTick(MinecraftClient minecraftClient) {
		for (Timer timer : timers) {
			if (timer.isDeactivated()) {
				continue;
			}
			final int time = timer.getTime();
			if (time <= timer.getAlertTime() && !timer.hasBeenAlerted) {
				timer.alert();
			}
		}
	}

	public enum NotificationType {
		CHAT, TOAST, BOTH
	}

}
