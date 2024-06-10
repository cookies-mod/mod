package dev.morazzer.cookies.mod.repository.recipes;

import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.repository.Ingredient;

/**
 * A recipe to represent something that is smelted in the furnace.
 */
public class SmeltRecipe implements Recipe {

    private final Ingredient[] input;
    private final Ingredient output;

    @SuppressWarnings("MissingJavadoc")
    public SmeltRecipe(JsonObject jsonObject) {
        input = new Ingredient[] {Ingredient.of(jsonObject.get("in").getAsString())};
        output = Ingredient.of(jsonObject.get("out").getAsString());
    }

    @Override
    public Ingredient[] getIngredients() {
        return this.input;
    }

    @Override
    public Ingredient getOutput() {
        return this.output;
    }
}
