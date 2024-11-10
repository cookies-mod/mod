package codes.cookies.mod.mixins.ui;

import codes.cookies.mod.config.ConfigKeys;
import codes.cookies.mod.events.api.ScreenKeyEvents;
import codes.cookies.mod.utils.accessors.FocusedSlotAccessor;
import codes.cookies.mod.utils.items.types.ScrollableDataComponentTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for scrollable tooltips.
 */
@Mixin(ParentElement.class)
public interface ParentElementMixin extends ParentElement {

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void mouseScrolled(
        double mouseX, double mouseY, double horizontalAmount, double verticalAmount,
        CallbackInfoReturnable<Boolean> cir) {
        if (!ConfigKeys.MISC_SCROLLABLE_TOOLTIP.get()) {
            return;
        }
        if (((Object) this) instanceof HandledScreen<?> handledScreen) {
            Slot focusedSlot = FocusedSlotAccessor.getFocusedSlot(handledScreen);
            if (handledScreen.getScreenHandler().getCursorStack().isEmpty() && focusedSlot != null &&
                focusedSlot.hasStack()) {
                final ItemStack stack = focusedSlot.getStack();
                final long handle = MinecraftClient.getInstance().getWindow().getHandle();
                if (InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_LEFT_SHIFT)) {
                    if (verticalAmount > 0) {
                        this.cookies$update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_LAST,
                            (int) -verticalAmount);
                    } else {
                        this.cookies$update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_FIRST,
                            (int) -verticalAmount);
                    }
                } else if (InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_LEFT_CONTROL)) {
                    this.cookies$update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_HORIZONTAL,
                        (int) verticalAmount * -10);
                } else {
                    this.cookies$update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_VERTICAL,
                        (int) verticalAmount * -10);
                    this.cookies$update(stack, ScrollableDataComponentTypes.TOOLTIP_OFFSET_HORIZONTAL,
                        (int) horizontalAmount * -10);
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private void cookies$update(ItemStack itemStack, ComponentType<Integer> componentType, int amount) {
        if (itemStack.contains(componentType)) {
            final Integer i = itemStack.get(componentType);
            if (i != null) {
                itemStack.set(componentType, i + amount);
                return;
            }
        }
        itemStack.set(componentType, amount);
    }

    @Inject(method = "keyReleased", at = @At("HEAD"), cancellable = true)
    private void keyReleased(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!ConfigKeys.MISC_SCROLLABLE_TOOLTIP.get()) {
            return;
        }
        if (((Object) this) instanceof HandledScreen<? extends ScreenHandler> handledScreen) {
            Slot focusedSlot = FocusedSlotAccessor.getFocusedSlot(handledScreen);
            if (handledScreen.getScreenHandler().getCursorStack().isEmpty() && focusedSlot != null &&
                focusedSlot.hasStack()) {
                if (keyCode == InputUtil.GLFW_KEY_LEFT_SHIFT) {
                    final ItemStack stack = focusedSlot.getStack();
                    stack.set(ScrollableDataComponentTypes.TOOLTIP_OFFSET_FIRST, 0);
                    stack.set(ScrollableDataComponentTypes.TOOLTIP_OFFSET_LAST, 0);
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Screen screen) {
            if (ScreenKeyEvents.handle(screen, chr, modifiers)) {
                cir.setReturnValue(true);
            }
        }
    }
}
