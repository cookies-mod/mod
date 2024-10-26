package dev.morazzer.cookies.mod.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import dev.morazzer.cookies.mod.utils.accessors.TextRenderUtils;

import net.minecraft.client.font.TextRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public class TextFieldWidgetMixin {

	@Inject(
			method = "drawLayer(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F",
			at = @At("HEAD"),
			cancellable = true
	)
	public void draw(CallbackInfoReturnable<Float> cir, @Local(argsOnly = true, ordinal = 0) boolean shadow) {
		if (shadow && TextRenderUtils.hasShadowsDisabled()) {
			cir.setReturnValue(0.0F);
		}
	}

	@Inject(
			method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F",
			at = @At("HEAD"),
			cancellable = true
	)
	public void drawOrdered(CallbackInfoReturnable<Float> cir, @Local(argsOnly = true, ordinal = 0) boolean shadow) {
		if (shadow && TextRenderUtils.hasShadowsDisabled()) {
			cir.setReturnValue(0.0F);
		}
	}

}
