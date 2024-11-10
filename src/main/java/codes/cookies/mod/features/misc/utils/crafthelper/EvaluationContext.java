package codes.cookies.mod.features.misc.utils.crafthelper;

import codes.cookies.mod.repository.recipes.calculations.RecipeResult;

import java.util.Stack;

public record EvaluationContext(EvaluationContext parent, RecipeResult<?> recipeResult, Stack<Integer> stack) {
	public EvaluationContext push(RecipeResult<?> recipeResult) {
		return new EvaluationContext(this, recipeResult, stack);
	}
}
