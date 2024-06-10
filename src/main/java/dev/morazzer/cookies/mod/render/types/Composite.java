package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Combine multiple different renderables into one.
 *
 * @param renderables The renderables to combine.
 */
public record Composite(Renderable... renderables) implements Renderable {
    @Override
    public void render(WorldRenderContext context) {
        for (Renderable renderable : renderables) {
            renderable.render(context);
        }
    }
}
