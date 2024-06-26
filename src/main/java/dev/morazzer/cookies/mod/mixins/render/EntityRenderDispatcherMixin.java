package dev.morazzer.cookies.mod.mixins.render;

import dev.morazzer.cookies.mod.config.ConfigManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for the hiding of fire on all entity types.
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(at = @At("HEAD"), method = "renderFire", cancellable = true)
    @SuppressWarnings("MissingJavadoc")
    public void renderFire(MatrixStack matrices,
                           VertexConsumerProvider vertexConsumers,
                           Entity entity,
                           Quaternionf rotation,
                           CallbackInfo ci) {
        if (ConfigManager.getConfig().miscConfig.hideFireOnEntities.getValue()) {
            ci.cancel();
        }
    }

}
