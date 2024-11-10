package codes.cookies.mod.events;

import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import net.fabricmc.fabric.api.event.Event;

/**
 * An event that runs every time the mod connects to the backend server, this may be used to send event subscriptions
 * or other data that gives context to the backend.
 */
public interface WebsocketEvent {

	/**
	 * Invoked whenever the websocket successfully connects to the backend.
	 */
	Event<Runnable> CONNECT = CookiesEventUtils.runnable();

	/**
	 * Invoked whenever the websocket disconnected from the backend.
	 */
	Event<Runnable> DISCONNECT = CookiesEventUtils.runnable();

}
