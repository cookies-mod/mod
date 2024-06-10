package dev.morazzer.cookies.mod.utils.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

/**
 * Item related utility methods.
 */
public class ItemUtils {

    private static final ItemFunctions MAIN_HAND = () -> MinecraftClient.getInstance().player.getMainHandStack();
    private static final ItemFunctions OFF_HAND = () -> MinecraftClient.getInstance().player.getOffHandStack();

    /**
     * Gets the main hands item.
     *
     * @return The item.
     */
    @NotNull
    public static ItemFunctions getMainHand() {
        return MAIN_HAND;
    }

    /**
     * Gets the off hands item.
     *
     * @return The item.
     */
    @NotNull
    public static ItemFunctions getOffHand() {
        return OFF_HAND;
    }

    /**
     * Gets the custom data {@linkplain NbtComponent} of an item.
     *
     * @param itemStack The item.
     * @return The data.
     */
    @NotNull
    public static Value<NbtCompound> skyblockTag(ItemStack itemStack) {
        return () -> itemStack.getComponentChanges()
            .get(DataComponentTypes.CUSTOM_DATA)
            .map(NbtComponent::copyNbt)
            .orElse(null);
    }


    /**
     * Gets the data for the type from the item.
     *
     * @param itemStack The item.
     * @param type      The type.
     * @param <T>       The type of that data.
     * @return The data.
     */
    public static <T> T getData(ItemStack itemStack, ComponentType<T> type) {
        return itemStack.getComponents().get(type);
    }

}
