package dev.morazzer.cookies.mod.utils.cookies;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Various helper methods for event creations and invocation.
 */
public interface CookiesEventUtils {

	/**
	 * Creates an event that provides a single value.
	 * @return The event.
	 * @param <T> The type of the value.
	 */
	static <T> Event<Consumer<T>> consumer() {
		return EventFactory.createArrayBacked(Consumer.class, consumers -> value -> {
			for (Consumer<T> consumer : consumers) {
				consumer.accept(value);
			}
		});
	}

	/**
	 * Creates an event that will only execute but not provide values.
	 * @return The event.
	 */
	static Event<Runnable> runnable() {
		return EventFactory.createArrayBacked(Runnable.class, runnables -> () -> {
			for (Runnable runnable : runnables) {
				runnable.run();
			}
		});
	}
}
