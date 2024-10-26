package dev.morazzer.cookies.mod.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import dev.morazzer.cookies.mod.features.misc.render.ArmorRenderHelper;

import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for hiding of the "head feature" (skulls on head).
 */
@Mixin(HeadFeatureRenderer.class)
public class HeadFeatureRenderMixin {

    @Inject(
        at = @At("HEAD"),
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/LivingEntityRenderState;FF)V",
        cancellable = true
    )
    @SuppressWarnings("MissingJavadoc")
    public <T extends LivingEntity> void render(final CallbackInfo ci, @Local(argsOnly = true) LivingEntityRenderState renderState) {
        if (ArmorRenderHelper.shouldNotRender()) {
            ci.cancel();
        }
    }

}
