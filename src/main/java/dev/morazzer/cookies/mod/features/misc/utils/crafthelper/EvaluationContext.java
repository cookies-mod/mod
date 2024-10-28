package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeResult;

import java.util.Stack;

public record EvaluationContext(EvaluationContext parent, RecipeResult<?> recipeResult, Stack<Integer> stack) {
	public EvaluationContext push(RecipeResult<?> recipeResult) {
		return new EvaluationContext(this, recipeResult, stack);
	}
}
