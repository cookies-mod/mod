package codes.cookies.mod.datagen.lang;

import java.util.concurrent.CompletableFuture;

import codes.cookies.mod.datagen.CookiesLanguageProvider;

import net.minecraft.registry.RegistryWrapper;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class EnglishLanguageProvider extends CookiesLanguageProvider {
	public EnglishLanguageProvider(
			FabricDataOutput fabricDataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(fabricDataOutput, registryLookupFuture);
	}

	@Override
	protected void generateLocals(
			RegistryWrapper.WrapperLookup registryLookup, CookiesTranslationBuilder translationBuilder) {

		translationBuilder.add(MOD, "Cookies Mod");
		translationBuilder.add(KEYBIND, "Cookies Mod");
		translationBuilder.add(SEARCH, "Item Search");

		translationBuilder.add(UNEXPECTED_ERROR, "An unexpected error occurred while executing the command!");
		translationBuilder.add(INTERNAL_ERROR,
				"An internal error occurred please report this on our discord. (Click to copy)");

		translationBuilder.add(UPDATE_AVAILABLE, "Your version of the mod isn't up-to-date!");
		translationBuilder.add(UPDATE_MODRINTH, "(Click here to open modrinth)");


		translationBuilder.addConfig(CONFIG_SEARCH, "Search", "Search all config settings.");
		translationBuilder.addConfig(CONFIG_TOGGLED, "Toggled", "Show either all on or off settings!");

		translationBuilder.add(ITEM_SOURCE_ALL, "All items");
		translationBuilder.add(ITEM_SOURCE_CHEST, "Chest");
		translationBuilder.add(ITEM_SOURCE_INVENTORY, "Inventory");
		translationBuilder.add(ITEM_SOURCE_MISC, "Misc");
		translationBuilder.add(ITEM_SOURCE_ACCESSORY_BAG, "Accessory Bag");
		translationBuilder.add(ITEM_SOURCE_CRAFTABLE, "Craftable");
		translationBuilder.add(ITEM_SOURCE_FORGE, "Forge");
		translationBuilder.add(ITEM_SOURCE_VAULT, "Vault");
		translationBuilder.add(ITEM_SOURCE_SACK_OF_SACKS, "Sack of Sacks");
		translationBuilder.add(ITEM_SOURCE_POTION_BAG, "Potion Bag");
		translationBuilder.add(ITEM_SOURCE_SACK, "Sack");
		translationBuilder.add(ITEM_SOURCE_STORAGE, "Storage");
		translationBuilder.add(ITEM_SOURCE_ENDERCHEST, "Enderchest");
		translationBuilder.add(ITEM_SOURCE_BACKPACK, "Backpack");
		translationBuilder.add("cookies.mod.keybinds", "Cookies Mod");
		translationBuilder.add("cookies.mod.search", "Item Search");
		translationBuilder.add("cookies.mod.garden.keybind_switch", "Garden Keybinds Switcher");
		translationBuilder.add("cookies.mod.garden.keybinds.enabled", "Garden Keybinds Enabled");
		translationBuilder.add("cookies.mod.garden.keybinds.disabled", "Garden Keybinds Disabled");

		this.addItemStats(translationBuilder);
		this.addMisc(translationBuilder);
		this.addForgeRecipeScreen(translationBuilder);
		this.addItemSearchScreen(translationBuilder);

		this.addHotmUtils(translationBuilder);
		this.addCompostUpgrades(translationBuilder);
		this.addPlotPriceBreakdown(translationBuilder);

		// Config
		this.addCleanupConfig(translationBuilder);
		this.addDevConfig(translationBuilder);
		this.addFarmingConfig(translationBuilder);
		this.addHelpersConfig(translationBuilder);
		this.addItemSearchConfig(translationBuilder);
		this.addMiningConfig(translationBuilder);
		this.addMiscConfig(translationBuilder);
		this.addDungeonConfig(translationBuilder);
	}

	private void addItemStats(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(ITEM_STATS_VALUE_COINS, "Coin Value");
		translationBuilder.add(ITEM_STATS_VALUE_MOTES, "Motes Value");
		translationBuilder.add(ITEM_STATS_MUSEUM, "Museum");
		translationBuilder.add(ITEM_STATS_OBTAINED, "Obtained");
	}

	private void addMisc(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(CLOSE, "Close");
		translationBuilder.add(GO_BACK, "Go Back");
		translationBuilder.add(TO_RECIPE_BOOK, "To Recipe Book");
		translationBuilder.add(CLICK_TO_VIEW, "Click to view");
		translationBuilder.add(CLICK_TO_EDIT, "Click to edit");
		translationBuilder.add(CLICK_TO_SELECT, "Click to select");
		translationBuilder.add(LEFT_CLICK_TO_VIEW, "Left-click to view");
		translationBuilder.add(LEFT_CLICK_TO_SET, "Left-click to set");
		translationBuilder.add(RIGHT_CLICK_TO_EDIT, "Right-click to edit");
		translationBuilder.add(RANCHER_BOOTS_SAVE_GLOBAL, "Save Global");
		translationBuilder.add(RANCHER_BOOTS_RESET_TO_DEFAULT, "Reset to default");
		translationBuilder.add(RANCHER_BOOTS_FARMING_SPEEDS, "Farming Speeds");

		translationBuilder.add(DROP_PROTECTION_MESSAGE, "Refusing temporarily disabled due to rare drop");

		translationBuilder.add(BLOCK_XYZ, "(%s, %s, %s)");
		translationBuilder.add(PAGE, "Page");
		translationBuilder.add(PAGE_WITH_NUMBER, "Page %s");
		translationBuilder.add(PAGE_NEXT, "Next Page");
		translationBuilder.add(PAGE_PREVIOUS, "Previous Page");

		translationBuilder.add(ITEM_NOT_FOUND, "Can't find item %s");
		translationBuilder.add(NOT_FOUND, "Not Found");

		translationBuilder.add(SELECT_SLOT_ELIGIBLE, "Eligible for selection");
		translationBuilder.add(SELECT_SLOT_NOT_ELIGIBLE, "Not eligible for selection");

		translationBuilder.add(CRAFT_HELPER, "Set craft helper item");
		translationBuilder.add(CRAFT_HELPER_LINE_1, "Set the recipe as the selected");
		translationBuilder.add(CRAFT_HELPER_LINE_2, "craft helper item!");

		translationBuilder.add(CRAFT_HELPER_PLACEMENT, "Example Inventory");

		translationBuilder.add(
				BACKEND_WRONG_VERSION,
				"Your version of the mod is outdated, please download a newer version to use the backend!");
	}

	private void addForgeRecipeScreen(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW, "Forge Recipes");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_TITLE, "(%s/%s) Forge Recipes");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_VIEW_ALL, "View all of the Forge Recipes!");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_RECIPE, "Forge Recipe");
		translationBuilder.add(SCREEN_FORGE_RECIPE_OVERVIEW_BACK_TO_FORGE_RECIPES, "To Forge Recipes");
	}

	private void addItemSearchScreen(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(SCREEN_ITEM_SEARCH, "Item Search");
		translationBuilder.add(SCREEN_ITEM_SEARCH_HIGHLIGHT, "Highlighting Chests");
		translationBuilder.add(SCREEN_ITEM_SEARCH_OVERVIEW, "Right-click to show overview!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_OVERVIEW_TITLE, "(%s/%s) Item Pages");
		translationBuilder.add(SCREEN_ITEM_SEARCH_REMOVE_FROM_CACHE, "Right-click to remove from cache!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT, "Left-click to highlight all items and chests!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_NO_CHESTS, "Left-click to highlight all items!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_VAULT, "Left-click to open Bank (Vault)!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_ACCESSORY_BAG,
				"Left-click to open accessory bag!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_SACK_OF_SACKS,
				"Left-click to open sack-of-sacks!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_POTION_BAG, "Left-click to open potion bag!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_ALL_CHEST, "Left-click to highlight all chests!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT_CHEST, "Left-click to highlight chest!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_SACKS, "Left-click to open sacks!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE, "Left-click to open and highlight storage!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE_PAGE, "Left-click to open storage page!");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE_PAGE_VALUE, "Page");
		translationBuilder.add(SCREEN_ITEM_SEARCH_CLICK_TO_OPEN_STORAGE_PAGE_STORAGE, "Type");
		translationBuilder.add(SCREEN_ITEM_SEARCH_TOTAL, "Total");

		translationBuilder.add(ITEM_SEARCH_ALL, "All");
		translationBuilder.add(ITEM_SEARCH_ARMOR, "Armor");
		translationBuilder.add(ITEM_SEARCH_MATERIAL, "Material");
		translationBuilder.add(ITEM_SEARCH_WEAPONS, "Weapons");
		translationBuilder.add(ITEM_SEARCH_MINION, "Minions");
	}

	private void addHotmUtils(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(HOTM_UTILS_COST_NEXT_10, "Cost (10)");
		translationBuilder.add(HOTM_UTILS_COST_TOTAL, "Cost (Total)");
	}

	private void addCompostUpgrades(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(COMPOST_UPGRADE_MAX_TIER, "Max Tier");
		translationBuilder.add(COMPOST_UPGRADE_REMAINING_COST, "Remaining Upgrade Cost");
	}

	private void addPlotPriceBreakdown(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_PLOTS_MISSING, "Plots missing");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_PLOTS_OWNED, "Plots owned");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_MISSING, "Missing");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN, "Breakdown");
		translationBuilder.add(PLOT_PRICE_BREAKDOWN_COMPOST_BREAKDOWN, "Compost Breakdown");
	}

	private void addCleanupConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_CLEANUP,
				"Cleanup",
				"Various cleanup settings, that either hide or modify what you see.");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_COOP, "Coop");

		translationBuilder.addConfig(CONFIG_CLEANUP_COOP_CLEANUP,
				"Collection tooltips",
				"Hides the names of coop members from the collection item.");

		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_KEEP, "Keep");
		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_EMPTY, "Empty");
		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_ALL, "All");
		translationBuilder.add(CONFIG_CLEANUP_COOP_CLEANUP_VALUES_OTHER, "Other");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_DUNGEONS, "Dungeons");

		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_WATCHER_MESSAGES,
				"Hide watcher messages",
				"Hides all watcher messages.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_POTION_EFFECT_MESSAGE,
				"Hide potion messages",
				"Hides the paused effects message.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_CLASS_MESSAGES,
				"Hide class messages",
				"Hides the class stat messages.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_ULTIMATE_READY,
				"Hide ultimate ready",
				"hides the ultimate ready message.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_BLESSING_MESSAGE,
				"Hide blessing messages",
				"Hides all blessing messages");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_SILVERFISH_MESSAGE,
				"Hide silverfish messages",
				"Hides the silverfish moving message.");
		translationBuilder.addConfig(CONFIG_CLEANUP_HIDE_DUNGEON_KEY_MESSAGE,
				"Hide key messages",
				"Hides the key pickup messages.");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_ITEMS, "Items");

		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_DUNGEON_STATS,
				"Remove dungeon stats",
				"Removes the dungeon stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_REFORGE_STATS,
				"Remove reforge stats",
				"Removes the reforge stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_HPB_STATS,
				"Remove hpb",
				"Removes the hot potato book stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_GEMSTONE_STATS,
				"Removes gemstone stats",
				"Remove the gemstone stats from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_GEAR_SCORE,
				"Remove gear score",
				"Removes the gear score from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_BLANK_LINE,
				"Remove blank lines",
				"Removes blank lines from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_FULL_SET_BONUS,
				"Remove full set bonus",
				"Removes the full set bonus from the item.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_GEMSTONE_LINE,
				"Remove gemstones",
				"Removes the gemstone line from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_ABILITY,
				"Remove abilities",
				"Removes abilities from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_PIECE_BONUS,
				"Remove piece bonus",
				"Remove the piece bonus from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_ENCHANTS, "Remove enchants", "Remove enchants from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_REFORGE, "Remove reforges", "Removes reforges from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_SOULBOUND,
				"Remove soulbound",
				"Removes the soulbound text from items.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_RUNES, "Remove runes", "Removes runes from items.");

		translationBuilder.add(CONFIG_CLEANUP_CATEGORIES_PETS, "Pets");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_MAX_LEVEL,
				"Remove max level",
				"Removes the max level and xp lines from pets.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_ACTIONS,
				"Remove actions",
				"Removes the left-click and right-click actions from the pet.");
		translationBuilder.addConfig(CONFIG_CLEANUP_REMOVE_HELD_ITEM,
				"Remove held item",
				"Removes the left-click and right-click actions from the pet.");
	}

	private void addDevConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_DEV, "Dev Config", "Development related config entries.");
		translationBuilder.addConfig(CONFIG_DEV_HIDE_CONSOLE_SPAM,
				"Remove console spam",
				"Removes spam from the console by canceling various logger invocations.");
		translationBuilder.add(CONFIG_DEV_REPO, "Data repository");
		translationBuilder.addConfig(CONFIG_DEV_DATA_REPO, "Data repo", "The github location of the data repo.");
		translationBuilder.addConfig(CONFIG_DEV_DATA_REPO_BRANCH, "Data repo branch", "The branch of the data repo.");
		translationBuilder.addConfig(CONFIG_DEV_BACKEND_RECONNECT, "Press button to ", "Reconnect to the backend.");
		translationBuilder.add(CONFIG_DEV_BACKEND_RECONNECT_VALUE, "Reconnect");
		translationBuilder.addConfig(CONFIG_DEV_BACKEND_CONNECT,
				"Connect to backend",
				"Whether the mod should connect to the backend or not.");
		translationBuilder.addConfig(CONFIG_DEV_BACKEND_SERVER, "Server Url", "Used to set the backend server url.");
		translationBuilder.addConfig(CONFIG_DEV_BACKEND_VERSION_SUFFIX,
				"Use version scheme",
				"Appends the current api version to the url.");
		translationBuilder.add(CONFIG_DEV_BACKEND, "Backend");
	}

	private void addFarmingConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_FARMING, "Farming Config", "Farming related settings.");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_PLOT_PRICE_BREAKDOWN,
				"Plot price breakdown",
				"Shows a breakdown of how much compost you need to unlock all plots.");
		translationBuilder.addConfig(CONFIG_FARMING_YAW_PITCH_DISPLAY,
				"Yaw/Pitch display",
				"Displays your yaw/pitch on the screen (in a non obnoxious way).");
		translationBuilder.add(CONFIG_FARMING_CATEGORIES_RANCHERS, "Rancher's Boots");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_RANCHER_SPEED,
				"Show rancher speed",
				"Shows the speed selected on ranchers boots as item stack size.");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_RANCHER_OPTIMAL_SPEED,
				"Show rancher overlay",
				"Show optimal speeds in the rancher's boots.");

		translationBuilder.add(CONFIG_FARMING_CATEGORIES_VISITORS, "Visitors");

		translationBuilder.addConfig(CONFIG_FARMING_VISITOR_DROP_PROTECTION,
				"Rare drop protection",
				"Prevents rejecting visitors with rare drops for 5s");

		translationBuilder.addConfig(CONFIG_FARMING_VISITOR_NOT_AS_RARE_DROP_PROTECTION,
				"Common drop protection",
				"Prevents rejecting visitors with drops that are not as rare as the ones in the rare drop protection.");

		translationBuilder.addConfig(CONFIG_FARMING_VISITOR_DROP_PROTECTION_DELAY,
				"Drop protection delay",
				"Delay in seconds before the visitor drop protection is removed.");

		translationBuilder.add(CONFIG_FARMING_SQUEAKY_MOUSEMAT, "Squeaky Mousemat");
		translationBuilder.addConfig(
				CONFIG_FARMING_SQUEAKY_MOUSEMAT_OVERLAY,
				"Enabled overlay",
				"Enables an overlay for the squeaky mousemat where you can save yaw/pitch for every crop.");

		translationBuilder.add(CONFIG_FARMING_CATEGORIES_COMPOST, "Composter");
		translationBuilder.addConfig(CONFIG_FARMING_SHOW_COMPOST_PRICE_BREAKDOWN,
				"Compost upgrade price",
				"Shows the amount of items required to max an upgrade.");
		translationBuilder.addConfig(CONFIG_FARMING_COMPOST_SORT_ORDER,
				"Item sort",
				"How the items should be sorted" + ".");
		translationBuilder.add(CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_ASCENDING, "Ascending");
		translationBuilder.add(CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_DESCENDING, "Descending");
		translationBuilder.add(CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_UNSORTED, "Unsorted");

		translationBuilder.addConfig(CONFIG_FARMING_VISITOR_MATERIAL_HELPER,
				"Show visitor materials",
				"Shows the amount of items a visitor needs down to the actual crop.");
		translationBuilder.add(CONFIG_FARMING_CATEGORIES_JACOBS, "Jacob / Contests");
		translationBuilder.addConfig(CONFIG_FARMING_HIGHLIGHT_UNCLAIMED_JACOB_CONTENTS,
				"Highlight unclaimed",
				"Highlight unclaimed jacob contests in his inventory.");

		translationBuilder.add(CONFIG_FARMING_RENDER, "Render");
		translationBuilder.addConfig(
				CONFIG_FARMING_RENDER_HIGHLIGHT_GLOWING_MUSHROOMS,
				"Highlight glowing mushrooms",
				"Highlights glowing mushrooms by drawing an outline around them.");

		translationBuilder.add(CONFIG_FARMING_CATEGORIES_GARDEN_KEYBINDS, "Garden Keybinds");
		translationBuilder.addConfig(CONFIG_FARMING_OPEN_KEYBIND_MENU, "Open Keybind Menu", "Opens the garden keybind menu.");

		translationBuilder.add(CONFIG_FARMING_OPEN_KEYBIND_TEXT, "Click Here!");
		translationBuilder.add(CONFIG_FARMING_KEYBIND_MENU_TITLE, "Garden Keybinds");
		translationBuilder.addConfig(CONFIG_FARMING_GARDEN_KEYBIND_PREDICATE, "Predicate: ", "When to enable garden keybinds.");

		translationBuilder.addConfig(CONFIG_MISC_NOTIFICATIONS_PEST, "Pest Warnings" ,"Warns you 10s before pests can spawn!");
		translationBuilder.addConfig(CONFIG_MISC_NOTIFICATIONS_PEST_ORDER, "Pest timer", """
				Changes what pest time to use for the notifications,
				depending on if a plot is sprayed it can have a 50%
				reduction in cooldown.
				
				Current -> Current plot timer
				First -> First time a pest can spawn on any plot
				Last -> Time at which pests can spawn everywhere""");
	}

	private void addHelpersConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_HELPERS,
				"Helpers",
				"Settings that help you with keeping track of certain things.");
		translationBuilder.addConfig(CONFIG_HELPERS_CRAFT_HELPER_SETTING,
				"Enable craft helper",
				"Shows the items required to craft something and your progress in the inventory.");
		translationBuilder.addConfig(CONFIG_HELPERS_CRAFT_HELPER, "Craft Helper","Settings related to the craft helper");
		translationBuilder.addConfig(CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS,
				"Craft Helper Location",
				"Edits the location of the craft helper.");
		translationBuilder.add(CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS_BUTTON, "Edit");
		translationBuilder.addConfig(CONFIG_HELPERS_ANVIL_HELPER,
				"Anvil Helper",
				"Highlights the same book in your inventory when combining them in an anvil.");
		translationBuilder.addConfig(CONFIG_HELPERS_CRAFT_HELPER_SOURCES, "Item Sources", "Allows you to modify the available item sources.");

		translationBuilder.addConfig(ITEM_SOURCE_CHEST, "Chest", "Allows island chest items");
		translationBuilder.addConfig(ITEM_SOURCE_INVENTORY, "Inventory", "Allows inventory items");
		translationBuilder.addConfig(ITEM_SOURCE_ACCESSORY_BAG, "Accessory Bag", "Allows accessory bag items");
		translationBuilder.addConfig(ITEM_SOURCE_FORGE, "Forge", "Allows forge items");
		translationBuilder.addConfig(ITEM_SOURCE_VAULT, "Vault", "Allows vault items");
		translationBuilder.addConfig(ITEM_SOURCE_SACK_OF_SACKS, "Sack of Sacks", "Allows sack of sacks items");
		translationBuilder.addConfig(ITEM_SOURCE_POTION_BAG, "Potion Bag", "Allows potion bag items");
		translationBuilder.addConfig(ITEM_SOURCE_SACK, "Sack", "Allows sack items");
		translationBuilder.addConfig(ITEM_SOURCE_STORAGE, "Storage", "Allows storage items");

		translationBuilder.addConfig(CONFIG_HELPERS_CHEST_TRACKER,
				"Chest Tracker",
				"Allows for tracking of chests on private island.");
	}

	private void addItemSearchConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_ITEM_SEARCH, "Item Search", "All item search related settings");
		translationBuilder.addConfig(CONFIG_ITEM_SEARCH_CRAFTABLE, "Enable craftable", "Shows craftable items");
		translationBuilder.addConfig(
				CONFIG_ITEM_NON_SEARCH_CRAFTABLE,
				"Show all craftable",
				"Shows all craftable items, even if you dont have all items.");
		translationBuilder.addConfig(
				CONFIG_ITEM_SHOW_ONLY_MISSING,
				"Only show missing items",
				"Only shows items that are out of reach of the supercraft in the overview.");
		translationBuilder.addConfig(CONFIG_ITEM_SHOW_IN_MUSEUM,
				"Show in museum",
				"Shows the status of museum items.");
		translationBuilder.addConfig(CONFIG_ITEM_HIGHLIGHT_COLOR, "Highlight Color", "The color to highlight chests in.");
		translationBuilder.addConfig(CONFIG_ITEM_HIGHLIGHT_TIME, "Highlight Time", "The time to highlight the chests for.");
		translationBuilder.addConfig(CONFIG_ITEM_PERSIST_SEARCH, "Persist Search", "Persists the search between item search screens.");
	}

	private void addMiningConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_MINING, "Mining", "All settings related to mining.");
		translationBuilder.addConfig(CONFIG_MINING_MODIFY_COMMISSIONS,
				"Modify commission items",
				"Visually changes the commission item to represent the stages a commission can be in.");

		translationBuilder.addConfig(CONFIG_MINING_MOD_HELPER,
				"Mod Helper",
				"Enables the mines of divan chest helper.");
		translationBuilder.addConfig(
				CONFIG_MINING_MOD_CRYSTAL_HUD,
				"Crystal Hud",
				"Enables the crystal hud, shows information about current crystal status, parts available and tools available.\n\n§cRequires the crystal player list widget to be active!");
		translationBuilder.addConfig(CONFIG_MINING_GLOSSY_GEMSTONE_MESSAGE, "Glossy gemstone message", "Adds messages when you drop a glossy gemstone");
		translationBuilder.addConfig(CONFIG_MINING_PUZZLER_SOLVER,
				"Puzzler solver",
				"Highlight the correct block for the puzzler.");
		translationBuilder.add(CONFIG_MINING_CATEGORIES_HOTM, "HOTM");
		translationBuilder.addConfig(CONFIG_MINING_SHOW_HOTM_PERK_LEVEL_AS_STACK_SIZE,
				"Show perk as size",
				"Shows the hotm perk level as item stack size.");
		translationBuilder.addConfig(CONFIG_MINING_HIGHLIGHT_DISABLED_HOTM_PERKS,
				"Highlight disabled",
				"Change disabled perks to redstone.");
		translationBuilder.addConfig(CONFIG_MINING_SHOW_NEXT_10_COST,
				"Cost for next 10",
				"Shows the cost for the next 10 levels");
		translationBuilder.addConfig(CONFIG_MINING_SHOW_TOTAL_COST, "Total cost", "Shows the total cost.");

		translationBuilder.addConfig(CONFIG_MINING_SHAFT, "Mineshaft", "Configs related to mineshafts.");
		translationBuilder.addConfig(CONFIG_MINING_SHAFT_ANNOUNCE, "Announce Shaft", "Announces the shaft and it's contents to either you or all of your party!\n\nExample: Mineshaft Entered | AMBE1 | 3L 1U");
		translationBuilder.addConfig(CONFIG_MINING_SHAFT_ENABLE, "Enable Helper", "Enables the corpse location helper!");
		translationBuilder.addConfig(CONFIG_MINING_SHAFT_TEXT, "Enable text", "Renders a text at every possible corpse's location!");
		translationBuilder.addConfig(CONFIG_MINING_SHAFT_BOX, "Enable box", "Renders a box under the corpse's location!");
		translationBuilder.addConfig(CONFIG_MINING_SHAFT_BEAM, "Enable beacon beam", "Renders a beacon beam at the corpse's location!");
		translationBuilder.addConfig(CONFIG_MINING_SHAFT_COLOR, "Highlight color", "Allows you to change the highlight color that is used for the helper.\n\n§eOnly works for new shafts!");

		translationBuilder.addConfig(CONFIG_MINING_POWDER_TRACKER, "Powder Tracker", "Settings related to powder tracker hud");
		translationBuilder.addConfig(CONFIG_MINING_POWDER_TRACKER_ENABLED, "Enable", "Enables the powder tracker!");
		translationBuilder.addConfig(CONFIG_MINING_POWDER_TRACKER_SHOW_MS, "Show ms", "Adds milliseconds to the timer");
		translationBuilder.addConfig(
				CONFIG_MINING_POWDER_TRACKER_PAUSE,
				"Pause Timer",
				"Pauses the timer after a set period of time");
		translationBuilder.addConfig(
				CONFIG_MINING_POWDER_TRACKER_PAUSE_VALUE,
				"Pause Delay",
				"The delay to pause after!");
		translationBuilder.addConfig(
				CONFIG_MINING_POWDER_TRACKER_EXTRA_DATA, "Show extra data", """
						Shows extra data for specific powder types.
						
						Gemstone: chests/min
						Glacite: shafts/min""");
		translationBuilder.addConfig(
				CONFIG_MINING_POWDER_TRACKER_SHAFT_TRACKING, "Shaft tracking", """
						Changes how to track mineshafts.
						
						Enter -> Whenever you enter a mineshafts (counts all)
						Find -> Counts mineshafts found, even not entered ones""");
		translationBuilder.addConfig(
				CONFIG_MINING_POWDER_TRACKER_TIMEOUT,
				"Timeout time",
				"Changes the time the tracker can be inactive for, before resetting!");
	}

	private void addMiscConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_MISC, "Misc Config", "Miscellaneous settings");
		translationBuilder.addConfig(CONFIG_MISC_EDIT_HUD, "To edit hud", "Opens a screen where you can edit the hud elements!");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_MUSEUM_ARMOR_SETS, "Show museum armor sets", "Shows the components of an armor set in the description.");
		translationBuilder.addConfig(CONFIG_MISC_ENABLE_SCROLL_TOOLTIPS, "Scrollable Tooltips", """
				Allows you to scroll through tooltips
				
				CTRL + Scroll -> move horizontal
				SHIFT + Scroll -> chop tooltips""");
		translationBuilder.addConfig(CONFIG_MISC_SIGN_EDIT_ENTER_SUBMITS,
				"Sign editing enter submits",
				"Enables hitting enter while editing a sign to submit it\n" +
						"shift + enter for current enter behavior.");
		translationBuilder.addConfig(CONFIG_MISC_STORAGE_PREVIEW,
				"Storage Preview",
				"Shows a preview of the content in the storage.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_PING, "Show Ping", "Shows the ping in the action bar");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_ITEMS, "Items");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_ITEM_CREATION_DATE,
				"Creation date",
				"Shows the creation dates of items.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_ITEM_NPC_VALUE, "NPC Value", "Show the npc value of items.");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_RENDER, "Render");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_OWN_ARMOR, "Hide own armor", "Hides your own armor.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_OTHER_ARMOR, "Hide others armor", "Hides others armor.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_DYE_ARMOR,
				"Show armor if dyed",
				"Shows the armor if a dye is applied to it.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_FIRE_ON_ENTITIES, "Hide fire", "Hide fire from entities.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_LIGHTNING_BOLT,
				"Hide lightning",
				"Hide the lightning bolt entity.");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_RENDER_UI, "Render - UI");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_POTION_EFFECTS,
				"Hide inventory potions",
				"Hides potion effects from the inventory.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_HEALTH, "Hide health bar", "Hide health bar from ui.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_ARMOR, "Hide armor bar", "Hide armor bar from ui.");
		translationBuilder.addConfig(CONFIG_MISC_HIDE_FOOD, "Hide food bar", "Hide food bar from ui.");
		translationBuilder.add(CONFIG_MISC_CATEGORIES_RENDER_INVENTORY, "Render - Inventory");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_PET_LEVEL,
				"Show pet level",
				"Shows the pet level as stack size.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_PET_RARITY_IN_LEVEL_TEXT,
				"Show rarity in level",
				"Shows the pet level in the color of the rarity");
		translationBuilder.addConfig(
				CONFIG_MISC_SHOW_ITEM_UPGRADES,
				"Show item stars",
				"Shows the amount of stars that are applied to an item in the slot text.");
		translationBuilder.addConfig(CONFIG_MISC_SHOW_FORGE_RECIPE_STACK,
				"Show forge recipes",
				"Shows forge recipes in the recipe book");

		translationBuilder.addConfig(CONFIG_MISC_NOTIFICATIONS_PRIMAL_FEAR, "Fear Notifications", "Notifies you shortly before you can spawn a primal fear");
		translationBuilder.addConfig(CONFIG_MISC_NOTIFICATIONS_ENABLED, "Enable", "Enables the notifications");
		translationBuilder.addConfig(CONFIG_MISC_NOTIFICATIONS_ENABLED_HUD, "Enable Hud", "Enables the hud for the pests timer.");
		translationBuilder.addConfig(CONFIG_MISC_NOTIFICATIONS_TYPE, "Notification type", """
				Changes how you get the notifications.
				
				Chat -> Simple chat message
				Toast -> Popup in the top right of your screen
				Both -> Chat message + popup""");
		translationBuilder.addConfig(CONFIG_MISC_NOTIFICATION_SOUND, "Enable sound", "Plays a sound when sending the notification");
	}

	private void addDungeonConfig(CookiesTranslationBuilder translationBuilder) {
		translationBuilder.addConfig(CONFIG_DUNGEON, "Dungeon Config", "Various settings related to dungeons");
		translationBuilder.addConfig(CONFIG_DUNGEON_USE_FEATURES,
				"Enabled dungeon features",
				"Enables or disables all dungeon features. This will not work for ongoing runs.");
		translationBuilder.addConfig(CONFIG_DUNGEON_USE_BACKEND,
				"Relay to backend",
				"Whether information should be exchanged with the backend or not");

		translationBuilder.add(CONFIG_DUNGEON_RENDER, "Render");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_MAP,
				"Enable map",
				"Enables or disables the map rendering. This will not disable the underlying features.");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_MAP_REPOSITION,
				"To reposition",
				"Allows you to scale and move the dungeon map.");
		translationBuilder.add(CONFIG_DUNGEON_RENDER_MAP_REPOSITION_TEXT, "Click Here!");
		translationBuilder.add(CONFIG_DUNGEON_RENDER_MAP_REPLACEMENT_LINE_1, "Click element to move around");
		translationBuilder.add(CONFIG_DUNGEON_RENDER_MAP_REPLACEMENT_LINE_2, "Use + and - to change size of selected element");
		translationBuilder.add(CONFIG_DUNGEON_RENDER_MAP_REPLACEMENT_LINE_3, "Scale: %s");
		translationBuilder.add(CONFIG_DUNGEON_SPIRIT_LEAP_CUSTOM, "Spirit Leap - Custom");
		translationBuilder.add(CONFIG_DUNGEON_SPIRIT_LEAP_VANILLA, "Spirit Leap - Vanilla");
		translationBuilder.addConfig(CONFIG_DUNGEON_SHOW_PLAYER_SKULLS,
				"Show player skulls",
				"Shows the player skull instead of the map marker.");
		translationBuilder.addConfig(CONFIG_DUNGEON_ROTATE_PLAYER_SKULLS,
				"Rotate player skulls",
				"Rotate the player skulls to the direction they are looking, else draw an arrow showing it instead.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SHOW_PLAYER_NAMES,
				"Show player names",
				"Shows the names of the players on the map.");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_OVER_TEXT,
				"Player over room",
				"Renders the player name over the dungeon room text.");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_KEEP_WITHER_DOOR,
				"Keep wither doors",
				"Prevents wither doors from changing to normal ones.");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_SHOW_SECRETS,
				"Show secrets",
				"Shows information about the secrets in rooms.");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_SHOW_PUZZLE_NAME,
				"Show puzzle name",
				"Shows the puzzle name (if known).");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_ROOM_STATUS_AS_COLOR,
				"Show status color",
				"Shows the current room status as color.");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_MAP_BACKGROUND,
				"Map background",
				"Renders a monochrome background behind the map.");
		translationBuilder.addConfig(
				CONFIG_DUNGEON_RENDER_MAP_SHOW_TRAP_AS_CLEARED,
				"Show trap as cleared",
				"Always shows the trap as cleared (white text) instead of opened (red text).");
		translationBuilder.addConfig(CONFIG_DUNGEON_RENDER_MAP_BACKGROUND_COLOR,
				"Background color",
				"The color to use for the map background");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP,
				"Spirit Leap",
				"Settings related to the spirit leap ui.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP_ENABLE, "Enable UI", "Enables the spirit leap ui.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP_MODIFY_DEFAULT_IF_AVAILABLE,
				"Modify default ui",
				"Changes the default ui if there is exactly one player per class.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP_USE_HEADS_IF_AVAILABLE,
				"Use heads if available",
				"Uses the player heads instead of the class items.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP_SHOW_MAP,
				"Show dungeon map",
				"Shows the dungeon map in the ui.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP_SORT_BY_CLASS_NAME,
				"Sort by class",
				"Sorts the players by their class in the spirit overlay.");
		translationBuilder.addConfig(CONFIG_DUNGEON_TERMINAL, "Terminals", "All settings related to terminals.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP_COLOR,
				"Background color",
				"Changes the background color of the ui widgets.");
		translationBuilder.addConfig(CONFIG_DUNGEON_SPIRIT_LEAP_USE_CLASS_COLOR,
				"Use class colors",
				"Uses the class colors instead of the default color.");
		translationBuilder.addConfig(CONFIG_DUNGEON_GLOW_CLASS_COLOR,
				"Class based glow color",//"Glow teammates by class colors",
				"Overrides the default hypixel glowing by rank to glow by class color.");
		translationBuilder.addConfig(CONFIG_DUNGEON_CLASS_COLOR,
				"Class colors",
				"The colors used for every class");
		translationBuilder.addConfig(CONFIG_DUNGEON_CLASS_COLOR_HEALER,
				"Healer",
				"The color used for healer.");
		translationBuilder.addConfig(CONFIG_DUNGEON_CLASS_COLOR_MAGE,
				"Mage",
				"The color used for mage.");
		translationBuilder.addConfig(CONFIG_DUNGEON_CLASS_COLOR_BERS,
				"Berserk",
				"The color used for berserk.");
		translationBuilder.addConfig(CONFIG_DUNGEON_CLASS_COLOR_ARCH,
				"Archer",
				"The color used for archer.");
		translationBuilder.addConfig(CONFIG_DUNGEON_CLASS_COLOR_TANK,
				"Tank",
				"The color used for tank.");

		translationBuilder.addConfig(CONFIG_DUNGEON_TERMINAL_PREVENT_MISS_CLICKS,
				"Prevent Missclicks",
				"Prevents you from clicking wrong options.");
		translationBuilder.addConfig(CONFIG_DUNGEON_TERMINAL_CHANGE_ALL_TO_SAME,
				"Change all to same",
				"Enables the change all to same solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_TERMINAL_CLICK_IN_ORDER,
				"Click in order",
				"Enables the click in order solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_TERMINAL_CORRECT_ALL_PANES,
				"Correct all panes",
				"Enables the correct all panes solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_TERMINAL_SELECT_ALL_COLORS,
				"Select all colors",
				"Enables the select all colors solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_TERMINAL_STARTS_WITH_TERMINAL,
				"Starts with",
				"Enables the starts with solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_PUZZLE, "Puzzles", "All settings related to puzzles.");
		translationBuilder.addConfig(CONFIG_DUNGEON_PUZZLE_CREEPER_BEAMS_SOLVER,
				"Creeper Beams",
				"Enables the creeper beams puzzle solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_PUZZLE_HIGHER_LOWER_SOLVER,
				"Higher/Lower",
				"Enables the higher or lower puzzle solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_PUZZLE_QUIZ_SOLVER, "Quiz", "Enables the quiz puzzle solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_PUZZLE_THREE_WEIRDOS_SOLVER,
				"Three Weirdos",
				"Enables the three weirdos puzzle solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_PUZZLE_WATER_BOARD_SOLVER,
				"Water Board",
				"Enables the water board solver.");
		translationBuilder.addConfig(CONFIG_DUNGEON_PUZZLE_WATER_BOARD_CREDITS, "Credits to Desco1",
				"""
						The original oneflow solver was created by Desco1
						check out their Github at Desco1/WaterSolver
						Furthermore thank you to drek1984, Jade and bonsai
						which where helping Desco1 with the original
						and as well skytils for parts of the original solver.""");
	}
}
