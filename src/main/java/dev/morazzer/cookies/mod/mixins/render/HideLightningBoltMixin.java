package dev.morazzer.cookies.mod.mixins.render;

import dev.morazzer.cookies.mod.config.ConfigManager;

import net.minecraft.client.render.entity.LightningEntityRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntityRenderer.class)
public class HideLightningBoltMixin {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LightningEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    public void lightning(CallbackInfo ci) {
        if (ConfigManager.getConfig().miscConfig.hideLightningBolt.getValue()) {
            ci.cancel();
        }
    }

}
