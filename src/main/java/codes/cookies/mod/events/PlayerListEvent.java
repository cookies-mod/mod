package codes.cookies.mod.events;

import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import net.fabricmc.fabric.api.event.Event;

import net.minecraft.client.network.PlayerListEntry;

import java.util.function.Consumer;

/**
 * Event related to the player list, will be called on every update.
 */
public interface PlayerListEvent {

	Event<Consumer<PlayerListEntry>> EVENT = CookiesEventUtils.consumer();

}
