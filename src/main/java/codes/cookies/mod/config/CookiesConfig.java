package codes.cookies.mod.config;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.categories.CleanupCategory;
import codes.cookies.mod.config.categories.DevCategory;
import codes.cookies.mod.config.categories.FarmingCategory;
import codes.cookies.mod.config.categories.HelpersCategory;
import codes.cookies.mod.config.categories.ItemSearchCategory;
import codes.cookies.mod.config.categories.MiscCategory;
import codes.cookies.mod.config.categories.dungeons.DungeonCategory;
import codes.cookies.mod.config.categories.mining.MiningCategory;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;

@Config(value = "cookies-mod", categories = {
		MiscCategory.class,
		ItemSearchCategory.class,
		HelpersCategory.class,
		MiningCategory.class,
		FarmingCategory.class,
		DungeonCategory.class,
		CleanupCategory.class,
		DevCategory.class
})
@ConfigInfo(
		title = "Cookies Mod Config",
		icon = "cookie",
		description = "test"
)
public class CookiesConfig {

	@CookiesOptions.Button(value = TranslationKeys.CONFIG_MISC_EDIT_HUD, buttonText = TranslationKeys.CLICK_TO_EDIT)
	@ConfigButton(text = "")
	public static final Runnable openHud = CookiesMod::openHudScreen;

}
