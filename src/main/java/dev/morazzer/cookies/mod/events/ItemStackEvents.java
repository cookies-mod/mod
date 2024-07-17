package dev.morazzer.cookies.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

/**
 * Item stack creation event.
 */
public interface ItemStackEvents {

    /**
     * An event that is called whenever an item stack is created.
     */
    Event<ItemStackEvents> EVENT = EventFactory.createArrayBacked(ItemStackEvents.class, events -> stack -> {
        for (ItemStackEvents event : events) {
            event.create(stack);
        }
    });

    /**
     * Callback to modify item stacks on creation.
     * @param stack The item stack that was created.
     */
    void create(ItemStack stack);

}
