package codes.cookies.mod.repository.recipes.calculations;

import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.recipes.Recipe;
import codes.cookies.mod.utils.Result;

/**
 * Printer to display a {@linkplain RecipeCalculationResult}
 */
public class RecipePrinter {

    /**
     * Calculate and print the recipe.
     *
     * @param recipe The recipe.
     * @return The printed form.
     */
    public static String printRecipe(Recipe recipe) {
		final Result<RecipeCalculationResult, String> calculate = RecipeCalculator.calculate(recipe);
		if (calculate.isError()) {
			return calculate.getError().orElse("An internal error occurred.");
		}
		return calculate.getResult().map(RecipePrinter::printRecipe).orElse("An internal error occurred.");
    }

    /**
     * Print the recipe.
     *
     * @param recipeCalculationResult The calculation result.
     * @return The printed form.
     */
    public static String printRecipe(RecipeCalculationResult recipeCalculationResult) {
        return printRecipe(recipeCalculationResult, 0);
    }

    private static String printRecipe(RecipeResult<?> calculationResult, int depth) {
        StringBuilder sb = new StringBuilder();

        if (calculationResult instanceof RecipeCalculationResult subResult) {
            sb.append(" ".repeat(depth)).append(subResult.getIngredient().getAmount()).append("x ")
                .append(subResult.getIngredient().getNameSafe()).append("\n");
            subResult.required.forEach(recipeResult -> {
                sb.append(printRecipe(recipeResult, depth + 1));
            });
        } else if (calculationResult instanceof Ingredient ingredient) {
            sb.append(" ".repeat(depth)).append(ingredient.getAmount()).append("x ").append(ingredient.getNameSafe())
                .append("\n");
        }

        return sb.toString();
    }

}
