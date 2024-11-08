package codes.cookies.mod.data.profile.items;

import java.util.Collection;

/**
 * An item source that provides cached data.
 * @param <T> The type of the attached data.
 */
public interface ItemSource<T> {

    /**
     * Gets all items stored in the source.
     * @return The items.
     */
    Collection<Item<?>> getAllItems();

    /**
     * Gets the type of the item source.
     * @return The type.
     */
    ItemSources getType();

	void remove(Item<?> item);
}
