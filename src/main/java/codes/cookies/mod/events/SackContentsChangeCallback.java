package codes.cookies.mod.events;

import codes.cookies.mod.repository.RepositoryItem;

import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import net.fabricmc.fabric.api.event.Event;

import java.util.function.BiConsumer;

public interface SackContentsChangeCallback {

	Event<BiConsumer<RepositoryItem, Integer>> DELTA_CALLBACK = CookiesEventUtils.biConsumer();
	Event<BiConsumer<RepositoryItem, Integer>> CALLBACK = CookiesEventUtils.biConsumer();

}
