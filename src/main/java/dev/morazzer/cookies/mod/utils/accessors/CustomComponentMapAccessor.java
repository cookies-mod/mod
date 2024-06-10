package dev.morazzer.cookies.mod.utils.accessors;

import net.minecraft.component.ComponentMapImpl;

/**
 * Accessor to get/set the extra tracking map for custom item data.
 */
public interface CustomComponentMapAccessor {

    /**
     * Sets the custom component map.
     *
     * @param componentMap The component map.
     */
    void cookies$setComponentMapImpl(ComponentMapImpl componentMap);

    /**
     * Gets the custom component map.
     *
     * @return The component map.
     */
    ComponentMapImpl cookies$getComponentMapImpl();

}
