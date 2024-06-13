package dev.morazzer.cookies.mod.repository.recipes.calculations;

import dev.morazzer.cookies.mod.repository.Ingredient;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import java.util.ArrayList;
import java.util.List;

/**
 * "Calculator" to retrieve information about a recipe and its ingredients.
 */
public class RecipeCalculator {

    private static final String[] defaultBlacklist = {
        "hay_block",
        "wheat",
        "lapis_block",
        "lapis_lazuli",
        "redstone_block",
        "redstone",
        "diamond_block",
        "diamond",
        "emerald_block",
        "emerald",
        "coal_block",
        "coal",
        "iron_block",
        "iron"
    };

    /**
     * Calculates the ingredients required to craft a certain item.
     *
     * @param recipe The recipe.
     * @return The result.
     */
    public static RecipeCalculationResult calculate(Recipe recipe) {
        return calculate(recipe, new CalculationContext(defaultBlacklist));
    }

    /**
     * Calculates the ingredients required to craft a certain item.
     *
     * @param repositoryItem The item.
     * @return The result.
     */
    public static RecipeCalculationResult calculate(RepositoryItem repositoryItem) {
        return calculate(getBestRecipe(repositoryItem, false));
    }

    /**
     * Calculates the ingredients required to craft a certain item.
     *
     * @param recipe  The recipe.
     * @param context The context.
     * @return The result.
     */
    public static RecipeCalculationResult calculate(Recipe recipe, CalculationContext context) {
        List<RecipeResult> list = new ArrayList<>();

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getRepositoryItem() == null) {
                list.add(ingredient);
                continue;
            }

            final RepositoryItem repositoryItem = ingredient.getRepositoryItem();
            if (repositoryItem.getRecipes().isEmpty() || !context.canVisit(ingredient.getId())) {
                list.add(ingredient);
                continue;
            }


            final Recipe recipe1 = getBestRecipe(repositoryItem, true);
            if (context.hasBeenVisited(ingredient.getId())) {
                continue;
            }
            context.push(ingredient.getId());
            final RecipeCalculationResult calculate = calculate(recipe1, context);
            context.pop();
            list.add(
                calculate.multiply((int) Math.ceil((double) ingredient.getAmount() / recipe1.getOutput().getAmount())));
        }

        return new RecipeCalculationResult(recipe.getOutput(), list);
    }

    private static Recipe getBestRecipe(RepositoryItem repositoryItem, boolean flip) {
        return repositoryItem.getRecipes().stream().min((o1, o2) -> {
            int distanceOne = o1.getOutput().getAmount();
            int distanceTwo = o2.getOutput().getAmount();

            return (flip ? -1 : 1) * Integer.compare(distanceOne, distanceTwo);
        }).orElse(null);
    }

    private static int distance(Recipe recipe, int amount) {
        return amount % recipe.getOutput().getAmount();
    }

}
