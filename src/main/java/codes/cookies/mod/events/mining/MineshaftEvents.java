package codes.cookies.mod.events.mining;

import codes.cookies.mod.repository.constants.mining.ShaftCorpseLocations;

import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import net.fabricmc.fabric.api.event.Event;

import java.util.function.Consumer;

public interface MineshaftEvents {

	Event<Consumer<ShaftCorpseLocations.ShaftLocations>> JOIN_SHAFT = CookiesEventUtils.consumer();
	Event<Runnable> JOIN = CookiesEventUtils.runnable();
	Event<Runnable> LEAVE = CookiesEventUtils.runnable();

}
