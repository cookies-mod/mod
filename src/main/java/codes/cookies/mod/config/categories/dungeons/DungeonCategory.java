package codes.cookies.mod.config.categories.dungeons;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

import java.awt.*;

@ConfigInfo(
		title = "Dungeon",
		description = "All dungeon related settings, including puzzle solvers, dungeon map and more!"
)
@Category(value = "dungeon", categories = {
		TerminalCategory.class,
		SpiritLeapCategory.class,
		PuzzleCategory.class,
		ClassColorCategory.class,
})
public class DungeonCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_USE_FEATURES)
	@ConfigEntry(id = "use_dungeon_features")
	public static boolean useDungeonFeatures = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_USE_BACKEND)
	@ConfigEntry(id = "relay_to_backend")
	public static boolean relayToBackend = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_GLOW_CLASS_COLOR)
	@ConfigEntry(id = "glow_in_class_color")
	public static boolean glowInClassColor = true;

	@CookiesOptions.Seperator(value = TranslationKeys.CONFIG_DUNGEON_CROESUS, hasDescription = true)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_CROESUS_HIGHLIGHT_UNCLAIMED)
	@ConfigEntry(id = "highlight_unclaimed_chests")
	public static boolean highlightUnclaimedChests = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_CROESUS_REPLACE_ITEM)
	@ConfigEntry(id = "replace_chest_item_with_highest_rarity_item")
	public static boolean replaceChestItemWithHighestRarityItem = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_DUNGEON_RENDER)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_MAP)
	@ConfigEntry(id = "render_map")
	public static boolean renderMap = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SHOW_PLAYER_SKULLS)
	@ConfigEntry(id = "show_player_skulls")
	public static boolean showPlayerSkulls = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_ROTATE_PLAYER_SKULLS)
	@ConfigEntry(id = "rotate_player_heads")
	public static boolean rotatePlayerHeads = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_SHOW_PLAYER_NAMES)
	@ConfigEntry(id = "show_player_names")
	public static boolean showPlayerNames = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_OVER_TEXT)
	@ConfigEntry(id = "render_over_room_text")
	public static boolean renderOverRoomText = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_KEEP_WITHER_DOOR)
	@ConfigEntry(id = "keep_wither_doors")
	public static boolean keepWitherDoors = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_SHOW_SECRETS)
	@ConfigEntry(id = "show_secrets")
	public static boolean showSecrets = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_SHOW_PUZZLE_NAME)
	@ConfigEntry(id = "show_puzzle_name")
	public static boolean showPuzzleName = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_ROOM_STATUS_AS_COLOR)
	@ConfigEntry(id = "show_room_status")
	public static boolean showRoomStatusAsTextColor = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_MAP_SHOW_TRAP_AS_CLEARED)
	@ConfigEntry(id = "show_trap_as_cleared")
	public static boolean showTrapAsCleared = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_MAP_BACKGROUND_COLOR)
	@ConfigOption.Color(alpha = true)
	@ConfigEntry(id = "map_background_color")
	public static int mapBackgroundColor = Color.DARK_GRAY.getRGB();

	@CookiesOptions.Button(value = TranslationKeys.CONFIG_DUNGEON_RENDER_MAP_REPOSITION, buttonText = TranslationKeys.CONFIG_DUNGEON_RENDER_MAP_REPOSITION_TEXT)
	@ConfigButton(text = "")
	public static final Runnable repositionMap = CookiesMod::openHudScreen;
}
