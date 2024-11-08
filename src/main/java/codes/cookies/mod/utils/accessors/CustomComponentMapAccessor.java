package codes.cookies.mod.utils.accessors;

import net.minecraft.component.MergedComponentMap;

/**
 * Accessor to get/set the extra tracking map for custom item data.
 */
public interface CustomComponentMapAccessor {

    /**
     * Sets the custom component map.
     *
     * @param componentMap The component map.
     */
    void cookies$setMergedComponentMap(MergedComponentMap componentMap);

    /**
     * Gets the custom component map.
     *
     * @return The component map.
     */
	MergedComponentMap cookies$getMergedComponentMap();

}
