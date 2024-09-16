package dev.morazzer.cookies.mod.events;

import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.text.Text;

/**
 * Event wrapper for chat messages to remove boilerplate code.
 */
public interface ChatMessageEvent {

	/**
	 * Event that will trigger on chat messages.
	 */
	Event<ChatMessageEvent> EVENT = EventFactory.createArrayBacked(ChatMessageEvent.class, callbacks -> message -> {
		for (ChatMessageEvent callback : callbacks) {
			callback.onMessage(message);
		}
	});

	/**
	 * Registers the underlying chat message listener.
	 */
	static void register() {
		ClientReceiveMessageEvents.GAME.register(ChatMessageEvent::game);
	}

	/**
	 * Listens to all messages sent by the server.
	 *
	 * @param text The message.
	 * @param b    Whether the message is in the hotbar or not, it will be ignored if it is.
	 */
	static void game(Text text, boolean b) {
		if (b) {
			return;
		}
		final String string = text.getString();
		EVENT.invoker().onMessage(string);
	}

	/**
	 * Registers a chat listener with a search attached to it. The listener will only be invoked if the search is
	 * present in the message.
	 *
	 * @param event  The listener to register.
	 * @param search The search.
	 * @see CookiesUtils#match(String, String) Here for more information on  the search string
	 */
	static void register(final ChatMessageEvent event, final String search) {
		EVENT.register(message -> {
			if (CookiesUtils.match(message, search)) {
				event.onMessage(message);
			}
		});
	}

	/**
	 * Invoked with the literal content of a message if the listeners condition matches the message.
	 *
	 * @param message The literal message.
	 */
	void onMessage(String message);

}
