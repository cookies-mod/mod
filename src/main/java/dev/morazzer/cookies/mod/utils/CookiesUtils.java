package dev.morazzer.cookies.mod.utils;

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
     * Sends a message with the mod prefix {@link Constants#PREFIX} and the success color {@link Constants#SUCCESS_COLOR}
     *
     * @param message The message to send.
     */
    public static void sendSuccessMessage(String message) {
        CookiesUtils.sendMessage(CookiesUtils.createPrefix(Constants.SUCCESS_COLOR).append(message));
    }

}
