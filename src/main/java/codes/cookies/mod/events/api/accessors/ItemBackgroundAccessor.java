package codes.cookies.mod.events.api.accessors;

import codes.cookies.mod.events.api.ItemBackgroundRenderCallback;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * Accessor to get the item background render callback for a screen.
 */
@FunctionalInterface
public interface ItemBackgroundAccessor {

    /**
     * Gets the item background accessor for the specified screen.
     *
     * @param screen The screen.
     * @return The accessor.
     */
    static ItemBackgroundAccessor getItemBackgroundAccessor(HandledScreen<?> screen) {
        return (ItemBackgroundAccessor) screen;
    }

    /**
     * Gets the item render callback for the accessor.
     *
     * @return The item render callback.
     */
    Event<ItemBackgroundRenderCallback> cookies$itemRenderCallback();

}
