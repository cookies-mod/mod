package codes.cookies.mod.utils.items;

import codes.cookies.mod.utils.items.types.ScrollableDataComponentTypes;

import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Unique;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

public interface ScrollableTooltipHandler {

	static void scroll(ItemStack stack, double horizontalAmount, double verticalAmount) {
		if (Screen.hasShiftDown()) {
			if (verticalAmount > 0) {
				update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_LAST, (int) -verticalAmount);
			} else {
				update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_FIRST, (int) -verticalAmount);
			}
		} else if (Screen.hasControlDown()) {
			update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_HORIZONTAL, (int) verticalAmount * -10);
		} else {
			update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_VERTICAL, (int) verticalAmount * -10);
			update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_HORIZONTAL, (int) horizontalAmount * -10);
		}
	}

	@Unique
	static void update(ItemStack itemStack, ComponentType<Integer> componentType, int amount) {
		if (itemStack.contains(componentType)) {
			final Integer i = itemStack.get(componentType);
			if (i != null) {
				itemStack.set(componentType, i + amount);
				return;
			}
		}
		itemStack.set(componentType, amount);
	}
}
