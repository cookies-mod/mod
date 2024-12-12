package codes.cookies.mod.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import codes.cookies.mod.features.misc.render.ArmorRenderHelper;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.item.ItemStack;

/**
 * Allows for cancellation of armor rendering.
 */
@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRenderMixin {

	@Unique
	private BipedEntityRenderState cookies$renderState;

	@Inject(
			at = @At("HEAD"),
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V"
	)
	@SuppressWarnings("MissingJavadoc")
	public <S extends BipedEntityRenderState> void render(
			CallbackInfo ci,
			@Local(argsOnly = true) S state
	) {
		this.cookies$renderState = state;
	}

	@Inject(at = @At("HEAD"), method = "renderArmor", cancellable = true)
	public void renderArmor(
			CallbackInfo info,
			@Local(argsOnly = true) ItemStack stack
	) {
		if (ArmorRenderHelper.shouldNotRender(cookies$renderState, stack)) {
			info.cancel();
		}
	}
}
