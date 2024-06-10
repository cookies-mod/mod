package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;
import java.util.Objects;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

/**
 * Renders a line in 3d space from the start vector to the end vector.
 *
 * @param start The start vector.
 * @param end   The end vector.
 * @param red   The red value between 0 and 1.
 * @param green The green value between 0 and 1.
 * @param blue  The blue value between 0 and 1.
 * @param alpha The alpha value between 0 and 1.
 */
public record Line(Vec3d start, Vec3d end, float red, float green, float blue, float alpha) implements Renderable {

    /**
     * Creates a line between the start and the end with the specified color.
     *
     * @param start The start.
     * @param end   The end.
     * @param color The color of the line as argb.
     */
    public Line(Vec3d start, Vec3d end, int color) {
        this(
            start,
            end,
            (color & 0xFF) / 255f,
            ((color >> 8) & 0xFF) / 255f,
            ((color >> 16) & 0xFF) / 255f,
            ((color >> 24) & 0xFF) / 255f
        );
    }

    /**
     * Creates a line between the start and the end with the specified color.
     *
     * @param start The start vector.
     * @param end   The end vector.
     * @param red   The red value between 0 and 255.
     * @param green The green value between 0 and 255.
     * @param blue  The blue value between 0 and 255.
     * @param alpha The alpha value between 0 and 255.
     */
    public Line(Vec3d start,
                Vec3d end,
                int red,
                int green,
                int blue,
                int alpha) {
        this(
            start,
            end,
            red / 255f,
            green / 255f,
            blue / 255f,
            alpha / 255f
        );
    }

    @Override
    public void render(WorldRenderContext context) {
        final VertexConsumerProvider vertices = context.consumers();
        if (vertices == null) {
            return;
        }
        final VertexConsumer buffer = vertices.getBuffer(RenderLayer.getDebugLineStrip(1));
        final MatrixStack.Entry peek = Objects.requireNonNull(context.matrixStack()).peek();
        buffer.vertex(peek, (float) start.x, (float) start.y, (float) start.z).color(0, 0, 255, 255);
        buffer.vertex(peek, (float) end.x, (float) end.y, (float) end.z).color(0, 0, 255, 255);
    }
}
