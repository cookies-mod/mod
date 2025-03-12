package codes.cookies.mod.config.categories.dungeons;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;

@ConfigInfo(
		title = "Terminal Solvers"
)
@Category("terminals")
public class TerminalCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_PREVENT_MISS_CLICKS)
	@ConfigEntry(id = "prevent_missclicks")
	public static boolean preventMissclicks = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_CHANGE_ALL_TO_SAME)
	@ConfigEntry(id = "change_all_to_same_color")
	public static boolean changeAllToSameColorTerminal = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_CLICK_IN_ORDER)
	@ConfigEntry(id = "click_in_order_terminal")
	public static boolean clickInOrderTerminal = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_CORRECT_ALL_PANES)
	@ConfigEntry(id = "correct_all_the_panes")
	public static boolean correctAllThePanesTerminal = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_SELECT_ALL_COLORS)
	@ConfigEntry(id = "select_all_colors")
	public static boolean selectAllColorsTerminal = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_STARTS_WITH_TERMINAL)
	@ConfigEntry(id = "starts_with")
	public static boolean startWithTerminal = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_MELODY)
	@ConfigEntry(id = "melody")
	public static boolean melodyTerminal = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_TERMINAL_MELODY_NOTIFIER)
	@ConfigEntry(id = "melody_notifier")
	public static boolean melodyNotifier = true;

}
