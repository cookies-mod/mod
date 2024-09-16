package dev.morazzer.cookies.mod.events;

import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events related to island change
 */
public interface IslandChangeEvent {

	Event<IslandChangeEvent> EVENT =
			EventFactory.createArrayBacked(IslandChangeEvent.class, callbacks -> (previous, current) -> {
				for (IslandChangeEvent callback : callbacks) {
					callback.onIslandChange(previous, current);
				}
			});

	/**
	 * Called everytime the player swaps island.
	 * @param previous The previous island.
	 * @param current The new island.
	 */
	void onIslandChange(LocationUtils.Island previous, LocationUtils.Island current);
}
