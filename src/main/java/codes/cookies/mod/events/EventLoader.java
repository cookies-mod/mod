package codes.cookies.mod.events;

import codes.cookies.mod.utils.skyblock.LocationUtils;

import codes.cookies.mod.utils.skyblock.PartyUtils;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

/**
 * Utility class to register all listeners.
 */
@SuppressWarnings("MissingJavadoc")
public interface EventLoader {

    static void load() {
        ClientReceiveMessageEvents.GAME.register(ChatListener::lookForProfileIdMessage);
        ChatMessageEvents.register();
		LocationUtils.register();
		PartyUtils.register();
    }

}
