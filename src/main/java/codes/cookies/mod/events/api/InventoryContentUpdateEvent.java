package codes.cookies.mod.events.api;

import codes.cookies.mod.events.api.accessors.ScreenHandlerUpdateEventAccessor;

import java.util.function.Consumer;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

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
		ScreenHandlerUpdateEventAccessor.getInventoryUpdateEventAccessor(screenHandler)
				.cookies$inventoryUpdateEvent()
				.register(event);
	}

	/**
	 * Registers an update event for the inventory.
	 *
	 * @param screenHandler The screen handler.
	 * @param consumer      The event consumer.
	 */
	static void registerSlot(ScreenHandler screenHandler, Consumer<Slot> consumer) {
		ScreenHandlerUpdateEventAccessor.getInventoryUpdateEventAccessor(screenHandler)
				.cookies$slotUpdateEvent()
				.register(consumer);
	}

	/**
	 * Called when the inventory contents update.
	 *
	 * @param slot The slot the item is in.
	 * @param item The new item in the slot.
	 */
	void updateInventory(int slot, ItemStack item);

}
