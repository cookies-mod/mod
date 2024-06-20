package dev.morazzer.cookies.mod.events;

import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

/**
 * Event to modify the item description without the need for an extra mixin.
 */
public interface ItemLoreEvent {

    /**
     * Event that is called on creation of an item with a description.
     */
    Event<ItemLoreEvent> EVENT =
        EventFactory.createArrayBacked(ItemLoreEvent.class, itemLoreEvents -> instance -> {
            for (ItemLoreEvent itemLoreEvent : itemLoreEvents) {
                itemLoreEvent.modify(instance);
            }
        });


    /**
     * Modifies the item lore.
     *
     * @param list The list of entries.
     */
    void modify(List<Text> list);
}
