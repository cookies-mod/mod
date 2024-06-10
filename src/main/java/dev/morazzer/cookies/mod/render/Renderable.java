package dev.morazzer.cookies.mod.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Class that marks something as "Renderable"
 */
public interface Renderable {

    /**
     * Renders the object onto the screen or into the inventory.
     *
     * @param context The world render context.
     */
    void render(WorldRenderContext context);

    /**
     * @param context The world render context.
     * @return Whether the object should be rendered or not.
     */
    default boolean shouldRender(WorldRenderContext context) {
        return true;
    }

}
