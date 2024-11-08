package codes.cookies.mod.events.api.accessors;

import codes.cookies.mod.events.api.InventoryContentUpdateEvent;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.event.Event;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Accessor to get the inventory content update event for a screen.
 */
public interface ScreenHandlerUpdateEventAccessor {

    static ScreenHandlerUpdateEventAccessor getInventoryUpdateEventAccessor(ScreenHandler screen) {
        return (ScreenHandlerUpdateEventAccessor) screen;
    }

    Event<InventoryContentUpdateEvent> cookies$inventoryUpdateEvent();
	Event<Consumer<Slot>> cookies$slotUpdateEvent();
}
