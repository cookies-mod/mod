package codes.cookies.mod.render;

/**
 * Accessor to allow for setting fields for outlined block entities.
 */
public interface BlockEntityAccessor {

    /**
     * @param highlighted Whether the block is highlighted or not.
     */
    void cookies$setHighlighted(boolean highlighted);

    /**
     * @return Whether the block is highlighted or not.
     */
    boolean cookies$isHighlighted();

    /**
     * @param highlighted The color of the outline.
     */
    void cookies$setHighlightedColor(int highlighted);

    /**
     * @return The color of the outline.
     */
    int cookies$getHighlightedColor();
}
