package dev.morazzer.cookies.mod.mixins.render;

import dev.morazzer.cookies.mod.features.misc.render.ArmorRenderHelper;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for cancellation of armor rendering.
 */
@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRenderMixin {

    @Inject(
        at = @At("HEAD"),
        method = "renderArmor",
        cancellable = true
    )
    @SuppressWarnings("MissingJavadoc")
    public void render(CallbackInfo ci) {
        if (ArmorRenderHelper.shouldNotRender()) {
            ci.cancel();
        }
    }

}
