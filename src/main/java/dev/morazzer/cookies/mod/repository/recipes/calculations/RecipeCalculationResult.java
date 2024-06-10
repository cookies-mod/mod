package dev.morazzer.cookies.mod.repository.recipes.calculations;

import dev.morazzer.cookies.mod.repository.Ingredient;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The result of a recipe calculation.
 */
@Getter
@AllArgsConstructor
public class RecipeCalculationResult implements RecipeResult<RecipeCalculationResult> {

    Ingredient ingredient;
    List<RecipeResult> required;

    @Override
    public RecipeCalculationResult multiply(int multiplier) {
        return new RecipeCalculationResult(ingredient.multiply(multiplier),
            required.stream().map(recipeResult -> recipeResult.multiply(multiplier)).toList()
        );
    }

    @Override
    public int getAmount() {
        return this.ingredient.getAmount();
    }

    @Override
    public RepositoryItem getRepositoryItem() {
        return this.ingredient.getRepositoryItem();
    }

    @Override
    public String getId() {
        return this.ingredient.getId();
    }
}
