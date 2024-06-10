package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.util.math.Vec3d;

/**
 * @param offset     Offset for the renderable.
 * @param renderable The object to render.
 */
public record Offset(Vec3d offset, Renderable renderable) implements Renderable {
    @Override
    public void render(WorldRenderContext context) {
        context.matrixStack().push();
        context.matrixStack().translate(offset.getX(), offset.getY(), offset.getZ());
        renderable.render(context);
        context.matrixStack().pop();
    }

    @Override
    public boolean shouldRender(WorldRenderContext context) {
        return renderable.shouldRender(context);
    }
}
