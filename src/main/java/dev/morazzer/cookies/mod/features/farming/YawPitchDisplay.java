package dev.morazzer.cookies.mod.features.farming;

import dev.morazzer.cookies.mod.config.ConfigKeys;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Displays the yaw/pitch of the user on the screen.
 */
public class YawPitchDisplay {
    private static final Identifier DEBUG = DevUtils.createIdentifier("yaw_pitch_debug");

    private static double lastYaw = 0;
    private static double lastPitch = 0;
    private static double lastUpdate = 0;

    @SuppressWarnings("MissingJavadoc")
    public static void register() {
        HudRenderCallback.EVENT.register(YawPitchDisplay::renderHud);
    }

    private static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!ConfigKeys.FARMING_YAW_PITCH.get()) {
            return;
        }
        if (ItemUtils.getMainHand().skyblockId().get() == null) {
            return;
        }
        if (!ItemUtils.getMainHand()
                      .enchantments()
                      .getAsOptional()
                      .map(map -> map.containsKey("cultivating") ||
                                  map.containsKey("replenish"))
                      .orElse(false)) {
            return;
        }

        double yaw = MathHelper.wrapDegrees(MinecraftClient.getInstance().player.getYaw());
        double pitch = MinecraftClient.getInstance().player.getPitch();
        if (lastYaw == yaw && lastPitch == pitch) {
            if (lastUpdate + 5000 < System.currentTimeMillis()) {
                return;
            }
        } else {
            lastUpdate = System.currentTimeMillis();
            lastPitch = pitch;
            lastYaw = yaw;
        }

        int crosshairX = (int) (((double) (drawContext.getScaledWindowWidth() - 15) / 2) * (1.25));
        int crosshairY = (int) (((double) (drawContext.getScaledWindowHeight() - 15) / 2) * (1.25));

        int delta = (int) (System.currentTimeMillis() - lastUpdate);
        float opacity = 1;
        if (delta > 1000) {
            opacity = 1 - ((delta - 1000) / 4000f);
        }


        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(0.8f, 0.8f, 0.8f);
        drawContext.setShaderColor(1, 1, 1, opacity);

        //noinspection DataFlowIssue
        drawContext.drawText(
            MinecraftClient.getInstance().textRenderer,
            "%.03f".formatted(yaw),
            crosshairX + 18,
            crosshairY + 6,
            Formatting.GRAY.getColorValue() & 0xFFFFFF,
            true);

        drawContext.drawCenteredTextWithShadow(
            MinecraftClient.getInstance().textRenderer,
            "%.03f".formatted(pitch),
            crosshairX + 8,
            crosshairY + 18,
            Formatting.GRAY.getColorValue() & 0xFFFFFF);
        drawContext.setShaderColor(1, 1, 1, 1);

        if (DevUtils.isEnabled(DEBUG)) {
            drawContext.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                "Δt: " + delta,
                crosshairX + 8,
                crosshairY - 4,
                Formatting.GRAY.getColorValue());
            drawContext.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                "opacity: " + opacity,
                crosshairX + 8,
                crosshairY - 14,
                Formatting.GRAY.getColorValue());
        }
        drawContext.getMatrices().pop();
    }

}
