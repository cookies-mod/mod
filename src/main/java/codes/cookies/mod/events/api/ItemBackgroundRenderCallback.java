package codes.cookies.mod.events.api;

import codes.cookies.mod.events.api.accessors.ItemBackgroundAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

/**
 * Callback to change the color of the background behind an item.
 */
@FunctionalInterface
public interface ItemBackgroundRenderCallback {

    /**
     * Registers the callback for the provided screen.
     *
     * @param screen   The screen.
     * @param callback The callback.
     */
    static void register(HandledScreen<?> screen, ItemBackgroundRenderCallback callback) {
        ItemBackgroundAccessor.getItemBackgroundAccessor(screen).cookies$itemRenderCallback().register(callback);
    }

    /**
     * Invoked on the render of an item background in a specific screen.
     *
     * @param drawContext The draw context.
     * @param slot        The slot the item is in.
     */
    void renderBackground(DrawContext drawContext, Slot slot);

}
