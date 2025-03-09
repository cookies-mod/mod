package codes.cookies.mod.config.categories.mining;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.Constants;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

@ConfigInfo(
		title = "Mineshaft"
)
@Category("shaft")
public class ShaftCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHAFT_ANNOUNCE)
	@ConfigEntry(id = "type")
	public static ShaftAnnouncementType type = ShaftAnnouncementType.CHAT;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHAFT_ENABLE)
	@ConfigEntry(id = "enabled")
	public static boolean enabled = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHAFT_TEXT)
	@ConfigEntry(id = "text")
	public static boolean text = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHAFT_BOX)
	@ConfigEntry(id = "box")
	public static boolean box = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHAFT_BEAM)
	@ConfigEntry(id = "beam")
	public static boolean beam = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHAFT_COLOR)
	@ConfigOption.Color(alpha = true)
	@ConfigEntry(id = "color")
	public static int color = Constants.MAIN_COLOR;
}
