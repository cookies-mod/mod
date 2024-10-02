package dev.morazzer.cookies.mod.utils.cookies;

import dev.morazzer.cookies.mod.utils.ColorUtils;

import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various utils that are used a lot of times.
 */
public class CookiesUtils {

	private static final Logger logger = LoggerFactory.getLogger("cookies-utils");

	/**
	 * Creates a text with the default mod color.
	 *
	 * @return The text.
	 */
	public static MutableText createColor() {
		return Text.empty().styled(style -> style.withColor(Constants.MAIN_COLOR));
	}

	/**
	 * Sends a message without the mod prefix.
	 *
	 * @param message The message to send.
	 */
	public static void sendRawMessage(String message) {
		CookiesUtils.sendMessage(Text.literal(message));
	}

	/**
	 * Adds a text message to the chat.
	 *
	 * @param text The message.
	 */
	public static void sendMessage(Text text) {
		sendMessage(text, false);
	}

	/**
	 * Adds a message to the chat.
	 *
	 * @param text    The message.
	 * @param overlay If the message should be sent as overlay or not.
	 */
	public static void sendMessage(Text text, boolean overlay) {
		CookiesUtils.getPlayer().ifPresent(clientPlayerEntity -> clientPlayerEntity.sendMessage(text, overlay));
	}

	/**
	 * Gets the current player.
	 *
	 * @return The current player.
	 */
	public static Optional<ClientPlayerEntity> getPlayer() {
		return Optional.ofNullable(MinecraftClient.getInstance()).map(client -> client.player);
	}

	/**
	 * Sends a message with the mod prefix {@link Constants#PREFIX} and the mod color {@link Constants#MAIN_COLOR}
	 *
	 * @param message The message to send.
	 */
	public static void sendInformation(String message) {
		CookiesUtils.sendMessage(message);
	}

	/**
	 * Adds a message to the chat.
	 *
	 * @param text The message.
	 */
	public static void sendMessage(String text) {
		sendMessage(createPrefix().append(text));
	}

	/**
	 * Creates the prefix as colored text.
	 *
	 * @return The prefix.
	 */
	public static MutableText createPrefix() {
		return Text.literal(Constants.PREFIX).styled(style -> style.withColor(Constants.MAIN_COLOR));
	}

	/**
	 * Sends a message with the mod prefix {@link Constants#PREFIX} in white.
	 *
	 * @param message The message to send.
	 */
	public static void sendWhiteMessage(String message) {
		CookiesUtils.sendMessage(CookiesUtils.createPrefix(-1).append(message));
	}

	/**
	 * Creates the prefix with a gradient from the default mod color to the end color.
	 *
	 * @param endColor The end color.
	 * @return The text.
	 */
	public static MutableText createPrefix(int endColor) {
		return ColorUtils.literalWithGradient(Constants.PREFIX, Constants.MAIN_COLOR, endColor);
	}

	/**
	 * Sends a message with the mod prefix {@link Constants#PREFIX} and the failed color {@link Constants#FAIL_COLOR}
	 *
	 * @param message The message to send.
	 */
	public static void sendFailedMessage(String message) {
		CookiesUtils.sendMessage(CookiesUtils.createPrefix(Constants.FAIL_COLOR).append(message));
	}

	/**
	 * Sends a message with the mod prefix {@link Constants#PREFIX} and the failed color {@link Constants#FAIL_COLOR}
	 *
	 * @param message The message to send.
	 */
	public static void sendFailedMessage(Text message) {
		CookiesUtils.sendMessage(CookiesUtils.createPrefix(Constants.FAIL_COLOR).append(message));
	}

	/**
	 * Sends a message with the mod prefix {@link Constants#PREFIX} and the success color
	 * {@link Constants#SUCCESS_COLOR}
	 *
	 * @param message The message to send.
	 */
	public static void sendSuccessMessage(String message) {
		CookiesUtils.sendMessage(CookiesUtils.createPrefix(Constants.SUCCESS_COLOR).append(message));
	}

	/**
	 * Helper method to allow for various string matching/searching operations.
	 * <br><br>
	 * If the search is prefixed with {@code cookies-regex:} it will cause a regex
	 * match instead of a default string comparison.<br><br>
	 * If the search prefixed with {@code cookies-equals:} it will invoke a check with {@link String#equals(Object)}
	 * .<br><br>
	 * If the search isn't prefixed with any of the above it will invoke a {@link String#equalsIgnoreCase(String)}
	 * check.
	 *
	 * @param string The string to check.
	 * @param search The search to match for details read javadoc above.
	 * @return Whether the two strings match.
	 */
	public static boolean match(String string, String search) {
		String withoutPrefix;
		if (search.startsWith("cookies-")) {
			withoutPrefix = search.substring(search.indexOf(':') + 1);
		} else {
			withoutPrefix = search;
		}
		return switch (search.split(":")[0]) {
			case "cookies-regex" -> string.matches(withoutPrefix);
			case "cookies-equals" -> string.equals(withoutPrefix);
			default -> string.equalsIgnoreCase(search);
		};
	}

	public static String stripColor(String input) {
		return input.replaceAll("§[0-9a-fklmnor]", "");
	}

	public static String formattedMs(long time) {
		StringBuilder stringBuilder = new StringBuilder();
		long milliSeconds = time % 1000;
		long secondsRemaining = time / 1000;
		long seconds = secondsRemaining % 60;
		long minutesRemaining = secondsRemaining / 60;
		long minutes = minutesRemaining % 60;
		long hoursRemaining = minutesRemaining/ 60;
		long hours = hoursRemaining % 24;
		long daysRemaining = hoursRemaining / 24;

		if (daysRemaining != 0) {
			stringBuilder.append(daysRemaining).append("d ");
		}
		if (hours != 0) {
			stringBuilder.append(hours).append("h ");
		}
		if (minutes != 0) {
			stringBuilder.append(minutes).append("m ");
		}
		if (seconds != 0) {
			stringBuilder.append(seconds).append("s ");
		}
		if (milliSeconds != 0) {
			stringBuilder.append(milliSeconds).append("ms");
		}

		return stringBuilder.toString().trim();
	}
}
