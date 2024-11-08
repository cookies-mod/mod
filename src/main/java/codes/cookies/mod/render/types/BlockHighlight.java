package codes.cookies.mod.render.types;

import com.mojang.blaze3d.systems.RenderSystem;
import codes.cookies.mod.render.BlockEntityAccessor;
import codes.cookies.mod.render.Renderable;
import codes.cookies.mod.render.utils.RenderHelper;

import java.util.Objects;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

/**
 * Highlights a block in the world with the entity outlines shader.
 */
public final class BlockHighlight implements Renderable {
	private final BlockPos blockPos;
	private final int color;
	private final BlockEntity entity;

	/**
	 * @param blockPos The block to highlight.
	 * @param color    The color to highlight the block in.
	 */
	public BlockHighlight(BlockPos blockPos, int color) {
		this.blockPos = blockPos;
		this.color = color;
		final ClientWorld clientWorld = MinecraftClient.getInstance().player.clientWorld;
		this.entity = clientWorld.getBlockEntity(blockPos);
	}

	@Override
	public void remove() {
		if (this.entity == null) {
			return;
		}
		((BlockEntityAccessor) this.entity).cookies$setHighlighted(false);
	}

	@Override
	public void load() {
		if (this.entity == null) {
			return;
		}
		((BlockEntityAccessor) this.entity).cookies$setHighlighted(true);
		((BlockEntityAccessor) this.entity).cookies$setHighlightedColor(this.color);
	}

	@Override
	public void render(WorldRenderContext context) {
		if (this.entity != null) {
			((BlockEntityAccessor) this.entity).cookies$setHighlighted(true);
			((BlockEntityAccessor) this.entity).cookies$setHighlightedColor(this.color);
			return;
		}
		context.matrixStack().push();

		final VertexConsumerProvider consumerProvider;
		context.matrixStack().translate(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
		context.matrixStack().translate(0.001f, 0.001f, 0.001f);
		context.matrixStack().scale(0.998f, 0.998f, 0.998f);
		final OutlineVertexConsumerProvider entityOutlinesFramebuffer =
				MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers();
		entityOutlinesFramebuffer.setColor(
				RenderHelper.getRed(this.color),
				RenderHelper.getGreen(this.color),
				RenderHelper.getBlue(this.color),
				RenderHelper.getAlpha(this.color));
		consumerProvider = entityOutlinesFramebuffer;

		RenderSystem.disableDepthTest();
		final ClientWorld clientWorld = MinecraftClient.getInstance().player.clientWorld;
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
				clientWorld.getBlockState(this.blockPos),
				context.matrixStack(),
				consumerProvider,
				LightmapTextureManager.pack(
						clientWorld.getLightLevel(LightType.BLOCK, this.blockPos),
						clientWorld.getLightLevel(LightType.SKY, this.blockPos)),
				OverlayTexture.DEFAULT_UV);

		context.matrixStack().pop();
	}

	/**
	 * Gets the position of the outlined block.
	 *
	 * @return The block.
	 */
	public BlockPos blockPos() {return this.blockPos;}

	/**
	 * Gets the color of the outline.
	 *
	 * @return The color.
	 */
	public int color() {return this.color;}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (BlockHighlight) obj;
		return Objects.equals(this.blockPos, that.blockPos) && this.color == that.color;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.blockPos, this.color);
	}

	@Override
	public String toString() {
		return "BlockHighlight[" + "blockPos=" + this.blockPos + ", " + "color=" + this.color + ']';
	}

	@Override
	public boolean requiresEntityOutlineShader() {
		return true;
	}
}
