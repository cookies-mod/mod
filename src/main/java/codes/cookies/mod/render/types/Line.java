package codes.cookies.mod.render.types;

import java.util.Objects;

import codes.cookies.mod.render.Renderable;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Renders a line in 3d space from the start vector to the end vector.
 *
 * @param start      The start vector.
 * @param end        The end vector.
 * @param startColor The start color.
 * @param endColor   The end color
 */
public record Line(Vec3d start, Vec3d end, int startColor, int endColor) implements Renderable {

	/**
	 * Creates a line between the start and the end with the specified color.
	 *
	 * @param start The start.
	 * @param end   The end.
	 * @param color The color of the line as argb.
	 */
	public Line(Vec3d start, Vec3d end, int color) {
		this(start, end, color, color);
	}

	@Override
	public void render(WorldRenderContext context) {
		final VertexConsumerProvider vertices = context.consumers();
		if (vertices == null) {
			return;
		}
		final VertexConsumer buffer = vertices.getBuffer(RenderLayer.getDebugLineStrip(6.0));
		final MatrixStack.Entry peek = Objects.requireNonNull(context.matrixStack()).peek();
		buffer.vertex(peek, (float) this.start.x, (float) this.start.y, (float) this.start.z).color(this.startColor);
		buffer.vertex(peek, (float) this.end.x, (float) this.end.y, (float) this.end.z).color(this.endColor);
	}
}
