package dev.morazzer.cookies.mod.events;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

/**
 * Utility class to register all listeners.
 */
@SuppressWarnings("MissingJavadoc")
public class EventLoader {

    public static void load() {
        ClientReceiveMessageEvents.GAME.register(ChatListener::lookForProfileIdMessage);
    }

}
