package codes.cookies.mod.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import codes.cookies.mod.features.misc.render.ArmorRenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/**
 * Allows for hiding of the "head feature" (skulls on head).
 */
@Mixin(HeadFeatureRenderer.class)
public class HeadFeatureRenderMixin {

	@Inject(
			at = @At("HEAD"),
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/LivingEntityRenderState;FF)V",
			cancellable = true
	)
	@SuppressWarnings("MissingJavadoc")
	public <S extends LivingEntityRenderState> void render(
			final CallbackInfo ci,
			@Local(argsOnly = true) S renderState
	) {
		if (ArmorRenderHelper.shouldNotRender(renderState, renderState.equippedHeadStack)) {
			ci.cancel();
		}
	}

}
