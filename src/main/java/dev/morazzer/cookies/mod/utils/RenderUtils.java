package dev.morazzer.cookies.mod.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * Various render related utility methods.
 */
public sealed interface RenderUtils permits RenderUtils.Sealed {
    Identifier BACKGROUND_TEXTURE = Identifier.of("cookies-mod", "textures/gui/blank.png");

    /**
     * Renders a box in minecraft's style.
     *
     * @param drawContext The draw context.
     * @param x           The start of the box.
     * @param y           The start of the box.
     * @param width       The width of the box.
     * @param height      The height of the box.
     */
    static void renderBox(
        final DrawContext drawContext, final int x, final int y, final int width, final int height) {
        drawContext.fill(x, y, x + width, y + height, 0xFF373737);
        drawContext.fill(x + 1, y + 1, x + width, y + height, 0xFFFFFFFF);
        drawContext.fill(x + width, y, x + width - 1, y + 1, 0xFF8B8B8B);
        drawContext.fill(x, y + height, x + 1, y + height - 1, 0xFF8B8B8B);
        drawContext.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF8B8B8B);
    }

    static void renderBackgroundBox(final DrawContext drawContext, int x, int y, int width, int height) {
        drawContext.drawTexture(BACKGROUND_TEXTURE, x, y, 4, 4, 0, 0, 4, 4, 12, 12);
        drawContext.drawTexture(BACKGROUND_TEXTURE, x + 4, y, width - 8, 4, 4, 0, 4, 4, 12, 12);
        drawContext.drawTexture(BACKGROUND_TEXTURE, x + width - 4, y, 4, 4, 8, 0, 4, 4, 12, 12);

        drawContext.drawTexture(BACKGROUND_TEXTURE, x, y + 4, 4, height - 8, 0, 4, 4, 4, 12, 12);
        drawContext.drawTexture(BACKGROUND_TEXTURE, x + 4, y + 4, width - 8, height - 8, 4, 4, 4, 4, 12, 12);
        drawContext.drawTexture(BACKGROUND_TEXTURE, x + width - 4, y + 4, 4, height - 8, 8, 4, 4, 4, 12, 12);

        drawContext.drawTexture(BACKGROUND_TEXTURE, x, y + height - 4, 4, 4, 0, 8, 4, 4, 12, 12);
        drawContext.drawTexture(BACKGROUND_TEXTURE, x + 4, y + height - 4, width - 8, 4, 4, 8, 4, 4, 12, 12);
        drawContext.drawTexture(BACKGROUND_TEXTURE, x + width - 4, y + height - 4, 4, 4, 8, 8, 4, 4, 12, 12);
    }

    /**
     * Renders a box in minecraft's style.
     *
     * @param drawContext The draw context.
     * @param x           The start of the box.
     * @param y           The start of the box.
     * @param width       The width of the box.
     * @param height      The height of the box.
     */
    static void renderFilledBox(
        final DrawContext drawContext, final int x, final int y, final int width, final int height) {
        drawContext.fill(x, y, width, height, 0xFFC6C6C6);
    }

    /**
     * Draws a text with a max width.
     *
     * @param drawContext The current draw context.
     * @param text        The text to render.
     * @param width       The width of the text.
     * @param x           The x coordinate.
     * @param y           The y coordinate.
     * @param color       The color of the text.
     * @param shadow      If the text has shadow.
     */
    static void renderTextWithMaxWidth(
        final @NotNull DrawContext drawContext,
        final @NotNull Text text,
        final int width,
        final int x,
        final int y,
        final int color,
        final boolean shadow) {
        final TextRenderer textRenderer = getTextRendererOrNull();
        if (textRenderer == null) {
            return;
        }
        final int textWidth = textRenderer.getWidth(text);
        float scale = 1.0f;
        if (textWidth > width) {
            scale = width / (float) textWidth;
        }
        renderTextScaled(drawContext, text, scale, x, y, color, shadow);
    }

    /**
     * Gets the text renderer or null.
     *
     * @return The text renderer.
     */
    private static @Nullable TextRenderer getTextRendererOrNull() {
        if (MinecraftClient.getInstance() != null) {
            return MinecraftClient.getInstance().textRenderer;
        }
        return null;
    }

    /**
     * Draws a text at a scale.
     *
     * @param drawContext The current draw context.
     * @param text        The text to render.
     * @param scaleFactor The scale to render the text at.
     * @param x           The x coordinate.
     * @param y           The y coordinate.
     * @param color       The color of the text.
     * @param shadow      If the text has shadow.
     */
    static void renderTextScaled(
        final @NotNull DrawContext drawContext,
        final @NotNull Text text,
        final float scaleFactor,
        final int x,
        final int y,
        final int color,
        final boolean shadow) {
        final TextRenderer textRenderer = getTextRendererOrNull();
        if (textRenderer == null) {
            return;
        }
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(scaleFactor, scaleFactor, 1);
        drawContext.drawText(textRenderer, text, (int) (x / scaleFactor), (int) (y / scaleFactor), color, shadow);
        drawContext.getMatrices().pop();
    }


    /**
     * Renders a centered text at a scale.
     *
     * @param drawContext The current draw context.
     * @param text        The text to render.
     * @param scaleFactor The scale to render the text at.
     * @param x           The center x coordinate.
     * @param y           The y coordinate.
     * @param color       The color of the text.
     */
    static void renderTextCenteredScaled(
        final @NotNull DrawContext drawContext,
        final @NotNull Text text,
        final float scaleFactor,
        final int x,
        final int y,
        final int color) {
        final TextRenderer textRenderer = getTextRendererOrNull();
        if (textRenderer == null) {
            return;
        }
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(scaleFactor, scaleFactor, 1);
        drawContext.drawCenteredTextWithShadow(textRenderer,
            text,
            (int) (x / scaleFactor),
            (int) (y / scaleFactor),
            color);
        drawContext.getMatrices().pop();
    }

    /**
     * Renders a text into the world as a billboard.
     *
     * @param matrixStack            The current matrix stack.
     * @param position               The position.
     * @param text                   The text.
     * @param vertexConsumerProvider The vertex consumer.
     * @param size                   The size of the text.
     * @param center                 If the text should be centered.
     * @param throughWalls           If the text should be visible through walls.
     * @param color                  The color of the text.
     */
    static void renderTextInWorld(
        final MatrixStack matrixStack,
        final Vec3d position,
        final Text text,
        final VertexConsumerProvider vertexConsumerProvider,
        final float size,
        final boolean center,
        final boolean throughWalls,
        final int color) {
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        final Camera camera = minecraftClient.gameRenderer.getCamera();
        if (!camera.isReady() || minecraftClient.getEntityRenderDispatcher().gameOptions == null) {
            return;
        }
        final TextRenderer textRenderer = minecraftClient.textRenderer;
        final double d = camera.getPos().x;
        final double e = camera.getPos().y;
        final double f = camera.getPos().z;
        matrixStack.push();
        matrixStack.translate((float) (position.x - d), (float) (position.y - e) + 0.07f, (float) (position.z - f));
        matrixStack.multiplyPositionMatrix(new Matrix4f().rotation(camera.getRotation()));
        matrixStack.scale(-size, -size, size);
        final float g = center ? (float) (-textRenderer.getWidth(text)) / 2.0f : 0.0f;


        final float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        final int background = (int) (backgroundOpacity * 255.0f) << 24;

        textRenderer.draw(text,
            g,
            0.0f,
            color,
            false,
            matrixStack.peek().getPositionMatrix(),
            vertexConsumerProvider,
            TextRenderer.TextLayerType.SEE_THROUGH,
            background,
            0xF000F0);
        matrixStack.pop();
    }

    final class Sealed implements RenderUtils {}
}
