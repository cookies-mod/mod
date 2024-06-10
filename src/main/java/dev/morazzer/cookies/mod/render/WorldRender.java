package dev.morazzer.cookies.mod.render;

import dev.morazzer.cookies.mod.render.types.BlockHighlight;
import dev.morazzer.cookies.mod.render.types.ComplexBlock;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * All methods related to rendering objects into the world.
 */
public class WorldRender {

    private static final List<Renderable> renderables = new LinkedList<>();
    private static final List<BlockPos> highlightBlocks = new LinkedList<>();
    @Getter
    private static long renderTimeNs = -1;
    @Getter
    private static long outlinesNs = -1;
    @Getter
    private static boolean hasOutlines = false;

    static {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(WorldRender::beforeDebugRender);
    }

    /**
     * Adds a renderable to the render list.
     *
     * @param renderable The renderable to add.
     */
    public synchronized static void addRenderable(final Renderable renderable) {
        renderables.add(renderable);
    }

    /**
     * Removes a renderable from the render list.
     *
     * @param renderable The renderable to remove.
     */
    public synchronized static void removeRenderable(final Renderable renderable) {
        renderables.remove(renderable);
    }

    /**
     * Called after the entities have been drawn but before the framebuffer has been drawn.
     *
     * @param context The world render context.
     */
    public static void afterEntities(WorldRenderContext context) {


        long start = System.nanoTime();
        context.matrixStack().push();
        final Vec3d pos = context.camera().getPos();
        context.matrixStack().translate(-pos.x, -pos.y, -pos.z);

        hasOutlines = false;
        renderables.forEach(renderable -> {
            if (renderable instanceof ComplexBlock && ((ComplexBlock) renderable).outlines() ||
                renderable instanceof BlockHighlight) {
                if (renderable.shouldRender(context)) {
                    renderable.render(context);
                    hasOutlines = true;
                }
            }
        });
        context.matrixStack().pop();
        outlinesNs = System.nanoTime() - start;
    }

    /**
     * Called before invocation of the debug renderers.
     *
     * @param context The world render context.
     */
    private static void beforeDebugRender(WorldRenderContext context) {
        long start = System.nanoTime();
        context.matrixStack().push();
        final Vec3d pos = context.camera().getPos();
        context.matrixStack().translate(-pos.x, -pos.y, -pos.z);

        renderables.forEach(renderable -> {
            if ((renderable instanceof ComplexBlock && ((ComplexBlock) renderable).outlines()) ||
                renderable instanceof BlockHighlight) {
                return;
            }
            if (renderable.shouldRender(context)) {
                renderable.render(context);
            }
        });
        context.matrixStack().pop();
        renderTimeNs = System.nanoTime() - start;
    }

    /**
     * Gets the amount of active renderables.
     *
     * @return The amount.
     */
    public static int getAmountOfDrawables() {
        return renderables.size();
    }
}
