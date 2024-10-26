package dev.morazzer.cookies.mod.mixins.ui;

import dev.morazzer.cookies.mod.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows the hiding of status effects.
 */
@Mixin(StatusEffectsDisplay.class)
public class PotionEffectMixin {

    @Inject(at = @At("HEAD"), method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", cancellable = true)
    private void drawStatusEffects(final DrawContext drawContext, final int i, final int j, final CallbackInfo ci) {
        if (ConfigManager.getConfig().miscConfig.hidePotionEffects.getValue()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "shouldHideStatusEffectHud", cancellable = true)
    private void hideStatusEffectHud(final CallbackInfoReturnable<Boolean> cir) {
        if (ConfigManager.getConfig().miscConfig.hidePotionEffects.getValue()) {
            cir.setReturnValue(true);
        }
    }

}
