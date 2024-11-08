package codes.cookies.mod.repository.recipes.calculations;

import codes.cookies.mod.repository.RepositoryItem;

/**
 * A recipe result, something that can be used in a recipe.
 *
 * @param <T> The type of the recipe result.
 */
public interface RecipeResult<T extends RecipeResult<T>> {

    /**
     * Multiplies the amount of items by the multiplier.
     *
     * @param multiplier The multiplier.
     * @return The new instance with the applied multiplier.
     */
    T multiply(int multiplier);

    /**
     * Gets the amount of required items.
     *
     * @return The amount.
     */
    int getAmount();

    /**
     * Gets the {@linkplain RepositoryItem} corresponding to the result.
     *
     * @return The repository item.
     */
    RepositoryItem getRepositoryItem();

	RepositoryItem getRepositoryItemNotNull();

    /**
     * Gets the id of the result.
     *
     * @return The id.
     */
    String getId();

}
