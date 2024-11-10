package codes.cookies.mod.utils.skyblock;

import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.minecraft.SoundUtils;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/**
 * Utils related to inventories.
 */
public class InventoryUtils {

    /**
     * Returns a predicate to check whether an item is an ui background or not.
     * @return Whether the item is an ui background or not.
     */
    public static Predicate<ItemStack> isSkyblockUiElement() {
        return InventoryUtils::isSkyblockUiElement;
    }

    /**
     * Whether the item is an ui element or not.
     * @param itemStack The item.
     * @return Whether it's an ui element or not.
     */
    public static boolean isSkyblockUiElement(ItemStack itemStack) {
        if (itemStack.isOf(Items.BLACK_STAINED_GLASS_PANE)) {
            return true;
        }

        return ItemUtils.getData(itemStack, DataComponentTypes.CUSTOM_NAME) != null &&
               ItemUtils.getData(itemStack, DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP) != null &&
               ItemUtils.getData(itemStack, DataComponentTypes.HIDE_TOOLTIP) != null &&
               Registries.ITEM.getEntry(itemStack.getItem()).getIdAsString().contains("stained_glass_pane");
    }

    /**
     * Wraps the runnable with a lever click sound.
     * @param runnable The runnable.
     * @return The wrapped runnable.
     */
    public static Runnable wrapWithSound(Runnable runnable) {
        return () -> {
            SoundUtils.playSound(SoundEvents.BLOCK_LEVER_CLICK, 1, 1);
            runnable.run();
        };
    }

    /**
     * Wraps the runnable with the provided sound.
     * @param runnable The runnable.
     * @param soundEvent The sound event.
     * @param pitch The pitch of the sound.
     * @param volume The volume of the sound.
     * @return The wrapped runnable.
     */
    public static Runnable wrapWithSound(Runnable runnable, SoundEvent soundEvent, float pitch, float volume) {
        return () -> {
            SoundUtils.playSound(soundEvent, pitch, volume);
            runnable.run();
        };
    }

    /**
     * Wraps the Consumer with a lever click sound.
     * @param consumer The consumer.
     * @return The wrapped consumer.
     */
    public static Consumer<Integer> wrapWithSound(Consumer<Integer> consumer) {
        return button -> {
            SoundUtils.playSound(SoundEvents.BLOCK_LEVER_CLICK, 1, 1);
            consumer.accept(button);
        };
    }

}
