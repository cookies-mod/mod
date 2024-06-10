package dev.morazzer.cookies.mod.repository.recipes;

import com.google.gson.JsonObject;
import java.util.function.Function;
import lombok.Getter;

/**
 * All supported types of recipes.
 */
@Getter
@SuppressWarnings("MissingJavadoc")
public enum RecipeType {

    FORGE(ForgeRecipe::new),
    CRAFT(CraftRecipe::new),
    SMELT(SmeltRecipe::new),
    ;

    private final Function<JsonObject, Recipe> recipeFunction;

    RecipeType(Function<JsonObject, Recipe> recipeFunction) {
        this.recipeFunction = recipeFunction;
    }
}
