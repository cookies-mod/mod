package codes.cookies.mod.features.misc.utils.crafthelper;

import codes.cookies.mod.repository.recipes.calculations.RecipeResult;

import java.util.Stack;

/**
 * Evaluation context for the craft helper.
 * @param parent The parent instance.
 * @param recipeResult The recipe result.
 * @param stack The item amount stack.
 */
public record EvaluationContext(EvaluationContext parent, RecipeResult<?> recipeResult, Stack<Integer> stack) {
	public EvaluationContext push(RecipeResult<?> recipeResult) {
		return new EvaluationContext(this, recipeResult, stack);
	}
}
