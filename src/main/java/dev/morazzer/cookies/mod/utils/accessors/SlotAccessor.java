package dev.morazzer.cookies.mod.utils.accessors;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/**
 * Accessor to for setting an override item.
 */
public interface SlotAccessor {

    /**
     * Sets the on click consumer for the slot.
     *
     * @param slot    The slot.
     * @param onClick The item.
     */
    static void setOnClick(Slot slot, Consumer<Integer> onClick) {
        getAccessor(slot).cookies$setOnClick(onClick);
    }

    void cookies$setOnClick(Consumer<Integer> onClick);

    private static SlotAccessor getAccessor(Slot slot) {
        //The cast is possible, though it correctly assumes it is not
        //noinspection CastToIncompatibleInterface
        return (SlotAccessor) slot;
    }

    /**
     * Gets the on click consumer for the slot.
     *
     * @param slot The slot.
     * @return The consumer.
     */
    static Consumer<Integer> getOnClick(Slot slot) {
        return getAccessor(slot).cookies$getOnClick();
    }

    Consumer<Integer> cookies$getOnClick();

    /**
     * Sets the "new" item for the slot.
     *
     * @param slot      The slot.
     * @param itemStack The item.
     */
    static void setItem(Slot slot, ItemStack itemStack) {
        getAccessor(slot).cookies$setOverrideItem(itemStack);
    }

    @SuppressWarnings("MissingJavadoc")
    void cookies$setOverrideItem(ItemStack itemStack);

    /**
     * Sets the runnable.
     *
     * @param slot     The slot.
     * @param runnable The runnable.
     */
    static void setRunnable(Slot slot, Runnable runnable) {
        getAccessor(slot).cookies$setRunnable(runnable);
    }

    @SuppressWarnings("MissingJavadoc")
    void cookies$setRunnable(Runnable runnable);

    /**
     * Gets the runnable.
     *
     * @param slot The slot.
     * @return The runnable.
     */
    static Runnable getRunnable(Slot slot) {
        return getAccessor(slot).cookies$getRunnable();
    }

    @SuppressWarnings("MissingJavadoc")
    Runnable cookies$getRunnable();

    /**
     * Gets the item of the slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    static ItemStack getItem(Slot slot) {
        return getAccessor(slot).cookies$getOverrideItem();
    }

    @SuppressWarnings("MissingJavadoc")
    ItemStack cookies$getOverrideItem();

}
