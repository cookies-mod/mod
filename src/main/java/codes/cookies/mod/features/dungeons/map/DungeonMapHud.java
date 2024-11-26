package codes.cookies.mod.features.dungeons.map;

import java.util.Optional;

import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.features.dungeons.DungeonFeatures;
import codes.cookies.mod.features.dungeons.DungeonInstance;
import codes.cookies.mod.features.dungeons.DungeonPlayer;
import codes.cookies.mod.features.dungeons.SpiritLeapOverlay;
import codes.cookies.mod.render.hud.elements.HudElement;
import codes.cookies.mod.render.hud.internal.HudEditAction;
import codes.cookies.mod.render.hud.settings.HudElementSettingBuilder;
import lombok.Getter;
import org.joml.Vector2i;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class DungeonMapHud extends HudElement {
	@Getter
	private static final DungeonMapHud instance = new DungeonMapHud();
	private DungeonInstance mockInstance;

	private DungeonMapHud() {
		super(Identifier.of("cookies", "dungeon_map"));
	}

	@Override
	public void render(DrawContext drawContext, TextRenderer textRenderer, float ticks) {
		final DungeonInstance dungeonInstance = getCurrentInstance().filter(instance -> !(instance.getPhase() == DungeonPhase.BOSS || instance.getPhase() == DungeonPhase.AFTER))
				.orElseGet(this::getMockInstance);

		if (dungeonInstance == null) {
			return;
		}

		if (!dungeonInstance.isDebugInstance()) {
			dungeonInstance.updatePlayersFromWorld();
		}
		final DungeonMapRenderer mapRenderer = dungeonInstance.getMapRenderer();
		mapRenderer.render(drawContext);
	}

	private Optional<DungeonInstance> getCurrentInstance() {
		return DungeonFeatures.getInstance().getCurrentInstance();
	}

	@Override
	public boolean shouldRender() {
		if (SpiritLeapOverlay.isOpen) {
			return false;
		}

		if (this.hudEditAction == HudEditAction.SHOW_ALL) {
			return true;
		}

		if (!DungeonConfig.getInstance().renderMap.getValue()) {
			return false;
		}

		if (this.hudEditAction == HudEditAction.ALL_ENABLED) {
			return true;
		}

		return DungeonFeatures.getInstance() != null && DungeonFeatures.getInstance().getCurrentInstance().isPresent();
	}

	@Override
	public int getWidth() {
		return 6 * DungeonMapRenderer.TOTAL_SIZE - DungeonMapRenderer.HALLWAY_SIZE;
	}

	@Override
	public int getHeight() {
		return getWidth();
	}

	@Override
	public Text getName() {
		return Text.literal("Dungeon Map").formatted(Formatting.DARK_RED);
	}

	@Override
	public void buildSettings(HudElementSettingBuilder builder) {
		addBasicSetting(builder);
		addConfigSetting(builder);
	}

	public DungeonInstance getMockInstance() {
		if (this.hudEditAction == HudEditAction.NONE) {
			return null;
		}

		if (mockInstance != null) {
			return mockInstance;
		}
		DungeonInstance instance = new DungeonInstance(DungeonType.CATACOMBS, 7, "cookies_internal_test");
		instance.load();
		instance.setPhase(DungeonPhase.CLEAR);

		final DungeonMap dungeonMap = instance.getDungeonMap();
		dungeonMap.setTopLeftPixel(new Vector2i(5, 5));
		dungeonMap.setBottomRightPixel(new Vector2i(121, 121));
		dungeonMap.setRoomsInX(6);
		dungeonMap.setRoomsInY(6);

		DungeonRoom one = new DungeonRoom(instance, RoomType.NORMAL);
		one.setCheckmark(Checkmark.OPENED);
		dungeonMap.setRoom(0, 0, one);
		dungeonMap.setRoom(0, 1, one);

		DungeonRoom start = new DungeonRoom(instance, RoomType.SPAWN);
		dungeonMap.setRoom(1, 0, start);
		start.setCheckmark(Checkmark.DONE);

		DungeonRoom two = new DungeonRoom(instance, RoomType.NORMAL);
		two.setMaxSecrets(7);
		two.setCollectedSecrets(5);
		two.setCheckmark(Checkmark.CLEARED);
		dungeonMap.setRoom(1, 1, two);
		dungeonMap.setRoom(2, 1, two);

		DungeonRoom three = new DungeonRoom(instance, RoomType.TRAP);
		three.setCheckmark(Checkmark.OPENED);
		dungeonMap.setRoom(2, 0, three);

		DungeonRoom four = new DungeonRoom(instance, RoomType.UNKNOWN);
		dungeonMap.setRoom(3, 1, four);

		DungeonRoom five = new DungeonRoom(instance, RoomType.UNKNOWN);
		dungeonMap.setRoom(0, 2, five);

		DungeonRoom six = new DungeonRoom(instance, RoomType.NORMAL);
		six.setCheckmark(Checkmark.CLEARED);
		six.setMaxSecrets(0);
		six.setCollectedSecrets(0);
		dungeonMap.setRoom(1, 2, six);

		DungeonRoom seven = new DungeonRoom(instance, RoomType.NORMAL);
		seven.setCheckmark(Checkmark.CLEARED);
		seven.setMaxSecrets(3);
		seven.setCollectedSecrets(1);
		dungeonMap.setRoom(2, 2, seven);

		DungeonRoom eight = new DungeonRoom(instance, RoomType.NORMAL);
		eight.setCheckmark(Checkmark.DONE);
		eight.setMaxSecrets(4);
		eight.setCollectedSecrets(4);
		dungeonMap.setRoom(3, 2, eight);
		dungeonMap.setRoom(4, 2, eight);
		dungeonMap.setRoom(5, 2, eight);

		DungeonRoom nine = new DungeonRoom(instance, RoomType.NORMAL);
		nine.setCheckmark(Checkmark.CLEARED);
		dungeonMap.setRoom(2, 3, nine);
		dungeonMap.setRoom(2, 4, nine);

		DungeonRoom ten = new DungeonRoom(instance, RoomType.FAIRY);
		ten.setCheckmark(Checkmark.DONE);
		dungeonMap.setRoom(3, 3, ten);

		DungeonRoom eleven = new DungeonRoom(instance, RoomType.UNKNOWN);
		dungeonMap.setRoom(4, 3, eleven);

		DungeonRoom twelve = new DungeonRoom(instance, RoomType.PUZZLE);
		twelve.setCheckmark(Checkmark.DONE);
		twelve.setPuzzleType(PuzzleType.ICE_FILL);
		dungeonMap.setRoom(2, 5, twelve);

		dungeonMap.addDoor(0, 1, false, DungeonDoor.Type.UNKNOWN);
		dungeonMap.addDoor(1, 1, true, DungeonDoor.Type.NORMAL);
		dungeonMap.addDoor(1, 0, false, DungeonDoor.Type.WITHER);
		dungeonMap.addDoor(1, 1, false, DungeonDoor.Type.WITHER);
		dungeonMap.addDoor(2, 2, true, DungeonDoor.Type.WITHER);
		dungeonMap.addDoor(2, 2, false, DungeonDoor.Type.NORMAL);
		dungeonMap.addDoor(3, 2, true, DungeonDoor.Type.WITHER);
		dungeonMap.addDoor(3, 2, false, DungeonDoor.Type.WITHER);
		dungeonMap.addDoor(4, 3, true, DungeonDoor.Type.WITHER);
		dungeonMap.addDoor(2, 4, false, DungeonDoor.Type.PUZZLE);

		dungeonMap.addDoor(2, 0, false, DungeonDoor.Type.TRAP);
		dungeonMap.addDoor(3, 1, true, DungeonDoor.Type.UNKNOWN);

		final DungeonPlayer dungeonPlayer =
				new DungeonPlayer(instance, MinecraftClient.getInstance().getSession().getUsername(), "Mage", "XII");

		dungeonPlayer.updateRotationLocal(-95);
		dungeonPlayer.updatePositionLocal(-90, -90);
		instance.getPlayers()[0] = dungeonPlayer;

		return this.mockInstance = instance;
	}
}
