package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.util.math.Vec3d;

/**
 * Renders a box with outlines in the world.
 *
 * @param box      The box.
 * @param outlines The outlines.
 */
public record BoxWithOutlines(Box box, Outlines outlines) implements Renderable {
    /**
     * Creates a box and the corresponding outlines.
     *
     * @param start         The start location the box.
     * @param end           The end location of the box.
     * @param boxColor      The color of the box in argb.
     * @param outlinesColor The color of the outlines in argb.
     * @param lineWidth     The line width of the outlines.
     * @param throughWalls  Whether the box and the outlines should be visible through walls.
     */
    public BoxWithOutlines(Vec3d start, Vec3d end, int boxColor, int outlinesColor, int lineWidth,
                           boolean throughWalls) {
        this(
            new Box(start, end, boxColor, throughWalls),
            new Outlines(start, end, outlinesColor, lineWidth, throughWalls)
        );

    }

    private static Vec3d getVec(Vec3d start, Vec3d end) {
        return new Vec3d(end.x - start.x, end.y - start.y, end.z - start.z);
    }

    @Override
    public void render(WorldRenderContext context) {
        box.render(context);
        outlines.render(context);
    }

    @Override
    public boolean shouldRender(WorldRenderContext context) {
        return box.shouldRender(context) || outlines.shouldRender(context);
    }
}
