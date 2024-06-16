package dev.morazzer.cookies.mod.events.api;

import dev.morazzer.cookies.mod.events.api.accessors.ScreenHandlerUpdateEventAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

/**
 * Called whenever the inventory contents of a specific screen handler update.
 */
public interface InventoryContentUpdateEvent {

    /**
     * Registers an {@linkplain InventoryContentUpdateEvent} for the given {@linkplain ScreenHandler}.
     *
     * @param screenHandler The screen handler.
     * @param event         The event.
     */
    static void register(ScreenHandler screenHandler, InventoryContentUpdateEvent event) {
        ScreenHandlerUpdateEventAccessor
            .getInventoryUpdateEventAccessor(screenHandler)
            .cookies$inventoryUpdateEvent()
            .register(event);
    }

    /**
     * Called when the inventory contents update.
     *
     * @param slot The slot the item is in.
     * @param item The new item in the slot.
     */
    void updateInventory(int slot, ItemStack item);

}
