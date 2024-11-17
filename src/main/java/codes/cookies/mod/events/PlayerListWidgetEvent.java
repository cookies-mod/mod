package codes.cookies.mod.events;

import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import codes.cookies.mod.utils.skyblock.tab.widgets.PlayerListWidget;

import codes.cookies.mod.utils.skyblock.tab.widgets.PlayerListWidgets;

import net.fabricmc.fabric.api.event.Event;

import java.util.function.Consumer;

/**
 * Called for all player list widgets that are found in the list.
 */
public interface PlayerListWidgetEvent {

	Event<Consumer<PlayerListWidget>> EVENT = CookiesEventUtils.consumer();

	static <T extends PlayerListWidget> void register(PlayerListWidgets.Entry<T> entry, Consumer<T> consumer) {
		EVENT.register(widget -> {
			if (entry.clazz().isInstance(widget)) {
				consumer.accept((T) widget);
			}
		});
	}

}
