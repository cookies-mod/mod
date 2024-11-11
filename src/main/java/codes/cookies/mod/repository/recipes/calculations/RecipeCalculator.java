package codes.cookies.mod.repository.recipes.calculations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.Recipe;
import codes.cookies.mod.utils.Result;

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
			"gold_block",
			"gold_ingot",
			"emerald",
			"coal_block",
			"coal",
			"iron_block",
			"iron_ingot",
			"slime_ball",
			"slime_block"
	};

	/**
	 * Calculates the ingredients required to craft a certain item.
	 *
	 * @param recipe The recipe.
	 * @return The result.
	 */
	public static Result<RecipeCalculationResult, String> calculate(Recipe recipe) {
		return calculate(recipe, new CalculationContext(defaultBlacklist));
	}

	/**
	 * Calculates the ingredients required to craft a certain item.
	 *
	 * @param repositoryItem The item.
	 * @return The result.
	 */
	public static Result<RecipeCalculationResult, String> calculate(RepositoryItem repositoryItem) {
		return getBestRecipe(repositoryItem, false).map(RecipeCalculator::calculate)
				.orElseGet(() -> Result.error("No recipe found :c"));
	}

	/**
	 * Calculates the ingredients required to craft a certain item.
	 *
	 * @param recipe  The recipe.
	 * @param context The context.
	 * @return The result.
	 */
	public static Result<RecipeCalculationResult, String> calculate(Recipe recipe, CalculationContext context) {
		List<RecipeResult<?>> list = new ArrayList<>();

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

			if (context.hasBeenVisited(ingredient.getId())) {
				continue;
			}
			final Optional<Recipe> bestRecipe = getBestRecipe(repositoryItem, true);
			if (bestRecipe.isEmpty()) {
				return Result.error("Passed recipe empty check but has no recipe (%s)".formatted(ingredient.getId()));
			}
			final Recipe subRecipe = bestRecipe.get();
			context.push(ingredient.getId());
			final Result<RecipeCalculationResult, String> subCalculation = calculate(subRecipe, context);
			if (subCalculation.isError()) {
				return subCalculation;
			}
			final RecipeCalculationResult calculate = subCalculation.unbox();
			if (calculate == null) {
				return Result.error("Recipe calculation failed (%s)".formatted(ingredient.getId()));
			}
			context.pop();
			list.add(
					calculate.multiply((int) Math.ceil((double) ingredient.getAmount() / subRecipe.getOutput()
							.getAmount())));
		}

		return Result.success(new RecipeCalculationResult(recipe.getOutput(), list));
	}

	private static Optional<Recipe> getBestRecipe(RepositoryItem repositoryItem, boolean flip) {
		if (repositoryItem == null) {
			return Optional.empty();
		}
		return repositoryItem.getRecipes()
				.stream().min(Comparator.comparingInt(recipe -> (flip ? -1 : 1) * recipe.getOutput().getAmount()));
	}
}
