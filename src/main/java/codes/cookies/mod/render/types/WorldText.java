package codes.cookies.mod.render.types;

import codes.cookies.mod.render.Renderable;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/**
 * A text rendered in the world.
 *
 * @param position              The position of the text.
 * @param text                  The text.
 * @param center                Whether the text is centered at the position.
 * @param size                  The size of the text.
 * @param offset                The y offset.
 * @param color                 The color of the text.
 * @param visibleThroughObjects Whether the text is visible through objects (e.g. walls).
 */
public record WorldText(
    Vec3d position,
    Text text,
    boolean center,
    float size,
    float offset,
    int color,
    boolean visibleThroughObjects
) implements Renderable {

    /**
     * @param position The position of the text.
     * @param text     The text.
     */
    public WorldText(Vec3d position, Text text) {
        this(position, text, true, 0.02f, 0f, -1, false);
    }

    /**
     * A text rendered in the world.
     *
     * @param position              The position of the text.
     * @param text                  The text.
     * @param visibleThroughObjects Whether the text is visible through objects (e.g. walls).
     */
    public WorldText(Vec3d position, Text text, boolean visibleThroughObjects) {
        this(position, text, true, 0.02f, 0f, -1, visibleThroughObjects);
    }

    /**
     * A text rendered in the world.
     *
     * @param position              The position of the text.
     * @param text                  The text.
     * @param color                 The color of the text.
     * @param visibleThroughObjects Whether the text is visible through objects (e.g. walls).
     */
    public WorldText(Vec3d position, Text text, int color, boolean visibleThroughObjects) {
        this(position, text, true, 0.02f, 0f, color, visibleThroughObjects);
    }

    /**
     * A text rendered in the world.
     *
     * @param position              The position of the text.
     * @param text                  The text.
     * @param offset                The y offset.
     * @param color                 The color of the text.
     * @param visibleThroughObjects Whether the text is visible through objects (e.g. walls).
     */
    public WorldText(Vec3d position, Text text, float offset, int color, boolean visibleThroughObjects) {
        this(position, text, true, 0.02f, offset, color, visibleThroughObjects);
    }

    @Override
    public void render(WorldRenderContext context) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Camera camera = context.camera();
        if (!camera.isReady() || minecraftClient.getEntityRenderDispatcher().gameOptions == null) {
            return;
        }
        TextRenderer textRenderer = minecraftClient.textRenderer;
        final MatrixStack matrices = context.matrixStack();
        matrices.push();
        matrices.translate((float) (position.x), (float) (position.y) + 0.07, (float) (position.z));
        matrices.multiply(camera.getRotation());
        matrices.scale(size, -size, size);
        float g = center ? (float) (-textRenderer.getWidth(text)) / 2.0f : 0.0f;
        textRenderer.draw(
            text,
            g - (offset / size),
            0.0f,
            color,
            false,
            matrices.peek().getPositionMatrix(),
            context.consumers(),
            TextRenderer.TextLayerType.NORMAL,
            0,
            0xF000F0
        );
        matrices.pop();
    }
}
