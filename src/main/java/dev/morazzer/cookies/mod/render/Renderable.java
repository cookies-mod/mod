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

	/**
	 * @return Whether this requires the entity outline shader to be enabled or not.
	 */
	default boolean requiresEntityOutlineShader() {
		return false;
	}

    /**
     * Called right after the renderable has been removed from the global render context.
     */
    default void remove() {}

	/**
	 * Whether the renderable should be removed, this may be used for self removing renderables.
	 * @return If it should be removed.
	 */
	default boolean shouldRemove() {
		return false;
	}
}
