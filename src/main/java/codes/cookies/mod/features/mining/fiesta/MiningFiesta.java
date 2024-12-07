package codes.cookies.mod.features.mining.fiesta;

import codes.cookies.mod.events.ChatMessageEvents;
import codes.cookies.mod.events.MiningFiestaEvents;
import lombok.Getter;

public class MiningFiesta {

	private static boolean active;
	@Getter
	private static long timeStarted;
	private static final long FIESTA_TIME =( 60 * 60 * 1000 * 2) + (20 * 60 * 1000);

	public static void register() {
		active = false;
		timeStarted = -1;
		ChatMessageEvents.EVENT.register(MiningFiesta::handleMessage);
	}

	private static void handleMessage(String content) {
		if ("MINING FIESTA is now underway! Equip your pickaxe and head to the mines!".equalsIgnoreCase(content.trim())) {
			start();
		} else if ("MINING FIESTA has concluded! Put your pickaxe down and haul your ores home!".equalsIgnoreCase(content.trim())) {
			stop();
		}
	}

	public static boolean isActive() {
		if (timeStarted + FIESTA_TIME < System.currentTimeMillis()) {
			active = false;
		}

		return active;
	}

	public static void startIfNotActive() {
		if (isActive()) {
			return;
		}

		start();
	}

	public static void start() {
		timeStarted = System.currentTimeMillis();
		active = true;
		MiningFiestaEvents.START.invoker().run();
	}

	public static void stop() {
		active = false;
		MiningFiestaEvents.STOP.invoker().run();
		timeStarted = -1;
	}

}
