package dev.morazzer.cookies.mod.repository;

import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeResult;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
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

    /**
     * Merges the iterable to a set of ingredients.
     *
     * @param ingredients The iterable.
     * @return The merged set.
     */
    public static Set<Ingredient> mergeToSet(Iterable<Ingredient> ingredients) {
        return mergeIngredients(ingredients, Collectors.toSet());
    }

    /**
     * Merges any given Iterable of Ingredients to not contain duplicated keys
     *
     * @param ingredients The original list of ingredients
     * @param collector   The collector to create the returned value
     * @param <T>         The return t ype of the collector
     * @return The collected ingredients
     */
    public static <T> T mergeIngredients(Iterable<Ingredient> ingredients, Collector<Ingredient, ?, T> collector) {
        HashMap<String, Ingredient> ingredientMap = new HashMap<>();
        ingredients.forEach(ingredient ->
                ingredientMap.merge(
                    ingredient.getId(),
                    ingredient,
                    Ingredient::merge
                                   )
                           );

        return ingredientMap.values().stream().collect(collector);
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

    /**
     * Merges the iterable to a list of ingredients.
     *
     * @param ingredients The iterable.
     * @return The merged list.
     */
    public static List<Ingredient> mergeToList(Iterable<Ingredient> ingredients) {
        return mergeIngredients(ingredients, Collectors.toList());
    }

    @Override
    public Ingredient multiply(int multiplier) {
        return new Ingredient(this.id, this.amount * multiplier);
    }

    /**
     * Gets the name or the id of the item if the item is null.
     *
     * @return The name or id.
     */
    public String getNameSafe() {
        return this.repositoryItem == null ? this.id : this.repositoryItem.getName().getString();
    }
}
