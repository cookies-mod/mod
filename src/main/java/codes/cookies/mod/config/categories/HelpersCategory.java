package codes.cookies.mod.config.categories;


import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;

@Category(value = "helpers_category", categories = CraftHelperCategory.class)
@ConfigInfo(title = "Helpers", description = "All settings related to helpers :3")
public class HelpersCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_HELPERS_ANVIL_HELPER)
	@ConfigEntry(id = "anvil_helper")
	public static boolean anvilHelper = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_HELPERS_CHEST_TRACKER)
	@ConfigEntry(id = "chest_tracker")
	public static boolean chestTracker = true;

}
