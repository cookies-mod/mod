package codes.cookies.mod.utils.items;

import codes.cookies.mod.data.profile.items.Item;

import codes.cookies.mod.utils.cookies.CookiesUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Copies the component data form one item onto another item.
     * @param type The component to copy.
     * @param source The source to copy from.
     * @param target The target to copy to.
     * @param <T> The data value of the component.
     */
    public static <T> void copy(ComponentType<T> type, ItemStack source, ItemStack target) {
        target.set(type, ItemUtils.getData(source, type));
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

	public static String getId(Item<?> item) {
		if (item == null) {
			return null;
		}
		return item.itemStack().get(CookiesDataComponentTypes.SKYBLOCK_ID);
	}

	public static Optional<List<String>> getLore(ItemStack stack) {
		return Optional.ofNullable(stack.get(DataComponentTypes.LORE))
				.map(LoreComponent::lines)
				.map(ItemUtils::mapTextListToString);
	}

	private static List<String> mapTextListToString(@NotNull List<Text> texts) {
		return texts.stream().map(Text::getString).map(CookiesUtils::stripColor).collect(Collectors.toList());
	}
}
