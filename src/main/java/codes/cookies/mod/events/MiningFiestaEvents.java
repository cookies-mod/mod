package codes.cookies.mod.events;

import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import net.fabricmc.fabric.api.event.Event;

public interface MiningFiestaEvents {

	Event<Runnable> START = CookiesEventUtils.runnable();
	Event<Runnable> STOP = CookiesEventUtils.runnable();

}
