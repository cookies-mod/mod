package dev.morazzer.cookies.mod.utils.mixins;


import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for the visual replacement of items.
 */
@Mixin(ItemRenderer.class)
public abstract class ReplaceRenderedItem {

    @Shadow public abstract BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity,
                                                int seed);

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void getModel(ItemStack stack, World world, LivingEntity entity, int seed,
                          CallbackInfoReturnable<BakedModel> cir) {
        final ItemStack data = ItemUtils.getData(stack, CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM);
        if (data == null) {
            return;
        }
        cir.setReturnValue(this.getModel(data, world, entity, seed));
    }

}
