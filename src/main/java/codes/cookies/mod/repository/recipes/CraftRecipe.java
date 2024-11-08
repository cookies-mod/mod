package codes.cookies.mod.repository.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import codes.cookies.mod.repository.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * A recipe that is crafted in the crafting table.
 */
public class CraftRecipe implements Recipe {

    private final Ingredient[] ingredients;
    private final Ingredient output;

    /**
     * Creates a recipe form a json object.
     *
     * @param jsonObject The json object.
     */
    public CraftRecipe(JsonObject jsonObject) {
        JsonArray ingredients = jsonObject.get("ingredients").getAsJsonArray();

        Map<String, Ingredient> ingredientMap = new HashMap<>();

        for (JsonElement element : ingredients) {
            final Ingredient ingredient = Ingredient.of(element.getAsString());
            if (!ingredientMap.containsKey(ingredient.getId())) {
                ingredientMap.put(ingredient.getId(), ingredient);
            } else {
                ingredientMap.put(ingredient.getId(), ingredientMap.remove(ingredient.getId()).merge(ingredient));
            }
        }

        this.ingredients = new Ingredient[ingredientMap.size()];
        ingredientMap.values().toArray(this.ingredients);

        if (jsonObject.get("out") != null) {
            this.output = Ingredient.of(jsonObject.get("out").getAsString());
        } else {
            this.output = null;
        }
    }

    @Override
    public Ingredient[] getIngredients() {
        return this.ingredients;
    }

    @Override
    public Ingredient getOutput() {
        return this.output;
    }
}
