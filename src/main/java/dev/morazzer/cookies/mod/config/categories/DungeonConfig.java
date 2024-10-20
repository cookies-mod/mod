package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.utils.cookies.Constants;

import java.awt.Color;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.config.data.HudElementPosition;
import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Foldable;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.ButtonOption;
import dev.morazzer.cookies.mod.config.system.options.ColorOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import dev.morazzer.cookies.mod.screen.DungeonMapRepositionScreen;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;

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

	public TextDisplayOption render = new TextDisplayOption(CONFIG_DUNGEON_RENDER);
	public ButtonOption repositionMap = new ButtonOption(CONFIG_DUNGEON_RENDER_MAP_REPOSITION,
			this::reposition,
			CONFIG_DUNGEON_RENDER_MAP_REPOSITION_TEXT);
	public HudElementPosition hudElementPosition = new HudElementPosition(0, 0, 1);
	public BooleanOption renderMap = new BooleanOption(CONFIG_DUNGEON_RENDER_MAP, true);
	public BooleanOption showPlayerSkulls = new BooleanOption(CONFIG_DUNGEON_SHOW_PLAYER_SKULLS, true);
	public BooleanOption showPlayerNames = new BooleanOption(CONFIG_DUNGEON_SHOW_PLAYER_NAMES, true);
	public BooleanOption renderOverRoomText = new BooleanOption(CONFIG_DUNGEON_RENDER_OVER_TEXT, true);
	public BooleanOption keepWitherDoor = new BooleanOption(CONFIG_DUNGEON_RENDER_KEEP_WITHER_DOOR, true);
	public BooleanOption showSecrets = new BooleanOption(CONFIG_DUNGEON_RENDER_SHOW_SECRETS, true);
	public BooleanOption showPuzzleName = new BooleanOption(CONFIG_DUNGEON_RENDER_SHOW_PUZZLE_NAME, true);
	public BooleanOption showRoomStatusAsTextColor =
			new BooleanOption(CONFIG_DUNGEON_RENDER_ROOM_STATUS_AS_COLOR, true);
	public BooleanOption showTrapAsCleared = new BooleanOption(CONFIG_DUNGEON_RENDER_MAP_SHOW_TRAP_AS_CLEARED, false);
	public BooleanOption showMapBackground = new BooleanOption(CONFIG_DUNGEON_RENDER_MAP_BACKGROUND, true);
	public ColorOption mapBackgroundColor =
			new ColorOption(CONFIG_DUNGEON_RENDER_MAP_BACKGROUND_COLOR, Color.DARK_GRAY).withAlpha()
					.onlyIf(this.showMapBackground);


	private void reposition() {
		CookiesMod.openScreen(new DungeonMapRepositionScreen());
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

		public BooleanOption spiritLeapUi = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_ENABLE, true);
		public BooleanOption showMap = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_SHOW_MAP, true);
		public BooleanOption sortByClassName = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_SORT_BY_CLASS_NAME, false);
		public BooleanOption colorInClassColor = new BooleanOption(CONFIG_DUNGEON_SPIRIT_LEAP_USE_CLASS_COLOR, false);
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
		public TextDisplayOption credit = new TextDisplayOption(CONFIG_DUNGEON_PUZZLE_WATER_BOARD_CREDITS,
				CONFIG_DUNGEON_PUZZLE_WATER_BOARD_CREDITS_LORE);
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

		@Override
		public String getName() {
			return CONFIG_DUNGEON_TERMINAL;
		}
	}

}
