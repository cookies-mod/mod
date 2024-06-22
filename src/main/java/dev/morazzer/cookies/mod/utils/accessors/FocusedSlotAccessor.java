package dev.morazzer.cookies.mod.utils.accessors;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

/**
 * Interface to access the focused slot field.
 */
public interface FocusedSlotAccessor {

    /**
     * Gets the focused slot from the handled screen.
     *
     * @param handledScreen The screen.
     * @return The slot.
     */
    static Slot getFocusedSlot(HandledScreen<?> handledScreen) {
        return ((FocusedSlotAccessor) handledScreen).cookies$getFocusedSlot();
    }

    /**
     * Invoker to get the slot.
     *
     * @return The slot.
     */
    Slot cookies$getFocusedSlot();

}
