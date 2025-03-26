package codes.cookies.mod.config.categories;

import codes.cookies.mod.config.system.HudSetting;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.config.system.Parent;
import codes.cookies.mod.features.dungeons.map.DungeonMapHud;
import codes.cookies.mod.render.hud.HudEditScreen;
import codes.cookies.mod.utils.cookies.Constants;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Foldable;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.ButtonOption;
import codes.cookies.mod.config.system.options.ColorOption;
import codes.cookies.mod.config.system.options.TextDisplayOption;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import net.minecraft.item.Items;

/**
 * All settings related to the dungeon map and dungeon in general.
 */
public class DungeonConfig extends Category {
	/**
	 * @return The instance of the dungeon config.
	 */
	public static DungeonConfig getInstance() {
		return ConfigManager.getConfig().dungeonConfig;
	}

	public DungeonConfig() {
		super(new ItemBuilder(Items.PLAYER_HEAD).setSkin(
						"eyJ0aW1lc3RhbXAiOjE1Nzg0MDk0MTMxNjksInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzliNTY4OTViOTY1OTg5NmFkNjQ3ZjU4NTk5MjM4YWY1MzJkNDZkYjljMWIwMzg5YjhiYmViNzA5OTlkYWIzM2QiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==")
				.build(), CONFIG_DUNGEON);
	}

	public BooleanOption useDungeonFeatures = new BooleanOption(CONFIG_DUNGEON_USE_FEATURES, true);
	public BooleanOption relayToBackend = new BooleanOption(CONFIG_DUNGEON_USE_BACKEND, true);

	public TerminalFoldable terminalFoldable = new TerminalFoldable();
	public SpiritLeapFoldable spiritLeapFoldable = new SpiritLeapFoldable();
	public PuzzleFoldable puzzleFoldable = new PuzzleFoldable();
	public ClassColorFoldable classColorFoldable = new ClassColorFoldable();
	public CroesusFoldable croesusFoldable = new CroesusFoldable();
	public BooleanOption glowClassColor = new BooleanOption(CONFIG_DUNGEON_GLOW_CLASS_COLOR, true);
	public PartyChatCommandsFoldable partyChatCommandsFoldable = new PartyChatCommandsFoldable();

	public TextDisplayOption render = new TextDisplayOption(CONFIG_DUNGEON_RENDER);
	public ButtonOption repositionMap = new ButtonOption(CONFIG_DUNGEON_RENDER_MAP_REPOSITION,
			this::reposition,
			CONFIG_DUNGEON_RENDER_MAP_REPOSITION_TEXT);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption renderMap = new BooleanOption(CONFIG_DUNGEON_RENDER_MAP, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption showPlayerSkulls = new BooleanOption(CONFIG_DUNGEON_SHOW_PLAYER_SKULLS, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption rotatePlayerHeads = new BooleanOption(CONFIG_DUNGEON_ROTATE_PLAYER_SKULLS, true).onlyIf(this.showPlayerSkulls);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption showPlayerNames = new BooleanOption(CONFIG_DUNGEON_SHOW_PLAYER_NAMES, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption renderOverRoomText = new BooleanOption(CONFIG_DUNGEON_RENDER_OVER_TEXT, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption keepWitherDoor = new BooleanOption(CONFIG_DUNGEON_RENDER_KEEP_WITHER_DOOR, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption showSecrets = new BooleanOption(CONFIG_DUNGEON_RENDER_SHOW_SECRETS, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption showPuzzleName = new BooleanOption(CONFIG_DUNGEON_RENDER_SHOW_PUZZLE_NAME, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption showRoomStatusAsTextColor =
			new BooleanOption(CONFIG_DUNGEON_RENDER_ROOM_STATUS_AS_COLOR, true);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption showTrapAsCleared = new BooleanOption(CONFIG_DUNGEON_RENDER_MAP_SHOW_TRAP_AS_CLEARED, false);
	@HudSetting(DungeonMapHud.class)
	public BooleanOption showMapBackground = new BooleanOption(CONFIG_DUNGEON_RENDER_MAP_BACKGROUND, true);
	@HudSetting(DungeonMapHud.class)
	public ColorOption mapBackgroundColor =
			new ColorOption(CONFIG_DUNGEON_RENDER_MAP_BACKGROUND_COLOR, Color.DARK_GRAY).withAlpha()
					.onlyIf(this.showMapBackground);


	private void reposition() {
		CookiesMod.openScreen(new HudEditScreen());
	}

	@Override
	public Row getRow() {
		return Row.BOTTOM;
	}

	@Override
	public int getColumn() {
		return 2;
	}
	public static class ClassColorFoldable extends Foldable {

		public ColorOption healer = new ColorOption(CONFIG_DUNGEON_CLASS_COLOR_HEALER, new Color(0xA933DC));
		public ColorOption mage = new ColorOption(CONFIG_DUNGEON_CLASS_COLOR_MAGE, new Color(0x6EB5FF));
		public ColorOption bers = new ColorOption(CONFIG_DUNGEON_CLASS_COLOR_BERS, new Color(0xEE9f27));
		public ColorOption arch = new ColorOption(CONFIG_DUNGEON_CLASS_COLOR_ARCH, new Color(0xFF6666));
		public ColorOption tank = new ColorOption(CONFIG_DUNGEON_CLASS_COLOR_TANK, new Color(Constants.SUCCESS_COLOR));

		@Override
		public String getName() {
			return CONFIG_DUNGEON_CLASS_COLOR;
		}
	}

	public static class SpiritLeapFoldable extends Foldable {

		public BooleanOption colorInClassColor = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_USE_CLASS_COLOR, false);
		public BooleanOption announceLeaps = new BooleanOption(CONFIG_DUNGEON_ANNOUNCE_LEAPS, true);
		public BooleanOption announceLeapCoords = new BooleanOption(CONFIG_DUNGEON_ANNOUNCE_LEAP_COORDS, false).onlyIf(announceLeaps);

		@Parent
		public TextDisplayOption vanillaUi = new TextDisplayOption(CONFIG_DUNGEON_SPIRIT_LEAP_VANILLA);
		public BooleanOption modifyNormalIfAvailable = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_MODIFY_DEFAULT_IF_AVAILABLE, true);
		public BooleanOption usePlayerHeadsInsteadOfClassItems = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_USE_HEADS_IF_AVAILABLE, false).onlyIf(modifyNormalIfAvailable);

		@Parent
		public TextDisplayOption customUi = new TextDisplayOption(CONFIG_DUNGEON_SPIRIT_LEAP_CUSTOM);
		public BooleanOption spiritLeapUi = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_ENABLE, true);
		public BooleanOption showMap = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_SHOW_MAP, true);
		public BooleanOption sortByClassName = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_SORT_BY_CLASS_NAME, false);
		public ColorOption colorOption =
				new ColorOption(CONFIG_DUNGEON_SPIRIT_LEAP_COLOR, new Color(0xFFDCD3FF, true)).withAlpha()
						.onlyIfNot(this.colorInClassColor);

		@Override
		public String getName() {
			return CONFIG_DUNGEON_SPIRIT_LEAP;
		}
	}

	public static class PuzzleFoldable extends Foldable {

		public BooleanOption creeperBeams = new BooleanOption(CONFIG_DUNGEON_PUZZLE_CREEPER_BEAMS_SOLVER, true);
		public BooleanOption higherLower = new BooleanOption(CONFIG_DUNGEON_PUZZLE_HIGHER_LOWER_SOLVER, true);
		public BooleanOption quiz = new BooleanOption(CONFIG_DUNGEON_PUZZLE_QUIZ_SOLVER, true);
		public BooleanOption threeWeirdos = new BooleanOption(CONFIG_DUNGEON_PUZZLE_THREE_WEIRDOS_SOLVER, true);
		public TextDisplayOption credit = TextDisplayOption.description(CONFIG_DUNGEON_PUZZLE_WATER_BOARD_CREDITS);
		public BooleanOption waterBoard = new BooleanOption(CONFIG_DUNGEON_PUZZLE_WATER_BOARD_SOLVER, true);

		@Override
		public String getName() {
			return CONFIG_DUNGEON_PUZZLE;
		}
	}

	public static class TerminalFoldable extends Foldable {

		public BooleanOption preventMissclicks = new BooleanOption(CONFIG_DUNGEON_TERMINAL_PREVENT_MISS_CLICKS, true);
		public BooleanOption changeAllToSameColorTerminal =
				new BooleanOption(CONFIG_DUNGEON_TERMINAL_CHANGE_ALL_TO_SAME, true);
		public BooleanOption clickInOrderTerminal = new BooleanOption(CONFIG_DUNGEON_TERMINAL_CLICK_IN_ORDER, true);
		public BooleanOption correctAllThePanesTerminal =
				new BooleanOption(CONFIG_DUNGEON_TERMINAL_CORRECT_ALL_PANES, true);
		public BooleanOption selectAllColorsTerminal =
				new BooleanOption(CONFIG_DUNGEON_TERMINAL_SELECT_ALL_COLORS, true);
		public BooleanOption startsWithTerminal = new BooleanOption(CONFIG_DUNGEON_TERMINAL_STARTS_WITH_TERMINAL,
				true);
		public BooleanOption melodyNotifier = new BooleanOption(CONFIG_DUNGEON_TERMINAL_MELODY_NOTIFIER, true);


		@Override
		public String getName() {
			return CONFIG_DUNGEON_TERMINAL;
		}
	}

	public static class PartyChatCommandsFoldable extends Foldable {
		public BooleanOption ptMe = new BooleanOption(CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_PT_ME, true);
		public BooleanOption warp = new BooleanOption(CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_WARP, true);
		public BooleanOption joinInstance = new BooleanOption(CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_JOIN_INSTANCE, true);
		public BooleanOption downTime = new BooleanOption(CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_DOWN_TIME, true);
		public BooleanOption coinFlip = new BooleanOption(CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_COIN_FLIP, true);

		public Map<String, BooleanOption> partyChatCommands = Map.of("ptme", ptMe, "warp", warp, "joininstance", joinInstance, "dt", downTime, "cf", coinFlip);

		@Override
		public String getName() {
			return CONFIG_DUNGEON_PARTY_CHAT_COMMANDS;
		}
	}
	public static class CroesusFoldable extends Foldable {

		public BooleanOption highlightUnclaimedChests = new BooleanOption(CONFIG_DUNGEON_CROESUS_HIGHLIGHT_UNCLAIMED, false);
		public BooleanOption replaceChestItemWithHighestRarityItem = new BooleanOption(CONFIG_DUNGEON_CROESUS_REPLACE_ITEM, false);

		@Override
		public String getName() {
			return CONFIG_DUNGEON_CROESUS;
		}
	}
}
