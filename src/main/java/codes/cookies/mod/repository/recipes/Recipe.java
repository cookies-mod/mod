package codes.cookies.mod.repository.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.utils.json.JsonUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * A recipe that exists in skyblock.
 */
public interface Recipe {

    List<ForgeRecipe> ALL_FORGE_RECIPES = new LinkedList<>();
    List<CraftRecipe> ALL_CRAFT_RECIPES = new LinkedList<>();
    List<Recipe> ALL_RECIPES = new LinkedList<>() {
        @Override
        public boolean add(Recipe recipe) {
            if (recipe instanceof ForgeRecipe forgeRecipe) {
                ALL_FORGE_RECIPES.add(forgeRecipe);
            } else if (recipe instanceof CraftRecipe craftRecipe) {
                ALL_CRAFT_RECIPES.add(craftRecipe);
            }
            return super.add(recipe);
        }
    };

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
        ALL_RECIPES.add(recipe);
        if (recipe.getOutput() == null || recipe.getOutput().getRepositoryItem() == null) {
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
