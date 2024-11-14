package codes.cookies.mod.features.misc.timer;

import java.util.Optional;

import codes.cookies.mod.config.categories.MiscConfig;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.minecraft.SoundUtils;
import lombok.Getter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class Timer {
	private final MiscConfig.NotificationFoldable.TimerConfig timerFoldable;
	private final Identifier debug;
	@Getter
	boolean hasBeenAlerted = true;

	public Timer(MiscConfig.NotificationFoldable.TimerConfig timerFoldable, String id) {
		this.timerFoldable = timerFoldable;
		this.debug = DevUtils.createIdentifier("timer/" + id);
	}

	boolean isDeactivated() {
		return !timerFoldable.enabled();
	}

	NotificationManager.NotificationType getNotificationType() {
		return timerFoldable.notificationType();
	}

	boolean playSound() {
		return timerFoldable.enableSound();
	}

	boolean showNotification() {
		return true;
	}

	int getAlertTime() {
		return 10;
	}

	abstract void onChatMessage(String message);

	boolean isDebug() {
		return DevUtils.isEnabled(debug);
	}

	Optional<String> getDebug() {
		if (!isDebug()) {
			return Optional.empty();
		}
		return Optional.of("Timer: " + getTime());
	}

	abstract Text getNotificationMessage();

	abstract Text getChatMessage();

	abstract int getTime();

	void alert() {
		hasBeenAlerted = true;
		if (!showNotification()) {
			return;
		}
		if (this.getNotificationType() == NotificationManager.NotificationType.NONE) {
			return;
		}
		if (playSound()) {
			SoundUtils.playSound(SoundEvents.BLOCK_BELL_USE, 1, 2);
		}
		switch (this.getNotificationType()) {
			case BOTH -> this.alertBoth();
			case TOAST -> this.sendToast();
			case CHAT -> this.sendChat();
		}
	}

	private void sendChat() {
		CookiesUtils.sendMessage(this.getChatMessage());
	}

	private void sendToast() {
		MinecraftClient.getInstance()
				.getToastManager()
				.add(new SbEntityToast(this.getData(), this::getNotificationMessage, 3000));
	}

	private void alertBoth() {
		this.sendChat();
		this.sendToast();
	}

	abstract SbEntityToast.ImageData getData();

}
