package dev.morazzer.cookies.mod.mixins.render;

import dev.morazzer.cookies.mod.config.ConfigManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LightningEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntityRenderer.class)
public class HideLightningBoltMixin {

    @Inject(method = "render(Lnet/minecraft/entity/LightningEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    public void lightning(LightningEntity lightningEntity, float f, float g, MatrixStack matrixStack,
                          VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (ConfigManager.getConfig().miscConfig.hideLightningBolt.getValue()) {
            ci.cancel();
        }
    }

}
