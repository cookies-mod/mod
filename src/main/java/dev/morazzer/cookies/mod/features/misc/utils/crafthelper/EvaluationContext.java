package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeResult;

@SuppressWarnings("MissingJavadoc")
public record EvaluationContext(RecipeResult<?> recipeResult, EvaluationContext parent) {}
