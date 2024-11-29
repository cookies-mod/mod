package codes.cookies.mod.mixins.render;

import codes.cookies.mod.features.misc.render.CustomMist;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

@Mixin(SectionBuilder.class)
public class SectionBuilderMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"), method = "build")
	private void build(
			BlockRenderManager instance,
			BlockState state,
			BlockPos pos,
			BlockRenderView world,
			MatrixStack matrices,
			VertexConsumer vertexConsumer,
			boolean cull,
			Random random,
			Operation<Void> original
	) {
		if (CustomMist.isIsActive() && pos.getY() < 76) {
			if (state.isOf(Blocks.WHITE_STAINED_GLASS)) {
				state = CustomMist.getReplacement().getGlassBlock().getDefaultState();
			} else if (state.isOf(Blocks.WHITE_CARPET)) {
				state = CustomMist.getReplacement().getCarpetBlock().getDefaultState();
			}
		}

		original.call(instance, state, pos, world, matrices, vertexConsumer, cull, random);
	}

}
