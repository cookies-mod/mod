package codes.cookies.mod.render.mixins;

import codes.cookies.mod.render.WorldRender;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import net.minecraft.client.render.WorldRenderer;

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
        method = "renderEntities",
        at = @At(value = "RETURN")
    )
    @SuppressWarnings("MissingJavadoc")
    public void render(CallbackInfo ci) {
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
