package dev.morazzer.cookies.mod.events.mixins;

import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.events.api.accessors.ScreenHandlerUpdateEventAccessor;
import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class InventoryContentUpdateMixin implements ScreenHandlerUpdateEventAccessor {

    @Unique
    final Event<InventoryContentUpdateEvent> cookies$inventoryUpdateEvent = EventFactory.createArrayBacked(
        InventoryContentUpdateEvent.class,
        inventoryUpdateEvents -> (slot, item) -> {
            for (InventoryContentUpdateEvent inventoryContentUpdateEvent : inventoryUpdateEvents) {
                inventoryContentUpdateEvent.updateInventory(slot, item);
            }
        }
    );

    /**
     * Called when the slots in a screen handler get updated.
     *
     * @param revision    The revision id.
     * @param stacks      The list of new stacks.
     * @param cursorStack The stack that is on the cursor.
     * @param ci          The callback information.
     */
    @Inject(method = "updateSlotStacks", at = @At("RETURN"))
    public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack, CallbackInfo ci) {
        for (int i = 0; i < stacks.size(); ++i) {
            cookies$inventoryUpdateEvent.invoker().updateInventory(i, stacks.get(i));
        }
    }

    /**
     * Called when the screen handler sets an item in a specific slot.
     *
     * @param slot     The slot the item is in.
     * @param revision The revision id.
     * @param stack    The stack that was put there.
     * @param ci       The callback information.
     */
    @Inject(method = "setStackInSlot", at = @At("RETURN"))
    public void setStackInSlot(int slot, int revision, ItemStack stack, CallbackInfo ci) {
        cookies$inventoryUpdateEvent.invoker().updateInventory(slot, stack);
    }

    /**
     * Gets the event instance for ths screen handler instance.
     *
     * @return The event instance.
     */
    @Override
    public Event<InventoryContentUpdateEvent> cookies$inventoryUpdateEvent() {
        return cookies$inventoryUpdateEvent;
    }

}
