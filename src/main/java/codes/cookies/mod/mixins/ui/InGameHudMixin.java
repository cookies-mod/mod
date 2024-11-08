package codes.cookies.mod.mixins.ui;

import codes.cookies.mod.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows the hiding of the health bar.
 * Allows the hiding of the hunger bar.
 * Allows the hiding of the armor bar.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x,
                                    CallbackInfo ci) {

        if (ConfigManager.getConfig().miscConfig.hideArmor.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void renderHealthBar(DrawContext context,
                                 PlayerEntity player,
                                 int x,
                                 int y,
                                 int lines,
                                 int regeneratingHeartIndex,
                                 float maxHealth,
                                 int lastHealth,
                                 int health,
                                 int absorption,
                                 boolean blinking,
                                 CallbackInfo ci) {
        if (ConfigManager.getConfig().miscConfig.hideHealth.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood", cancellable = true, at = @At("HEAD"))
    private void renderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        if (ConfigManager.getConfig().miscConfig.hideFood.getValue()) {
            ci.cancel();
        }
    }
}
