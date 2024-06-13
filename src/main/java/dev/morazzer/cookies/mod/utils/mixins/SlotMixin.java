package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.accessors.SlotAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for overwriting of the item in a slot, without changing it in the inventory.
 */
@Mixin(Slot.class)
public class SlotMixin implements SlotAccessor {

    @Unique
    private ItemStack cookies$override;
    @Unique
    private Runnable cookies$runnable;

    @Inject(method = "getStack", at = @At("HEAD"), cancellable = true)
    private void getItem(CallbackInfoReturnable<ItemStack> cir) {
        if (cookies$override != null) {
            cir.setReturnValue(cookies$override);
        }
    }

    @Inject(method = "hasStack", at = @At("HEAD"), cancellable = true)
    private void hasItem(CallbackInfoReturnable<Boolean> cir) {
        if (cookies$override != null) {
            cir.setReturnValue(!this.cookies$override.isEmpty());
        }
    }

    @Override
    public void cookies$setOverrideItem(ItemStack itemStack) {
        this.cookies$override = itemStack;
    }

    @Override
    public void cookies$setRunnable(Runnable runnable) {
        this.cookies$runnable = runnable;
    }

    @Override
    public Runnable cookies$getRunnable() {
        return cookies$runnable;
    }

    @Override
    public ItemStack cookies$getOverrideItem() {
        return this.cookies$override;
    }
}
