package dev.morazzer.cookies.mod.events.dungeon;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonPhase;

import dev.morazzer.cookies.mod.utils.cookies.CookiesEventUtils;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.event.Event;

/**
 * Various events related to dungeons.
 */
public interface DungeonEvents {

	/**
	 * Called when the dungeon phase changes, this can also skip a phase if the player wasn't in the dungeon during
	 * said phase.
	 */
	Event<Consumer<DungeonPhase>> DUNGEON_PHASE_CHANGE = CookiesEventUtils.consumer();
	/**
	 * Called whenever the player joins a dungeon, this may be invoked multiple times for any given dungeon if the
	 * player reconnects to said dungeon.
	 */
	Event<Runnable> JOIN_DUNGEON = CookiesEventUtils.runnable();
	/**
	 * Called whenever the player leaves a dungeon, this may be invoked multiple time for any given dungeon if the
	 * player disconnects after reconnecting.
	 */
	Event<Runnable> LEAVE_DUNGEON = CookiesEventUtils.runnable();

}
