package dev.morazzer.cookies.mod.events.api.accessors;

import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.screen.ScreenHandler;

/**
 * Accessor to get the inventory content update event for a screen.
 */
public interface ScreenHandlerUpdateEventAccessor {

    static ScreenHandlerUpdateEventAccessor getInventoryUpdateEventAccessor(ScreenHandler screen) {
        return (ScreenHandlerUpdateEventAccessor) screen;
    }

    Event<InventoryContentUpdateEvent> cookies$inventoryUpdateEvent();

}
