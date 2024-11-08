package codes.cookies.mod.render.types;

import codes.cookies.mod.render.Renderable;
import org.joml.Vector3f;

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
public record Line(Vector3f start, Vec3d end, int startColor, int endColor) implements Renderable {

	public Line(Vec3d start, Vec3d end, int startColor, int endColor) {
		this(start.toVector3f(), end.subtract(start), startColor, endColor);
	}

	public Line(Vec3d start, Vec3d end, int startColor) {
		this(start.toVector3f(), end.subtract(start), startColor, startColor);
	}

	/**
	 * Creates a line between the start and the end with the specified color.
	 *
	 * @param start The start.
	 * @param end   The end.
	 * @param color The color of the line as argb.
	 */
	public Line(Vector3f start, Vec3d end, int color) {
		this(start, end, color, color);
	}

	@Override
	public void render(WorldRenderContext context) {
		final VertexConsumerProvider vertices = context.consumers();
		if (vertices == null) {
			return;
		}
		final VertexConsumer vertexConsumers = vertices.getBuffer(RenderLayer.getDebugLineStrip(3));
		MatrixStack.Entry entry = context.matrixStack().peek();
		vertexConsumers.vertex(entry, start)
				.color(startColor)
				.normal(entry, (float) end.x, (float) end.y, (float) end.z);
		vertexConsumers.vertex(
						entry,
						(float) ((double) start.x() + end.x),
						(float) ((double) start.y() + end.y),
						(float) ((double) start.z() + end.z))
				.color(endColor)
				.normal(entry, (float) end.x, (float) end.y, (float) end.z);
	}
}
