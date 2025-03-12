package codes.cookies.mod.config.categories.mining;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.features.mining.hollows.MinesOfDivanHelper;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@ConfigInfo(
		title = "Mining"
)
@Category(value = "mining",categories = {ShaftCategory.class})
public class MiningCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_MODIFY_COMMISSIONS)
	@ConfigEntry(id = "modify_commissions")
	public static boolean modifyCommissions = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_PUZZLER_SOLVER)
	@ConfigEntry(id = "puzzler_solver")
	public static boolean puzzlerSolver = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_MOD_HELPER)
	@ConfigEntry(id = "mod_helper")
	public static Observable<Boolean> modHelper = Observable.of(false);

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_GLOSSY_GEMSTONE_MESSAGE)
	@ConfigEntry(id = "glossy_gemstone_message")
	public static boolean glossyGemstoneMessage = true;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_MINING_CATEGORIES_HOTM)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHOW_HOTM_PERK_LEVEL_AS_STACK_SIZE)
	@ConfigEntry(id = "show_hotm_perk_level_as_stack_size")
	public static boolean showHotmPerkLevelAsStackSize = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_HIGHLIGHT_DISABLED_HOTM_PERKS)
	@ConfigEntry(id = "highlight_disabled_hotm_perks")
	public static boolean highlightDisabledHotmPerks = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHOW_NEXT_10_COST)
	@ConfigEntry(id = "show_next_10_cost")
	public static boolean showNext10Cost = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MINING_SHOW_TOTAL_COST)
	@ConfigEntry(id = "show_total_cost")
	public static boolean showTotalCost = false;

	static {
		modHelper.addListener(MinesOfDivanHelper::reset);
	}

}
