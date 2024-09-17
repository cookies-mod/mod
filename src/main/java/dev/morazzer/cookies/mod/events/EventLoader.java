package dev.morazzer.cookies.mod.events;

import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import dev.morazzer.cookies.mod.utils.skyblock.PartyUtils;

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
