package dev.morazzer.cookies.mod.config.categories;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.config.data.HudElementPosition;
import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Row;

import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.ButtonOption;
import dev.morazzer.cookies.mod.config.system.options.ColorOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import dev.morazzer.cookies.mod.screen.DungeonMapRepositionScreen;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import java.awt.Color;

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

	public TextDisplayOption render = new TextDisplayOption(CONFIG_DUNGEON_RENDER);
	public ButtonOption repositionMap = new ButtonOption(CONFIG_DUNGEON_RENDER_MAP_REPOSITION, this::reposition, CONFIG_DUNGEON_RENDER_MAP_REPOSITION_TEXT);
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
}
