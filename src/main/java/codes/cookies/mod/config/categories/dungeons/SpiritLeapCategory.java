package codes.cookies.mod.config.categories.dungeons;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

@ConfigInfo(
		title = "Spirit Leap"
)
@Category("spirit_leap")
public class SpiritLeapCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_USE_CLASS_COLOR)
	@ConfigEntry(id = "color_in_class_color")
	public static boolean colorInClassColor = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_ANNOUNCE_LEAPS)
	@ConfigEntry(id = "announce_leaps")
	public static boolean announceLeaps = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_ANNOUNCE_LEAP_COORDS)
	@ConfigEntry(id = "announce_coords")
	public static boolean announceLeapCoords = false;
	@CookiesOptions.Seperator(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_VANILLA)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_MODIFY_DEFAULT_IF_AVAILABLE)
	@ConfigEntry(id = "modify_normal")
	public static boolean modifyNormalIfPossible = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_USE_HEADS_IF_AVAILABLE)
	@ConfigEntry(id = "use_player_heads")
	public static boolean usePlayerHeadsInsteadOfClassItems = false;
	@CookiesOptions.Seperator(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_CUSTOM)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_ENABLE)
	@ConfigEntry(id = "spirit_leap_ui")
	public static boolean spiritLeapUi = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_SHOW_MAP)
	@ConfigEntry(id = "show_map")
	public static boolean showMap = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_SORT_BY_CLASS_NAME)
	@ConfigEntry(id = "sort_by_class_name")
	public static boolean sortByClassName = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SPIRIT_LEAP_COLOR)
	@ConfigOption.Color(alpha = true)
	@ConfigEntry(id = "fallback_color")
	public static int colorOption = 0xFFDCD3FF;

}
