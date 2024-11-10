package codes.cookies.mod.utils.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import codes.cookies.mod.utils.accessors.SlotAccessor;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for overwriting of the item in a slot, without changing it in the inventory.
 */
@Mixin(Slot.class)
public abstract class SlotMixin implements SlotAccessor {

    @Unique
    private ItemStack cookies$override;
    @Unique
    private Runnable cookies$runnable;
    @Unique
    private Consumer<Integer> cookies$clicked;

    @Override
    public void cookies$setOnClick(Consumer<Integer> onClick) {
        this.cookies$clicked = onClick;
    }

    @Override
    public Consumer<Integer> cookies$getOnClick() {
        final ItemStack stack = getStack();
        if (stack != null) {
            final Consumer<Integer> data = ItemUtils.getData(stack, CookiesDataComponentTypes.ITEM_CLICK_CONSUMER);
            if (data != null) {
                return data;
            }
        }
        return cookies$clicked;
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
	public Runnable cookies$getOnItemClickRunnable() {
		final ItemStack stack = getStack();
		if (stack != null) {
			return ItemUtils.getData(stack, CookiesDataComponentTypes.ON_ITEM_CLICK_RUNNABLE);
		}
		return null;
	}

	@Override
    public Runnable cookies$getRunnable() {
        final ItemStack stack = getStack();
        if (stack != null) {
            final Runnable data = ItemUtils.getData(stack, CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
            if (data != null) {
                return data;
            }
        }
        return cookies$runnable;
    }

    @Override
    public ItemStack cookies$getOverrideItem() {
        final ItemStack stack = getStack();
        if (stack != null) {
            final ItemStack data = ItemUtils.getData(stack, CookiesDataComponentTypes.OVERRIDE_ITEM);
            if (data != null) {
                return data;
            }
        }
        return this.cookies$override;
    }

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "getStack", at = @At("HEAD"), cancellable = true)
    private void getItem(CallbackInfoReturnable<ItemStack> cir) {
        if (cookies$override != null) {
            cir.setReturnValue(cookies$override);
        }
    }

    @ModifyReturnValue(method = "getStack", at = @At("RETURN"))
    private ItemStack modifyReturn(ItemStack original) {
        ItemStack stack = ItemUtils.getData(original, CookiesDataComponentTypes.OVERRIDE_ITEM);
        if (stack != null) {
            return stack;
        }
        return original;
    }

    @Inject(method = "hasStack", at = @At("HEAD"), cancellable = true)
    private void hasItem(CallbackInfoReturnable<Boolean> cir) {
        if (cookies$override != null) {
            cir.setReturnValue(!this.cookies$override.isEmpty());
            return;
        }

        ItemStack stack = ItemUtils.getData(getStack(), CookiesDataComponentTypes.OVERRIDE_ITEM);
        if (stack != null) {
            cir.setReturnValue(stack.isEmpty());
        }
    }
}
