package codes.cookies.mod.render.mixins;

import codes.cookies.mod.render.WorldRender;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for rendering of outlined blocks.
 */
@Mixin(WorldRenderer.class)
public class WorldRenderMixin {

    @Inject(
        method = "render(Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getEntityVertexConsumers()Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;", shift = At.Shift.AFTER)
    )
    @SuppressWarnings("MissingJavadoc")
    public void render(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
                       GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
                       Matrix4f matrix4f2, CallbackInfo ci) {
        WorldRender.afterEntities(getContext());
    }

    @Unique
    private WorldRenderContext getContext() {
        try {
            return (WorldRenderContext) this.getClass().getDeclaredField("context").get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
