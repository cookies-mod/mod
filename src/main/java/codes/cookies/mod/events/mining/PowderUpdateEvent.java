package codes.cookies.mod.events.mining;

import codes.cookies.mod.data.mining.PowderType;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PowderUpdateEvent {

	Event<PowderUpdateEvent> EVENT = EventFactory.createArrayBacked(
			PowderUpdateEvent.class,
			(listeners) -> ((powderType, amount, delta) -> {
				for (PowderUpdateEvent listener : listeners) {
					listener.update(powderType, amount, delta);
				}
			}));

	void update(PowderType powderType, int amount, int delta);

}
