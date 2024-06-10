package dev.morazzer.cookies.mod.repository;

import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeResult;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Simple ingredient data.
 */
@Getter
public class Ingredient implements RecipeResult<Ingredient> {

    private final String id;
    private final int amount;
    @Nullable
    private final RepositoryItem repositoryItem;

    /**
     * Creates an ingredient with of the specified id and the specified amount.
     *
     * @param id     The id.
     * @param amount The amount.
     */
    public Ingredient(String id, int amount) {
        this.id = id;
        this.amount = amount;
        if (id.contains("null_ovoid")) {
            System.out.println("l");
        }
        this.repositoryItem = RepositoryItem.of(id);
    }

    /**
     * Creates an ingredient of the provided string.
     * <br>
     * Format: namespace:id:amount
     *
     * @param ingredient The string representation of the ingredient.
     * @return The ingredient.
     */
    public static Ingredient of(String ingredient) {
        final String[] split = ingredient.split(":");

        String id = split[0];
        if (split.length == 3) {
            id += ":" + split[1];
        }

        final int amount;
        if (split.length == 1) {
            amount = 1;
        } else {
            amount = Integer.parseInt(split[split.length - 1]);
        }

        return new Ingredient(id, amount);
    }

    @Override
    public Ingredient multiply(int multiplier) {
        return new Ingredient(id, amount * multiplier);
    }

    /**
     * Gets the name or the id of the item if the item is null.
     *
     * @return The name or id.
     */
    public String getNameSafe() {
        return repositoryItem == null ? id : repositoryItem.getName();
    }

    /**
     * Merges two ingredients of the same id together.
     *
     * @param other The ingredient to merge with.
     * @return The merged ingredient.
     */
    public Ingredient merge(Ingredient other) {
        if (!this.getId().equals(other.getId())) {
            throw new IllegalArgumentException("Cannot merge ingredients with different ids");
        }

        return new Ingredient(this.id, this.amount + other.amount);
    }
}
