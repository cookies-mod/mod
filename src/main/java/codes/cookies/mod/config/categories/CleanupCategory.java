package codes.cookies.mod.config.categories;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable;

import lombok.RequiredArgsConstructor;

@ConfigInfo(title = "Cleanup")
@Category("cleanup")
public class CleanupCategory {

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_CLEANUP_CATEGORIES_COOP)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_COOP_CLEANUP)
	@ConfigEntry(id = "coop_cleanup")
	public static CoopCleanup coopCleanup = CoopCleanup.UNCHANGED;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_CLEANUP_CATEGORIES_DUNGEONS)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_HIDE_WATCHER_MESSAGES)
	@ConfigEntry(id = "hide_watcher_messages")
	public static boolean hideWatcherMessages = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_HIDE_POTION_EFFECT_MESSAGE)
	@ConfigEntry(id = "hide_potion_effect_message")
	public static boolean hidPotionEffectMessage = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_HIDE_CLASS_MESSAGES)
	@ConfigEntry(id = "hide_class_messages")
	public static boolean hideClassMessages = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_HIDE_ULTIMATE_READY)
	@ConfigEntry(id = "hide_ultimate_ready")
	public static boolean hideUltimateReady = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_HIDE_BLESSING_MESSAGE)
	@ConfigEntry(id = "hide_blessing_messages")
	public static boolean hideBlessingMessages = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_HIDE_SILVERFISH_MESSAGE)
	@ConfigEntry(id = "hide_silverfish_message")
	public static boolean hideSilverfishMessage = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_HIDE_DUNGEON_KEY_MESSAGE)
	@ConfigEntry(id = "hide_dungeon_key_message")
	public static boolean hideDungeonKeyMessage = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_CLEANUP_CATEGORIES_ITEMS)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_DUNGEON_STATS)
	@ConfigEntry(id = "remove_dungeon_stats")
	public static boolean removeDungeonStats = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_REFORGE_STATS)
	@ConfigEntry(id = "remove_reforge_stats")
	public static boolean removeReforgeStats = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_HPB_STATS)
	@ConfigEntry(id = "remove_hpb_stats")
	public static boolean removeHpbStats = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_GEMSTONE_STATS)
	@ConfigEntry(id = "remove_gemstone_stats")
	public static boolean removeGemstoneStats = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_GEAR_SCORE)
	@ConfigEntry(id = "remove_gear_score")
	public static boolean removeGearScore = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_BLANK_LINE)
	@ConfigEntry(id = "remove_blank_line")
	public static boolean removeBlankLine = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_FULL_SET_BONUS)
	@ConfigEntry(id = "remove_full_set_bonus")
	public static boolean removeFullSetBonus = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_GEMSTONE_LINE)
	@ConfigEntry(id = "remove_gemstone_line")
	public static boolean removeGemstoneLine = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_ABILITY)
	@ConfigEntry(id = "remove_ability")
	public static boolean removeAbility = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_PIECE_BONUS)
	@ConfigEntry(id = "remove_piece_bonus")
	public static boolean removePieceBonus = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_ENCHANTS)
	@ConfigEntry(id = "remove_enchants")
	public static boolean removeEnchants = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_REFORGE)
	@ConfigEntry(id = "remove_reforge")
	public static boolean removeReforge = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_SOULBOUND)
	@ConfigEntry(id = "remove_soulbound")
	public static boolean removeSoulbound = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_RUNES)
	@ConfigEntry(id = "remove_runes")
	public static boolean removeRunes = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_CLEANUP_CATEGORIES_PETS)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_MAX_LEVEL)
	@ConfigEntry(id = "remove_max_level")
	public static boolean removeMaxLevel = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_ACTIONS)
	@ConfigEntry(id = "remove_actions")
	public static boolean removeActions = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_CLEANUP_REMOVE_HELD_ITEM)
	@ConfigEntry(id = "remove_held_item")
	public static boolean removeHeldItem = false;

	@RequiredArgsConstructor
	public enum CoopCleanup implements Translatable {
		UNCHANGED(TranslationKeys.CONFIG_CLEANUP_COOP_CLEANUP_VALUES_KEEP),
		EMPTY(TranslationKeys.CONFIG_CLEANUP_COOP_CLEANUP_VALUES_EMPTY),
		ALL(TranslationKeys.CONFIG_CLEANUP_COOP_CLEANUP_VALUES_ALL),
		OTHER(TranslationKeys.CONFIG_CLEANUP_COOP_CLEANUP_VALUES_OTHER);

		private final String translationKey;

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}

}
