package codes.cookies.mod.config.categories.dungeons;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.Constants;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

@ConfigInfo(
		title = "Class Colors"
)
@Category("class_color")
public class ClassColorCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_CLASS_COLOR_HEALER)
	@ConfigOption.Color(presets = {0xA933DC, 0x6EB5FF, 0xEE9f27, 0xFF6666, Constants.SUCCESS_COLOR})
	@ConfigEntry(id = "healer")
	public static int healer = 0xA933DC;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_CLASS_COLOR_MAGE)
	@ConfigOption.Color(presets = {0xA933DC, 0x6EB5FF, 0xEE9f27, 0xFF6666, Constants.SUCCESS_COLOR})
	@ConfigEntry(id = "mage")
	public static int mage = 0x6EB5FF;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_CLASS_COLOR_BERS)
	@ConfigOption.Color(presets = {0xA933DC, 0x6EB5FF, 0xEE9f27, 0xFF6666, Constants.SUCCESS_COLOR})
	@ConfigEntry(id = "bers")
	public static int bers = 0xEE9f27;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_CLASS_COLOR_ARCH)
	@ConfigOption.Color(presets = {0xA933DC, 0x6EB5FF, 0xEE9f27, 0xFF6666, Constants.SUCCESS_COLOR})
	@ConfigEntry(id = "arch")
	public static int arch = 0xFF6666;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_CLASS_COLOR_TANK)
	@ConfigOption.Color(presets = {0xA933DC, 0x6EB5FF, 0xEE9f27, 0xFF6666, Constants.SUCCESS_COLOR})
	@ConfigEntry(id = "tank")
	public static int tank = Constants.SUCCESS_COLOR;

}
