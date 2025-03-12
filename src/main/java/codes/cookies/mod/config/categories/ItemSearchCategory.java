package codes.cookies.mod.config.categories;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.Constants;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

@Category("item_search")
@ConfigInfo(title = "Item Search Config", description = "All item search related settings :3")
public class ItemSearchCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_ITEM_SEARCH_CRAFTABLE)
	@ConfigEntry(id = "enable_craftable_items")
	public static boolean enableCraftableItems = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_ITEM_NON_SEARCH_CRAFTABLE)
	@ConfigEntry(id = "enable_non_craftable_items")
	public static boolean enableNonCraftableItems = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_ITEM_SHOW_ONLY_MISSING)
	@ConfigEntry(id = "show_only_missing_items")
	public static boolean showOnlyMissingItems = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_ITEM_SHOW_IN_MUSEUM)
	@ConfigEntry(id = "show_in_museum")
	public static boolean showInMuseum = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_ITEM_PERSIST_SEARCH)
	@ConfigEntry(id = "persist_search")
	public static boolean persistSearch = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_ITEM_HIGHLIGHT_COLOR)
	@ConfigOption.Color
	@ConfigEntry(id = "highlightColor")
	public static int highlightColor = Constants.MAIN_COLOR;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_ITEM_HIGHLIGHT_TIME)
	@ConfigEntry(id = "highlight_time")
	@ConfigOption.Range(min = 0, max = 300)
	@ConfigOption.Slider
	public static int highlightTime = 10;


}
