package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;
import dev.morazzer.cookies.mod.render.utils.CookiesRenderLayers;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * A simple box.
 *
 * @param box   The box to render.
 * @param red   Red value 0-1.
 * @param green Green value 0-1.
 * @param blue  Blue value 0-1.
 * @param alpha Alpha value 0-1.
 */
public record Box(
		net.minecraft.util.math.Box box, float red, float green, float blue, float alpha, boolean throughWalls
) implements Renderable {

	/**
	 * @param start The start position.
	 * @param end   The end position.
	 * @param red   Red value 0-255.
	 * @param green Green value 0-255.
	 * @param blue  Blue value 0-255.
	 * @param alpha Alpha value 0-255.
	 */
	public Box(Vec3d start, Vec3d end, int red, int green, int blue, int alpha) {
		this(start, end, red / 255f, green / 255f, blue / 255f, alpha / 255f, false);
	}

	public Box(Vec3d start, Vec3d end, float red, float green, float blue, float alpha, boolean throughWalls) {
		this(new net.minecraft.util.math.Box(start, end), red, green, blue, alpha, throughWalls);
	}


	/**
	 * @param start The start position.
	 * @param end   The end position.
	 * @param color The color of the box.
	 */
	public Box(Vec3d start, Vec3d end, int color) {
		this(start, end, color, false);
	}

	/**
	 * @param start        The start position.
	 * @param end          The end position.
	 * @param color        The color of the box.
	 * @param throughWalls Whether the box should be visible through walls or not.
	 */
	public Box(Vec3d start, Vec3d end, int color, boolean throughWalls) {
		this(start,
				end,
				(color & 0xFF) / 255f,
				((color >> 8) & 0xFF) / 255f,
				((color >> 16) & 0xFF) / 255f,
				((color >> 24) & 0xFF) / 255f,
				throughWalls);
	}

	public Box(BlockPos origin, int color, boolean throughWalls) {
		this(origin.toCenterPos().subtract(0.5, 0.5, 0.5),
				origin.toCenterPos().add(0.5, 0.5, 0.5),
				color,
				throughWalls);
	}

	@Override
	public void render(WorldRenderContext context) {
		final MatrixStack matrixStack = context.matrixStack();
		final VertexConsumerProvider consumers = context.consumers();

		VertexRendering.drawFilledBox(matrixStack,
				consumers.getBuffer(
						throughWalls ? CookiesRenderLayers.FILLED_THROUGH_WALLS : RenderLayer.getDebugFilledBox()),
				box.minX,
				box.minY,
				box.minZ,
				box.maxX,
				box.maxY,
				box.maxZ,
				green,
				blue,
				alpha,
				alpha);
	}

	@Override
	public boolean shouldRender(WorldRenderContext context) {
		return context.frustum().isVisible(box);
	}
}
