package codes.cookies.mod.utils.skyblock;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatUtils {
	/**
	 * Sends a message without specifying a channel.
	 */
	public static void sendRawMessage(String message) {
		CookiesUtils.getPlayer().ifPresent(clientPlayerEntity -> clientPlayerEntity.networkHandler.sendChatMessage(message));
	}


	/**
	 * Sends a message to the party channel.
	 */
	public static void sendPartyMessage(String message) {
		if (!PartyUtils.isInParty()) {
			return;
		}

		CookiesUtils.sendCommand("pchat " + message);
	}


	/**
	 * Sends a message to the all channel.
	 */
	public static void sendAllMessage(String message) {
		CookiesUtils.sendCommand("achat " + message);
	}
}
