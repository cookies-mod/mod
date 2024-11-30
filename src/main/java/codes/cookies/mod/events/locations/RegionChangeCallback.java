package codes.cookies.mod.events.locations;

import codes.cookies.mod.generated.Regions;
import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import net.fabricmc.fabric.api.event.Event;

import java.util.function.Consumer;

public interface RegionChangeCallback {

	Event<Consumer<Regions>> CALLBACK = CookiesEventUtils.consumer();

}
