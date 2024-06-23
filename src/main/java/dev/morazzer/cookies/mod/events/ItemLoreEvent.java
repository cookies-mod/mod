package dev.morazzer.cookies.mod.events;

import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;

/**
 * Event to modify the item description without the need for an extra mixin.
 */
public interface ItemLoreEvent {

    /**
     * Event that is called on creation of an item with a description.
     */
    Event<Consumer<List<MutableText>>> EVENT =
        EventFactory.createArrayBacked(Consumer.class, itemLoreEvents -> instance -> {
            for (Consumer<List<MutableText>> itemLoreEvent : itemLoreEvents) {
                itemLoreEvent.accept(instance);
            }
        });


    /**
     * Event that is called on creation of an item with a description.
     */
    Event<ItemLoreEvent> EVENT_ITEM =
        EventFactory.createArrayBacked(ItemLoreEvent.class, itemLoreEvents -> (itemStack, lore) -> {
            for (ItemLoreEvent itemLoreEvent : itemLoreEvents) {
                itemLoreEvent.modify(itemStack, lore);
            }
        });


    /**
     * Modifies the item lore.
     *
     * @param itemStack The item stack to modify.
     * @param list      The list of entries.
     */
    void modify(ItemStack itemStack, List<MutableText> list);
}
