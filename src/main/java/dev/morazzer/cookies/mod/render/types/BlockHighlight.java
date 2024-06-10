package dev.morazzer.cookies.mod.render.types;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.morazzer.cookies.mod.render.Renderable;
import dev.morazzer.cookies.mod.render.utils.RenderHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
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
 *
 * @param blockPos The block to highlight.
 * @param color    The color to highlight the block in.
 */
public record BlockHighlight(BlockPos blockPos, int color) implements Renderable {
    @Override
    public void render(WorldRenderContext context) {
        context.matrixStack().push();

        final VertexConsumerProvider consumerProvider;
        context.matrixStack().translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        context.matrixStack().translate(0.001f, 0.001f, 0.001f);
        context.matrixStack().scale(0.998f, 0.998f, 0.998f);
        final OutlineVertexConsumerProvider entityOutlinesFramebuffer =
            MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers();
        entityOutlinesFramebuffer.setColor(
            RenderHelper.getRed(color),
            RenderHelper.getGreen(color),
            RenderHelper.getBlue(color),
            RenderHelper.getAlpha(color)
        );
        consumerProvider = entityOutlinesFramebuffer;

        RenderSystem.disableDepthTest();
        final ClientWorld clientWorld = MinecraftClient.getInstance().player.clientWorld;
        MinecraftClient.getInstance().getBlockRenderManager()
            .renderBlockAsEntity(
                clientWorld.getBlockState(blockPos),
                context.matrixStack(), consumerProvider,
                LightmapTextureManager.pack(
                    clientWorld.getLightLevel(LightType.BLOCK, blockPos),
                    clientWorld.getLightLevel(LightType.SKY, blockPos)
                ),
                OverlayTexture.DEFAULT_UV
            );

        context.matrixStack().pop();
    }
}
