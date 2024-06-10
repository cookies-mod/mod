package dev.morazzer.cookies.mod.repository.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.morazzer.cookies.mod.repository.Ingredient;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A recipe that exists in skyblock.
 */
public interface Recipe {

    /**
     * Loads a collection of recipes.
     *
     * @param path The path to the file that should be loaded.
     */
    static void load(Path path) {
        if (!Files.exists(path)) {
            System.err.println("Unable to load recipe list. (FILE_NOT_FOUND)");
            return;
        }

        try {
            final String content = Files.readString(path, StandardCharsets.UTF_8);
            final JsonArray jsonArray = JsonUtils.CLEAN_GSON.fromJson(content, JsonArray.class);
            for (JsonElement jsonElement : jsonArray) {
                if (!(jsonElement instanceof final JsonObject jsonObject)) {
                    continue;
                }

                final JsonElement typeElement = jsonObject.get("type");
                if (!(typeElement instanceof JsonPrimitive)) {
                    continue;
                }

                final String type = typeElement.getAsString();
                final RecipeType recipeType = RecipeType.valueOf(type.toUpperCase());
                final Recipe apply = recipeType.getRecipeFunction().apply(jsonObject);
                add(apply);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a recipe.
     *
     * @param recipe The recipe to add.
     */
    static void add(Recipe recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getRepositoryItem() == null) {
                continue;
            }
            ingredient.getRepositoryItem().getUsedInRecipeAsIngredient().add(recipe);
        }
        if (recipe.getOutput() == null || recipe.getOutput().getRepositoryItem() == null) {
            System.err.println("Recipe output was not found");
            return;
        }
        recipe.getOutput().getRepositoryItem().getRecipes().add(recipe);
    }

    /**
     * Gets all ingredients used in the recipe.
     *
     * @return The ingredients.
     */
    Ingredient[] getIngredients();

    /**
     * Gets the output of the recipe.
     *
     * @return The output.
     */
    Ingredient getOutput();

}
