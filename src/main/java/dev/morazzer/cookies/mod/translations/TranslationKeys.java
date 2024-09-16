package dev.morazzer.cookies.mod.translations;

import org.jetbrains.annotations.NotNull;

public interface TranslationKeys {

	String MOD = "cookies";
	String KEYBIND = MOD + ".keybind";
	String SEARCH = MOD + ".search";

	//<editor-fold desc="Misc">
	String CLOSE = MOD + ".close";
	String GO_BACK = MOD + ".go_back";
	String TO_RECIPE_BOOK = MOD + ".to_recipe_book";
	String CLICK_TO_VIEW = MOD + ".click_to_view";
	String CLICK_TO_EDIT = MOD + ".click_to_edit";
	String CLICK_TO_SELECT = MOD + ".click_to_select";
	String RIGHT_CLICK_TO_EDIT = MOD + ".right_click_to_edit";
	String LEFT_CLICK_TO_SET = MOD + ".right_click_to_set";
	String LEFT_CLICK_TO_VIEW = MOD + ".left_click_to_view";

	String PAGE = MOD + ".page";
	String PAGE_WITH_NUMBER = PAGE + ".with_number";
	String PAGE_PREVIOUS = PAGE + ".previous";
	String PAGE_NEXT = PAGE + ".next";

	/**
	 * Arguments:
	 * 1. Item ID
	 */
	String ITEM_NOT_FOUND = MOD + ".item_not_found";
	String NOT_FOUND = MOD + ".not_found";

	String SELECT_SLOT = MOD + ".select_slot";
	String SELECT_SLOT_ELIGIBLE = SELECT_SLOT + ".eligible";
	String SELECT_SLOT_NOT_ELIGIBLE = SELECT_SLOT + ".not_eligible";
	//</editor-fold>

	//<editor-fold desc="Item Sources">
	String CRAFT_HELPER = MOD + ".craft_helper";
	String CRAFT_HELPER_LINE_1 = CRAFT_HELPER + ".line1";
	String CRAFT_HELPER_LINE_2 = CRAFT_HELPER + ".line2";

	String CRAFT_HELPER_PLACEMENT = CRAFT_HELPER + ".placement";
	//</editor-fold>

	//<editor-fold desc="Path stuff">
	String NAME_SUFFIX = ".name";
	String TOOLTIP_SUFFIX = ".tooltip";
	String VALUES_SUFFIX = ".values";
	String CATEGORIES_PART = ".categories";
	String SCREEN_PART = ".screen";
	//</editor-fold>

	//<editor-fold desc="Exception">
	String UNEXPECTED_ERROR = MOD + ".unexpected_error";
	String INTERNAL_ERROR = MOD + ".internal_error";
	//</editor-fold>

	//<editor-fold desc="Update">
	String UPDATE_AVAILABLE = MOD + ".update_available";
	String UPDATE_MODRINTH = MOD + ".update_modrinth";
	//</editor-fold>

	//<editor-fold desc="Plot Price Breakdown">
	String PLOT_PRICE_BREAKDOWN = MOD + ".plot_price_breakdown";
	String PLOT_PRICE_BREAKDOWN_PLOTS_MISSING = PLOT_PRICE_BREAKDOWN + ".plots_missing";
	String PLOT_PRICE_BREAKDOWN_PLOTS_OWNED = PLOT_PRICE_BREAKDOWN + ".plots_owned";
	String PLOT_PRICE_BREAKDOWN_MISSING = PLOT_PRICE_BREAKDOWN + ".missing";
	String PLOT_PRICE_BREAKDOWN_COMPOST_BREAKDOWN = PLOT_PRICE_BREAKDOWN + ".compost_breakdown";
	//</editor-fold>

	//<editor-fold desc="Compost Upgrades">
	String COMPOST_UPGRADE = MOD + ".compost_upgrade";
	String COMPOST_UPGRADE_MAX_TIER = COMPOST_UPGRADE + ".max_tier";
	String COMPOST_UPGRADE_REMAINING_COST = COMPOST_UPGRADE + ".remaining_cost";
	//</editor-fold>

	//<editor-fold desc="Hotm Utils">
	String HOTM_UTILS = MOD + ".hotm_utils";
	String HOTM_UTILS_COST_NEXT_10 = HOTM_UTILS + ".cost_next_10";
	String HOTM_UTILS_COST_TOTAL = HOTM_UTILS + ".cost_total";
	//</editor-fold>

	//<editor-fold desc="Item Stats">
	String ITEM_STATS = MOD + ".item_stats";
	String ITEM_STATS_VALUE = ITEM_STATS + ".value";
	String ITEM_STATS_VALUE_COINS = ITEM_STATS_VALUE + ".coins";
	String ITEM_STATS_VALUE_MOTES = ITEM_STATS_VALUE + ".motes";
	String ITEM_STATS_MUSEUM = ITEM_STATS_VALUE + ".museum";
	String ITEM_STATS_OBTAINED = ITEM_STATS_VALUE + ".obtained";
	//</editor-fold>

	//<editor-fold desc="Rancher Boots">
	String RANCHER_BOOTS = MOD + ".rancher_boots";
	String RANCHER_BOOTS_SAVE_GLOBAL = RANCHER_BOOTS + ".save_global";
	String RANCHER_BOOTS_RESET_TO_DEFAULT = RANCHER_BOOTS + ".reset_to_default";
	String RANCHER_BOOTS_FARMING_SPEEDS = RANCHER_BOOTS + ".farming_speeds";
	//</editor-fold>

	String COMMANDS = MOD + ".commands";

	//<editor-fold desc="Screen Forge">
	String SCREEN_FORGE_RECIPE_OVERVIEW = MOD + SCREEN_PART + ".forge_recipe_overview";
	String SCREEN_FORGE_RECIPE_OVERVIEW_TITLE = SCREEN_FORGE_RECIPE_OVERVIEW + ".title";
	String SCREEN_FORGE_RECIPE_OVERVIEW_VIEW_ALL = SCREEN_FORGE_RECIPE_OVERVIEW + ".view_all";
	String SCREEN_FORGE_RECIPE_OVERVIEW_RECIPE = SCREEN_FORGE_RECIPE_OVERVIEW + ".recipe";
	String SCREEN_FORGE_RECIPE_OVERVIEW_BACK_TO_FORGE_RECIPES = SCREEN_FORGE_RECIPE_OVERVIEW +
                                                                ".back_to_forge_recipes";
	//</editor-fold>

	//<editor-fold desc="Item Search (Screen)">
	String SCREEN_ITEM_SEARCH = MOD + SCREEN_PART + ".item_search";
	String SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT = SCREEN_ITEM_SEARCH + ".click_to_highlight";
	String SCREEN_ITEM_SEARCH_HIGHLIGHT = SCREEN_ITEM_SEARCH + ".highlight";
	String SCREEN_ITEM_SEARCH_TOTAL = SCREEN_ITEM_SEARCH + ".total";
	//</editor-fold>

	//<editor-fold desc="Item Search (Categories)">
	String ITEM_SEARCH = MOD + ".item_search";
	String ITEM_SEARCH_ALL = ITEM_SEARCH + ".all";
	String ITEM_SEARCH_ARMOR = ITEM_SEARCH + ".armor";
	String ITEM_SEARCH_WEAPONS = ITEM_SEARCH + ".weapons";
	String ITEM_SEARCH_MATERIAL = ITEM_SEARCH + ".material";
	String ITEM_SEARCH_MINION = ITEM_SEARCH + ".minion";
	//</editor-fold>

	//<editor-fold desc="Item Sources">
	String ITEM_SOURCE = MOD + ".item_source";
	String ITEM_SOURCE_ALL = ITEM_SOURCE + ".all";
	String ITEM_SOURCE_CHEST = ITEM_SOURCE + ".chest";
	String ITEM_SOURCE_INVENTORY = ITEM_SOURCE + ".inventory";
	String ITEM_SOURCE_SACK = ITEM_SOURCE + ".sack";
	String ITEM_SOURCE_STORAGE = ITEM_SOURCE + ".storage";
	//</editor-fold>

	//<editor-fold desc="Config">
	String CONFIG = MOD + ".config";

	//<editor-fold desc="Config/Cleanup">
	String CONFIG_CLEANUP = CONFIG + ".cleanup";

	String CONFIG_CLEANUP_CATEGORIES_COOP = CONFIG_CLEANUP + CATEGORIES_PART + ".coop";

	String CONFIG_CLEANUP_COOP_CLEANUP = CONFIG_CLEANUP + ".coop_cleanup";
	String CONFIG_CLEANUP_COOP_CLEANUP_VALUES = CONFIG_CLEANUP_COOP_CLEANUP + VALUES_SUFFIX;
	String CONFIG_CLEANUP_COOP_CLEANUP_VALUES_KEEP = CONFIG_CLEANUP_COOP_CLEANUP_VALUES + ".keep";
	String CONFIG_CLEANUP_COOP_CLEANUP_VALUES_EMPTY = CONFIG_CLEANUP_COOP_CLEANUP_VALUES + ".empty";
	String CONFIG_CLEANUP_COOP_CLEANUP_VALUES_ALL = CONFIG_CLEANUP_COOP_CLEANUP_VALUES + ".all";
	String CONFIG_CLEANUP_COOP_CLEANUP_VALUES_OTHER = CONFIG_CLEANUP_COOP_CLEANUP_VALUES + ".others";


	String CONFIG_CLEANUP_CATEGORIES_DUNGEONS = CONFIG_CLEANUP + CATEGORIES_PART + ".dungeons";

	String CONFIG_CLEANUP_HIDE_WATCHER_MESSAGES = CONFIG_CLEANUP + ".hide_watcher_messages";
	String CONFIG_CLEANUP_HIDE_POTION_EFFECT_MESSAGE = CONFIG_CLEANUP + ".potion_effect_message";
	String CONFIG_CLEANUP_HIDE_CLASS_MESSAGES = CONFIG_CLEANUP + ".hide_class_messages";
	String CONFIG_CLEANUP_HIDE_ULTIMATE_READY = CONFIG_CLEANUP + ".hide_ultimate_ready";
	String CONFIG_CLEANUP_HIDE_BLESSING_MESSAGE = CONFIG_CLEANUP + ".hide_blessing_message";
	String CONFIG_CLEANUP_HIDE_SILVERFISH_MESSAGE = CONFIG_CLEANUP + ".hide_silverfish_message";
	String CONFIG_CLEANUP_HIDE_DUNGEON_KEY_MESSAGE = CONFIG_CLEANUP + ".hide_dungeon_key_message";


	String CONFIG_CLEANUP_CATEGORIES_ITEMS = CONFIG_CLEANUP + CATEGORIES_PART + ".items";

	String CONFIG_CLEANUP_REMOVE_DUNGEON_STATS = CONFIG_CLEANUP + ".remove_dungeon_stats";
	String CONFIG_CLEANUP_REMOVE_REFORGE_STATS = CONFIG_CLEANUP + ".remove_reforge_stats";
	String CONFIG_CLEANUP_REMOVE_HPB_STATS = CONFIG_CLEANUP + ".remove_hpb_stats";
	String CONFIG_CLEANUP_REMOVE_GEMSTONE_STATS = CONFIG_CLEANUP + ".remove_gemstone_stats";
	String CONFIG_CLEANUP_REMOVE_GEAR_SCORE = CONFIG_CLEANUP + ".remove_gear_score";
	String CONFIG_CLEANUP_REMOVE_BLANK_LINE = CONFIG_CLEANUP + ".remove_blank_lines";
	String CONFIG_CLEANUP_REMOVE_FULL_SET_BONUS = CONFIG_CLEANUP + ".remove_full_set_bonus";
	String CONFIG_CLEANUP_REMOVE_GEMSTONE_LINE = CONFIG_CLEANUP + ".remove_gemstone_line";
	String CONFIG_CLEANUP_REMOVE_ABILITY = CONFIG_CLEANUP + ".remove_ability";
	String CONFIG_CLEANUP_REMOVE_PIECE_BONUS = CONFIG_CLEANUP + ".remove_piece_bonus";
	String CONFIG_CLEANUP_REMOVE_ENCHANTS = CONFIG_CLEANUP + ".remove_enchants";
	String CONFIG_CLEANUP_REMOVE_REFORGE = CONFIG_CLEANUP + ".remove_reforge";
	String CONFIG_CLEANUP_REMOVE_SOULBOUND = CONFIG_CLEANUP + ".remove_soulbound";
	String CONFIG_CLEANUP_REMOVE_RUNES = CONFIG_CLEANUP + ".remove_runes";

	String CONFIG_CLEANUP_CATEGORIES_PETS = CONFIG_CLEANUP + CATEGORIES_PART + ".pets";

	String CONFIG_CLEANUP_REMOVE_MAX_LEVEL = CONFIG_CLEANUP + ".remove_max_level";
	String CONFIG_CLEANUP_REMOVE_ACTIONS = CONFIG_CLEANUP + ".remove_actions";
	String CONFIG_CLEANUP_REMOVE_HELD_ITEM = CONFIG_CLEANUP + ".remove_held_item";
	//</editor-fold>
	//<editor-fold desc="Config/Dev">
	String CONFIG_DEV = CONFIG + ".dev";

	String CONFIG_DEV_HIDE_CONSOLE_SPAM = CONFIG_DEV + ".hide_console_spam";
	String CONFIG_DEV_REPO = CONFIG_DEV + ".repo";
	String CONFIG_DEV_DATA_REPO = CONFIG_DEV + ".data_repo";
	String CONFIG_DEV_DATA_REPO_BRANCH = CONFIG_DEV_DATA_REPO + ".branch";
	String CONFIG_DEV_BACKEND = CONFIG_DEV + ".backend";
	String CONFIG_DEV_BACKEND_CONNECT = CONFIG_DEV_BACKEND + ".connect";
	String CONFIG_DEV_BACKEND_SERVER = CONFIG_DEV_BACKEND + ".server";
	String CONFIG_DEV_BACKEND_RECONNECT = CONFIG_DEV_BACKEND + ".reconnect";
	String CONFIG_DEV_BACKEND_VERSION_SUFFIX = CONFIG_DEV_BACKEND + ".version_suffix";
	String CONFIG_DEV_BACKEND_RECONNECT_VALUE = CONFIG_DEV_BACKEND_RECONNECT + ".button";
	String CONFIG_FARMING_SHOW_PLOT_PRICE_BREAKDOWN = CONFIG_DEV + ".show_plot_price_breakdown";
	//</editor-fold>
	//<editor-fold desc="Config/Farming">
	String CONFIG_FARMING = CONFIG + ".farming";
	String CONFIG_FARMING_YAW_PITCH_DISPLAY = CONFIG_FARMING + ".yaw_pitch_display";

	String CONFIG_FARMING_CATEGORIES_RANCHERS = CONFIG_FARMING + CATEGORIES_PART + ".ranchers";
	String CONFIG_FARMING_SHOW_RANCHER_SPEED = CONFIG_FARMING + ".show_rancher_speed";
	String CONFIG_FARMING_SHOW_RANCHER_OPTIMAL_SPEED = CONFIG_FARMING + ".show_rancher_optimal_speeds";

	String CONFIG_FARMING_CATEGORIES_COMPOST = CONFIG_FARMING + CATEGORIES_PART + ".compost";
	String CONFIG_FARMING_SHOW_COMPOST_PRICE_BREAKDOWN = CONFIG_FARMING + ".show_compost_price_breakdown";
	String CONFIG_FARMING_COMPOST_SORT_ORDER = CONFIG_FARMING + ".compost_sort_order";
	String CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_ASCENDING =
			CONFIG_FARMING_COMPOST_SORT_ORDER + VALUES_SUFFIX + ".ascending";
	String CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_DESCENDING =
			CONFIG_FARMING_COMPOST_SORT_ORDER + VALUES_SUFFIX + ".descending";
	String CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_UNSORTED =
			CONFIG_FARMING_COMPOST_SORT_ORDER + VALUES_SUFFIX + ".unsorted";

	String CONFIG_FARMING_CATEGORIES_VISITOR = CONFIG_FARMING + CATEGORIES_PART + ".visitor";
	String CONFIG_FARMING_VISITOR_MATERIAL_HELPER = CONFIG_FARMING + ".visitor_material_helper";

	String CONFIG_FARMING_CATEGORIES_JACOBS = CONFIG_FARMING + CATEGORIES_PART + ".jacobs";
	String CONFIG_FARMING_HIGHLIGHT_UNCLAIMED_JACOB_CONTENTS = CONFIG_FARMING + ".highlight_unclaimed_jacobs_contents";
	//</editor-fold>
	//<editor-fold desc="Config/Helpers">
	String CONFIG_HELPERS = CONFIG + ".helpers";

	String CONFIG_HELPERS_CRAFT_HELPER = CONFIG_HELPERS + ".craft_helper";
	String CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS = CONFIG_HELPERS_CRAFT_HELPER + ".locations";
	String CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS_BUTTON = CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS + ".button";
	String CONFIG_HELPERS_ANVIL_HELPER = CONFIG_HELPERS + ".anvil_helper";
	String CONFIG_HELPERS_CHEST_TRACKER = CONFIG_HELPERS + ".chest_tracker";
	//</editor-fold>

	//<editor-fold desc="Config/Mining">
	String CONFIG_MINING = CONFIG + ".mining";

	String CONFIG_MINING_MODIFY_COMMISSIONS = CONFIG_MINING + ".modify_commissions";
	String CONFIG_MINING_PUZZLER_SOLVER = CONFIG_MINING + ".puzzler_solver";
	String CONFIG_MINING_CATEGORIES_HOTM = CONFIG_MINING + CATEGORIES_PART + ".hotm";
	String CONFIG_MINING_SHOW_HOTM_PERK_LEVEL_AS_STACK_SIZE = CONFIG_MINING + ".show_hotm_perk_level_as_stack_size";
	String CONFIG_MINING_HIGHLIGHT_DISABLED_HOTM_PERKS = CONFIG_MINING + ".highlight_disabled_hotm_perks";
	String CONFIG_MINING_SHOW_NEXT_10_COST = CONFIG_MINING + ".show_next_10_cost";
	String CONFIG_MINING_SHOW_TOTAL_COST = CONFIG_MINING + ".show_total_cost";
	//</editor-fold>
	//<editor-fold desc="Config/Misc">
	String CONFIG_MISC = CONFIG + ".misc";

	String CONFIG_MISC_ENABLE_SCROLL_TOOLTIPS = CONFIG_MISC + ".scroll_tooltips";
	String CONFIG_MISC_STORAGE_PREVIEW = CONFIG_MISC + ".storage_preview";
	String CONFIG_MISC_SHOW_PING = CONFIG_MISC + ".show_ping";
	String CONFIG_MISC_CATEGORIES_ITEMS = CONFIG_MISC + CATEGORIES_PART + ".items";
	String CONFIG_MISC_SHOW_ITEM_CREATION_DATE = CONFIG_MISC + ".show_item_creation_date";
	String CONFIG_MISC_SHOW_ITEM_DONATED_TO_MUSEUM = CONFIG_MISC + ".show_item_donated_to_museum";
	String CONFIG_MISC_SHOW_ITEM_NPC_VALUE = CONFIG_MISC + ".show_item_npc_value";
	String CONFIG_MISC_CATEGORIES_RENDER = CONFIG_MISC + CATEGORIES_PART + "render";
	String CONFIG_MISC_HIDE_OWN_ARMOR = CONFIG_MISC + ".hide_own_armor";
	String CONFIG_MISC_HIDE_OTHER_ARMOR = CONFIG_MISC + ".hide_other_armor";
	String CONFIG_MISC_SHOW_DYE_ARMOR = CONFIG_MISC + ".show_dye_armor";
	String CONFIG_MISC_HIDE_FIRE_ON_ENTITIES = CONFIG_MISC + ".hide_fire_on_entities";
	String CONFIG_MISC_HIDE_LIGHTNING_BOLT = CONFIG_MISC + ".hide_lightning_bolt";
	String CONFIG_MISC_CATEGORIES_RENDER_UI = CONFIG_MISC + CATEGORIES_PART + ".render_ui";
	String CONFIG_MISC_HIDE_POTION_EFFECTS = CONFIG_MISC + ".hide_potion_effects";
	String CONFIG_MISC_HIDE_HEALTH = CONFIG_MISC + ".hide_health";
	String CONFIG_MISC_HIDE_ARMOR = CONFIG_MISC + ".hide_armor";
	String CONFIG_MISC_HIDE_FOOD = CONFIG_MISC + ".hide_food";
	String CONFIG_MISC_CATEGORIES_RENDER_INVENTORY = CONFIG_MISC + CATEGORIES_PART + ".render_inventory";
	String CONFIG_MISC_SHOW_PET_LEVEL = CONFIG_MISC + ".show_pet_level";
	String CONFIG_MISC_SHOW_PET_RARITY_IN_LEVEL_TEXT = CONFIG_MISC + ".show_pet_rarity_in_level_text";
	String CONFIG_MISC_SHOW_FORGE_RECIPE_STACK = CONFIG_MISC + ".show_forge_recipe_stack";

	//</editor-fold>
	//<editor-fold desc="Config/Dungeon">
	String CONFIG_DUNGEON = CONFIG + ".dungeon";

	String CONFIG_DUNGEON_USE_BACKEND = CONFIG_DUNGEON + ".use_backend";

	String CONFIG_DUNGEON_RENDER = CONFIG_DUNGEON + ".render";
	String CONFIG_DUNGEON_SHOW_PLAYER_SKULLS = CONFIG_DUNGEON_RENDER + ".show_player_skulls";
	String CONFIG_DUNGEON_SHOW_PLAYER_NAMES = CONFIG_DUNGEON_RENDER + ".show_player_names";
	String CONFIG_DUNGEON_RENDER_OVER_TEXT = CONFIG_DUNGEON_RENDER + ".over_text";
	String CONFIG_DUNGEON_RENDER_KEEP_WITHER_DOOR = CONFIG_DUNGEON_RENDER + ".keep_wither_door";
	String CONFIG_DUNGEON_RENDER_SHOW_SECRETS = CONFIG_DUNGEON_RENDER + ".show_secrets";
	String CONFIG_DUNGEON_RENDER_SHOW_PUZZLE_NAME = CONFIG_DUNGEON_RENDER + ".show_puzzle_name";
	String CONFIG_DUNGEON_RENDER_ROOM_STATUS_AS_COLOR = CONFIG_DUNGEON_RENDER + ".room_status_as_color";
	String CONFIG_DUNGEON_RENDER_MAP_BACKGROUND = CONFIG_DUNGEON_RENDER + ".map_background";
	String CONFIG_DUNGEON_RENDER_MAP_BACKGROUND_COLOR = CONFIG_DUNGEON_RENDER_MAP_BACKGROUND + ".color";
	//</editor-fold>
	// internal

	//<editor-fold desc="Config/Dev">
	String CONFIG_SEARCH = CONFIG + ".search";
	//</editor-fold>
	//<editor-fold desc="Config/Toggled">
	String CONFIG_TOGGLED = CONFIG + ".toggled";
	//</editor-fold>
	//</editor-fold>
	String BACKEND = MOD + ".backend";
	String BACKEND_WRONG_VERSION = BACKEND + ".wrong_version";

	static String name(@NotNull String translationKey) {
		return translationKey + NAME_SUFFIX;
	}

	static String tooltip(@NotNull String translationKey) {
		return translationKey + TOOLTIP_SUFFIX;
	}
}
