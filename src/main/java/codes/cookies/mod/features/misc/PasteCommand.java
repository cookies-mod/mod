package codes.cookies.mod.features.misc;

import codes.cookies.mod.CookiesMod;

import codes.cookies.mod.utils.cookies.CookiesUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class PasteCommand
{
	/* private void sendMessage(String message, boolean hide) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            Scheduler.LOGGER.error("[Skyblocker Message Scheduler] Tried to send a message while player is null: {}", message);
            return;
        }
        message = StringHelper.truncateChat(StringUtils.normalizeSpace(message.trim()));

        if (message.startsWith("/")) {
            client.player.networkHandler.sendCommand(message.substring(1));
        } else {
			if (!hide) client.inGameHud.getChatHud().addToMessageHistory(message);
			client.player.networkHandler.sendChatMessage(message);
        }
    }*/
}
