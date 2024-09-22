package dev.morazzer.cookies.mod.utils.items.types;

import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

/**
 * Data components for various features.
 */
public class MiscDataComponentTypes {

    public static final ComponentType<Boolean> ANVIL_HELPER_MODIFIED;
    public static final ComponentType<Boolean> FORGE_RECIPES_MODIFIED;
    public static final ComponentType<Unit> CRAFT_HELPER_MODIFIED;
    public static final ComponentType<Recipe> FORGE_RECIPE;
    public static final ComponentType<Integer> ITEM_SEARCH_SERVICE_MODIFIED;
	public static final ComponentType<Unit> ITEM_SEARCH_MATCH_SAME;

    static {
        ANVIL_HELPER_MODIFIED = new CookiesDataComponent<>(Identifier.of("cookies", "anvil_helper_modified"));
        FORGE_RECIPES_MODIFIED = new CookiesDataComponent<>(Identifier.of("cookies", "forge_recipes_modified"));
        CRAFT_HELPER_MODIFIED = new CookiesDataComponent<>(Identifier.of("cookies", "craft_helper_modified"));
        FORGE_RECIPE = new CookiesDataComponent<>(Identifier.of("cookies", "forge_recipe"));
		ITEM_SEARCH_SERVICE_MODIFIED = new CookiesDataComponent<>(Identifier.of("cookies", "item_search_service"));
   		ITEM_SEARCH_MATCH_SAME = new CookiesDataComponent<>(Identifier.of("cookies", "item_search_match_same"));
    }

}
