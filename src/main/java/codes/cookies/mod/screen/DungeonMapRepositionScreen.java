package codes.cookies.mod.screen;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.config.data.HudElementPosition;

import codes.cookies.mod.features.dungeons.DungeonFeatures;
import codes.cookies.mod.features.dungeons.DungeonInstance;
import codes.cookies.mod.features.dungeons.DungeonPlayer;
import codes.cookies.mod.features.dungeons.map.Checkmark;
import codes.cookies.mod.features.dungeons.map.DungeonDoor;
import codes.cookies.mod.features.dungeons.map.DungeonMap;
import codes.cookies.mod.features.dungeons.map.DungeonMapRenderer;

import codes.cookies.mod.features.dungeons.map.DungeonPhase;

import codes.cookies.mod.features.dungeons.map.DungeonRoom;
import codes.cookies.mod.features.dungeons.map.DungeonType;

import codes.cookies.mod.features.dungeons.map.PuzzleType;
import codes.cookies.mod.features.dungeons.map.RoomType;

import codes.cookies.mod.translations.TranslationKeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

/**
 * Allows for moving and scaling of the dungeon map.
 */
public class DungeonMapRepositionScreen extends CookiesScreen {
	private final HudElementPosition position;
	public DungeonMapRenderer renderer;
	private boolean isDragging;
	private long lastSizeChange = -1;

	public DungeonMapRepositionScreen() {
		super(Text.empty());
		this.position = DungeonConfig.getInstance().hudElementPosition;
		if (DungeonFeatures.getInstance().getCurrentInstance().isPresent()) {
			final DungeonInstance currentInstance = DungeonFeatures.getInstance().getCurrentInstance().orElse(null);
			final DungeonPhase phase = currentInstance.getPhase();
			if (phase == DungeonPhase.BOSS || phase == DungeonPhase.AFTER) {
				this.mockMap();
				return;
			}
			this.renderer = currentInstance.getMapRenderer();
			return;
		}
		this.mockMap();
	}

	private void mockMap() {
		DungeonInstance instance = new DungeonInstance(DungeonType.CATACOMBS, 7, "cookies_internal_resize");
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

		DungeonRoom three = new DungeonRoom(instance, RoomType.UNKNOWN);
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

		dungeonMap.addDoor(2, 0, false, DungeonDoor.Type.UNKNOWN);
		dungeonMap.addDoor(3, 1, true, DungeonDoor.Type.UNKNOWN);

		final DungeonPlayer dungeonPlayer =
				new DungeonPlayer(instance, MinecraftClient.getInstance().getSession().getUsername(), "Mage", "XII");

		dungeonPlayer.updateRotationLocal(-95);
		dungeonPlayer.updatePositionLocal(-30 * 3, -30 * 3);
		instance.getPlayers()[0] = dungeonPlayer;

		this.renderer = instance.getMapRenderer();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer,
				Text.translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_MAP_REPLACEMENT_LINE_1),
				context.getScaledWindowWidth() / 2,
				context.getScaledWindowHeight() / 2 - this.textRenderer.fontHeight,
				0xFFAAAAAA);
		context.drawCenteredTextWithShadow(this.textRenderer,
				Text.translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_MAP_REPLACEMENT_LINE_2),
				context.getScaledWindowWidth() / 2,
				context.getScaledWindowHeight() / 2,
				0xFFAAAAAA);

		if (this.lastSizeChange + 2000 > System.currentTimeMillis()) {
			context.drawCenteredTextWithShadow(this.textRenderer,
					Text.translatable(TranslationKeys.CONFIG_DUNGEON_RENDER_MAP_REPLACEMENT_LINE_3,
							"%.2f".formatted(this.position.scale)),
					context.getScaledWindowWidth() / 2,
					context.getScaledWindowHeight() / 2 + this.textRenderer.fontHeight,
					0xFFAAAAAA);
		}

		final int size = 6 * DungeonMapRenderer.TOTAL_SIZE - DungeonMapRenderer.HALLWAY_SIZE;
		context.getMatrices().push();
		context.getMatrices()
				.translate(this.position.clampX(size) * MinecraftClient.getInstance().getWindow().getScaledWidth(),
						this.position.clampY(size) * MinecraftClient.getInstance().getWindow().getScaledHeight(),
						1000);
		context.getMatrices().scale(this.position.scale, this.position.scale, 1);
		if (!DungeonConfig.getInstance().showMapBackground.getValue()) {
			context.fill(0, 0, size, size,
                0xFF << 24 | DungeonConfig.getInstance().mapBackgroundColor.getColorValue());
		}
		this.renderer.render(context);
		context.getMatrices().pop();

	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isDragging) {
			this.position.x += (float) deltaX / MinecraftClient.getInstance().getWindow().getScaledWidth();
			this.position.y += (float) deltaY / MinecraftClient.getInstance().getWindow().getScaledHeight();
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_W) {
			this.position.scale += 0.1f;
			this.lastSizeChange = System.currentTimeMillis();
		} else if (keyCode == GLFW.GLFW_KEY_S) {
			this.position.scale -= 0.1f;
			this.lastSizeChange = System.currentTimeMillis();
		}
		if (this.position.scale < 0.1f) {
			this.position.scale = 0.1f;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		final int i = 6 * DungeonMapRenderer.TOTAL_SIZE - DungeonMapRenderer.HALLWAY_SIZE;
		if (isInBound((int) mouseX,
				(int) mouseY,
				(int) (this.position.clampX(i) * MinecraftClient.getInstance().getWindow().getScaledWidth()),
				(int) (this.position.clampY(i) * MinecraftClient.getInstance().getWindow().getScaledHeight()),
				(int) (i * this.position.scale),
				(int) (i * this.position.scale))) {
			this.isDragging = true;
			GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.isDragging = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void close() {
		CookiesMod.openConfig();
	}
}
