package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;

/**
 * A "complex" block shape that is highlighted.
 *
 * @param blockState The block state to draw.
 * @param position   The position to draw it at.
 * @param color      The color to draw it in.
 * @param outlines   Whether the renderer should use the entity outline shader or a shader that draws it as solid block.
 */
public record ComplexBlock(BlockState blockState, Vec3d position, int color, boolean outlines) implements Renderable {

    @Override
    public void render(WorldRenderContext context) {
        context.matrixStack().push();

        final VertexConsumerProvider consumerProvider;
        if (outlines && context.worldRenderer().canDrawEntityOutlines()) {
            context.matrixStack().translate(position.x, position.y, position.z);
            context.matrixStack().translate(0.001f, 0.001f, 0.001f);
            context.matrixStack().scale(0.998f, 0.998f, 0.998f);
            final OutlineVertexConsumerProvider entityOutlinesFramebuffer =
                MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers();
            entityOutlinesFramebuffer.setColor(255, 255, 255, 255);
            consumerProvider = entityOutlinesFramebuffer;
        } else {
            consumerProvider = context.consumers();
        }

        MinecraftClient.getInstance().getBlockRenderManager()
            .renderBlockAsEntity(blockState, context.matrixStack(), consumerProvider,
                LightmapTextureManager.pack(15, 15),
                OverlayTexture.DEFAULT_UV);

        context.matrixStack().pop();
    }
}
