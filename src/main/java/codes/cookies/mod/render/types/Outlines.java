package codes.cookies.mod.render.types;

import com.mojang.blaze3d.systems.RenderSystem;
import codes.cookies.mod.render.Renderable;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * A simple box.
 *
 * @param red          Red value 0-1.
 * @param green        Green value 0-1.
 * @param blue         Blue value 0-1.
 * @param alpha        Alpha value 0-1.
 * @param lineWidth    The width of the line.
 * @param throughWalls Whether the outlines should be rendered through walls.
 */
public record Outlines(
		Box box, float red, float green, float blue, float alpha, int lineWidth, boolean throughWalls
) implements Renderable {

	/**
	 * @param start     The start position.
	 * @param end       The end position.
	 * @param red       Red value 0-255.
	 * @param green     Green value 0-255.
	 * @param blue      Blue value 0-255.
	 * @param alpha     Alpha value 0-255.
	 * @param lineWidth The width of the line.
	 */
	public Outlines(
			Vec3d start, Vec3d end, int red, int green, int blue, int alpha, int lineWidth) {
		this(
				new Box(start.x, start.y, start.z, end.x, end.y, end.z),
				red / 255f,
				green / 255f,
				blue / 255f,
				alpha / 255f,
				lineWidth,
				false);
	}

	/**
	 * @param start The start position.
	 * @param end   The end position.
	 * @param red   Red value 0-255.
	 * @param green Green value 0-255.
	 * @param blue  Blue value 0-255.
	 * @param alpha Alpha value 0-255.
	 */
	public Outlines(
			Vec3d start, Vec3d end, int red, int green, int blue, int alpha) {
		this(
				new Box(start.x, start.y, start.z, end.x, end.y, end.z),
				red / 255f,
				green / 255f,
				blue / 255f,
				alpha / 255f,
				2,
				false);
	}

	/**
	 * @param start The start position.
	 * @param end   The end position.
	 * @param color The color of the box.
	 */
	public Outlines(
			Vec3d start, Vec3d end, int color) {
		this(start, end, color, 2);
	}

	/**
	 * @param start     The start position.
	 * @param end       The end position.
	 * @param color     The color of the box.
	 * @param lineWidth The width of the line.
	 */
	public Outlines(
			Vec3d start, Vec3d end, int color, int lineWidth) {
		this(start, end, color, lineWidth, false);
	}

	/**
	 * @param start        The start position.
	 * @param end          The end position.
	 * @param color        The color of the box.
	 * @param lineWidth    The width of the line.
	 * @param throughWalls Whether the outlines should be visible through walls or not.
	 */
	public Outlines(
			Vec3d start, Vec3d end, int color, int lineWidth, boolean throughWalls) {
		this(
				new Box(start.x, start.y, start.z, end.x, end.y, end.z),
				(color & 0xFF) / 255f,
				((color >> 8) & 0xFF) / 255f,
				((color >> 16) & 0xFF) / 255f,
				((color >> 24) & 0xFF) / 255f,
				lineWidth,
				throughWalls);
	}


	@Override
	public void render(WorldRenderContext context) {
		final MatrixStack matrixStack = context.matrixStack();
		RenderSystem.setShader(RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES));
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.lineWidth(this.lineWidth);
		RenderSystem.disableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(this.throughWalls ? GL11.GL_ALWAYS : GL11.GL_LEQUAL);

		final Tessellator tessellator = RenderSystem.renderThreadTesselator();
		final BufferBuilder begin = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		VertexRendering.drawBox(
				matrixStack,
				begin,
				this.box.minX,
				this.box.minY,
				this.box.minZ,
				this.box.maxX,
				this.box.maxY,
				this.box.maxZ,
				this.red,
				this.green,
				this.blue,
				this.alpha);
		BufferRenderer.drawWithGlobalProgram(begin.end());

		RenderSystem.lineWidth(1);
		RenderSystem.enableCull();
		RenderSystem.disableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
	}

	@Override
	public boolean shouldRender(WorldRenderContext context) {
		return context.frustum().isVisible(this.box);
	}
}
