package codes.cookies.mod.events.mixins;

import codes.cookies.mod.events.api.ItemBackgroundRenderCallback;
import codes.cookies.mod.events.api.accessors.ItemBackgroundAccessor;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for highlighting of slots.
 */
@Mixin(HandledScreen.class)
public class ItemBackgroundRenderMixin implements ItemBackgroundAccessor {

    @Unique
    Event<ItemBackgroundRenderCallback> backgroundCallbacks;

    @Override
    public Event<ItemBackgroundRenderCallback> cookies$itemRenderCallback() {
        return this.backgroundCallbacks;
    }

    /**
     * Called when a screen draws an item background.
     *
     * @param context The current draw context.
     * @param slot    The slot the item is in.
     * @param ci      The callback information.
     */
    @Inject(
        method = "drawSlot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V",
            shift = At.Shift.BEFORE
        )
    )
    private void renderBackground(DrawContext context, Slot slot, CallbackInfo ci) {
        this.backgroundCallbacks.invoker().renderBackground(context, slot);
        final ItemStack stack = slot.getStack();
        if (stack == null) {
            return;
        }
        final Integer data = ItemUtils.getData(stack, CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
        if (data == null) {
            return;
        }
        context.fill(
            slot.x,
            slot.y,
            slot.x + 16,
            slot.y + 16,
            data
        );
    }

    /**
     * Called when a screen is opened or resized.
     *
     * @param ci The callback information.
     */
    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        this.cookies$init();
    }

    /**
     * Set up the screen instance to have a working event instance.
     */
    @Unique
    private void cookies$init() {
        this.backgroundCallbacks = EventFactory.createArrayBacked(
            ItemBackgroundRenderCallback.class,
            itemBackgroundRenderCallbacks -> (drawContext, slot) -> {
                for (ItemBackgroundRenderCallback itemBackgroundRenderCallback : itemBackgroundRenderCallbacks) {
                    itemBackgroundRenderCallback.renderBackground(drawContext, slot);
                }
            }
        );
    }

}
