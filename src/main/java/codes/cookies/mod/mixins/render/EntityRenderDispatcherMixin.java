package codes.cookies.mod.mixins.render;

import codes.cookies.mod.config.categories.MiscCategory;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

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
    public void renderFire(
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			EntityRenderState renderState,
			Quaternionf rotation,
			CallbackInfo ci) {
        if (MiscCategory.hideFireOnEntities) {
            ci.cancel();
        }
    }

}
