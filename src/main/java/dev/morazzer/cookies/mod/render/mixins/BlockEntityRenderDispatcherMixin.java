package dev.morazzer.cookies.mod.render.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.morazzer.cookies.mod.render.BlockEntityAccessor;
import dev.morazzer.cookies.mod.render.utils.RenderHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Allows for rendering of block entities with outlines.
 */
@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    @ModifyVariable(
        method = "render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;" +
                 "Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;" +
                 "Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At(value = "HEAD"), argsOnly = true
    )
    private static <E extends BlockEntity> VertexConsumerProvider renderEntity(
        VertexConsumerProvider value, @Local(argsOnly = true) E entity) {
        return cookies$getProvider(entity, value);
    }

    @Unique
    private static VertexConsumerProvider cookies$getProvider(BlockEntity entity, VertexConsumerProvider defaultValue) {
        if (((BlockEntityAccessor) entity).cookies$isHighlighted()) {
            int color = ((BlockEntityAccessor) entity).cookies$getHighlightedColor();
            final OutlineVertexConsumerProvider outlineVertexConsumers =
                MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers();
            outlineVertexConsumers.setColor(
                RenderHelper.getRed(color),
                RenderHelper.getGreen(color),
                RenderHelper.getBlue(color),
                RenderHelper.getAlpha(color));
            RenderSystem.disableDepthTest();
            return outlineVertexConsumers;
        }

        return defaultValue;
    }

    @ModifyVariable(method = "renderEntity", at = @At(value = "HEAD"), argsOnly = true)
    private <E extends BlockEntity> VertexConsumerProvider renderEntityPrivate(
        VertexConsumerProvider value, @Local(argsOnly = true) E entity) {
        return cookies$getProvider(entity, value);
    }

}
