package dev.morazzer.cookies.mod.repository.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.repository.Ingredient;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

/**
 * Recipe that describes an item that is forged.
 */
public class ForgeRecipe implements Recipe {

    @Getter
    private final ForgeRecipeType type;
    @Getter
    private final long duration;
    private final Ingredient[] ingredients;
    private final Ingredient output;

    /**
     * Creates a forge recipe from a json object.
     *
     * @param jsonObject The json object.
     */
    public ForgeRecipe(JsonObject jsonObject) {
        this.type = ForgeRecipeType.valueOf(jsonObject.get("type").getAsString().toUpperCase(Locale.ROOT));
        if (jsonObject.has("duration")) {
            this.duration = jsonObject.get("duration").getAsLong();
        } else {
            this.duration = -1;
        }
        JsonArray ingredients = jsonObject.get("ingredients").getAsJsonArray();
        Map<String, Ingredient> ingredientMap = new HashMap<>();

        for (JsonElement jsonElement : ingredients) {
            final Ingredient ingredient = Ingredient.of(jsonElement.getAsString());
            if (!ingredientMap.containsKey(ingredient.getId())) {
                ingredientMap.put(ingredient.getId(), ingredient);
            } else {
                ingredientMap.put(ingredient.getId(), ingredientMap.remove(ingredient.getId()).merge(ingredient));
            }
        }

        this.ingredients = new Ingredient[ingredientMap.size()];
        ingredientMap.values().toArray(this.ingredients);

        if (jsonObject.has("out")) {
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

	/**
	 * @return The time that is required to forge for the current player.
	 */
	public long getPlayerDuration() {
		double playerDuration = this.duration;

		final Optional<ProfileData> optionalProfile = ProfileStorage.getCurrentProfile();
		if (optionalProfile.isPresent()) {
			playerDuration *= optionalProfile.get().getHotmData().getQuickForgeMultiplier();
		}

		return (long) Math.ceil(playerDuration);
	}

    /**
     * The different types of forge actions.
     */
    @SuppressWarnings("MissingJavadoc")
    public enum ForgeRecipeType {
        FORGE, REFINE, CAST
    }
}
