package dev.morazzer.cookies.mod.features.dungeons.map;

import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.features.dungeons.DungeonFeatures;
import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;
import dev.morazzer.cookies.mod.features.dungeons.DungeonPlayer;
import dev.morazzer.cookies.mod.utils.RenderUtils;
import dev.morazzer.cookies.mod.utils.cookies.Constants;

import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/**
 * Used to visually represent the data of {@link DungeonMap} and {@link DungeonInstance} on the screen.
 */
public class DungeonMapRenderer {

	public static final int HALLWAY_SIZE = 4;
	private static final int ROOM_SIZE = 20;
	public static final int TOTAL_SIZE = ROOM_SIZE + HALLWAY_SIZE;
	private static final int DOOR_SIZE = 6;
	private final DungeonInstance dungeonInstance;
	private final DungeonMap dungeonMap;

	public DungeonMapRenderer(DungeonInstance dungeonInstance) {
		this.dungeonInstance = dungeonInstance;
		this.dungeonMap = dungeonInstance.getDungeonMap();
	}

	/**
	 * Render the dungeon map at {0,0}, to reposition use the matrix stack.
	 *
	 * @param drawContext The draw context
	 */
	public void render(DrawContext drawContext) {
		if (!this.dungeonInstance.isDebugInstance() &&
			this.dungeonInstance != DungeonFeatures.getInstance().getCurrentInstance().orElse(null)) {
			return;
		}
		if (!DungeonConfig.getInstance().renderMap.getValue()) {
			return;
		}
		if (this.dungeonMap.getTopLeftPixel() == null) {
			return;
		}
		if (this.dungeonInstance.getPhase() == DungeonPhase.BOSS ||
			this.dungeonInstance.getPhase() == DungeonPhase.AFTER) {
			return;
		}
		if (DungeonConfig.getInstance().showMapBackground.getValue()) {
			drawContext.fill(0,
					0,
					6 * TOTAL_SIZE - HALLWAY_SIZE,
					6 * TOTAL_SIZE - HALLWAY_SIZE,
					DungeonConfig.getInstance().mapBackgroundColor.getColorValue());
		}
		drawContext.getMatrices().push();
		final int max = Math.max(this.dungeonMap.getRoomsInY(), this.dungeonMap.getRoomsInX());
		drawContext.getMatrices().scale(6f / max, 6f / max, 1);
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {
				final DungeonRoom roomAt = this.dungeonMap.getRoomAt(x, y);
				if (roomAt == null) {
					continue;
				}

				this.renderRoom(drawContext, x, y, roomAt);
			}
		}
		for (DungeonDoor door : this.dungeonMap.getDoors()) {
			this.renderDoor(drawContext, door);
		}
		drawContext.getMatrices().pop();
		for (DungeonPlayer player : this.dungeonInstance.getPlayers()) {
			this.renderPlayer(drawContext, player);
		}
	}

	/**
	 * Render a dungeon room onto the map.
	 *
	 * @param drawContext The draw context.
	 * @param x           The x position of the room.
	 * @param y           The y position of the room.
	 * @param roomAt      The room to render.
	 */
	private void renderRoom(
			DrawContext drawContext, int x, int y, DungeonRoom roomAt) {

		int color = 0xFF << 24 | switch (roomAt.getRoomType()) {
			case NORMAL -> MapColor.DIRT_BROWN.color;
			case SPAWN -> MapColor.DARK_GREEN.color;
			case PUZZLE -> MapColor.PURPLE.color;
			case FAIRY -> MapColor.PINK.color;
			case BLOOD -> MapColor.RED.color;
			case TRAP -> MapColor.ORANGE.color;
			case MINIBOSS -> MapColor.YELLOW.color;
			case UNKNOWN -> MapColor.GRAY.color;
		};

		int locationX = x * TOTAL_SIZE;
		int locationY = y * TOTAL_SIZE;


		boolean isConnectedUp = roomAt.isAt(x, y - 1);
		boolean isConnectedLeft = roomAt.isAt(x - 1, y);
		boolean isConnectedLeftTop = roomAt.isAt(x - 1, y - 1);
		boolean isConnectedBottomLeft = roomAt.isAt(x - 1, y + 1);

		if (isConnectedLeft) {
			drawContext.fill(locationX - HALLWAY_SIZE, locationY, locationX + ROOM_SIZE, locationY + ROOM_SIZE, color);
		}
		if (isConnectedUp) {
			drawContext.fill(locationX, locationY - HALLWAY_SIZE, locationX + ROOM_SIZE, locationY + ROOM_SIZE, color);
		}
		if (isConnectedLeft && isConnectedUp && isConnectedLeftTop) {
			drawContext.fill(locationX - HALLWAY_SIZE, locationY - HALLWAY_SIZE, locationX, locationY, color);
		}
		if (!isConnectedUp && !isConnectedLeft) {
			drawContext.fill(locationX, locationY, locationX + ROOM_SIZE, locationY + ROOM_SIZE, color);
			if (!isConnectedBottomLeft) {
				if ((roomAt.canHaveSecrets() || roomAt.getRoomType() == RoomType.PUZZLE)) {
					this.drawRoomText(drawContext, roomAt, locationX, locationY);
				} else {
					this.drawRoomCheckmark(drawContext, roomAt.getCheckmark(), locationX, locationY);
				}
			}
		}
	}

	/**
	 * Render a dungeon door onto the map.
	 *
	 * @param drawContext The draw context.
	 * @param door        The dungeon door.
	 */
	private void renderDoor(DrawContext drawContext, DungeonDoor door) {
		int roomX = door.x();
		int roomY = door.y();
		boolean isLeft = door.left();
		int color = 0xFF << 24 | switch (door.type()) {
			case BLOOD -> MapColor.RED.color;
			case WITHER -> MapColor.BLACK.color;
			case NORMAL -> MapColor.DIRT_BROWN.color;
			case UNKNOWN -> MapColor.GRAY.color;
			case TRAP -> MapColor.ORANGE.color;
			case MINIBOSS -> MapColor.YELLOW.color;
			case PUZZLE -> MapColor.PURPLE.color;
			case FAIRY -> MapColor.PINK.color;
			case null -> MapColor.BLUE.color;
		};
		int locationX, locationY, sizeX, sizeY;

		if (isLeft) {
			locationX = roomX * TOTAL_SIZE - HALLWAY_SIZE;
			locationY = roomY * TOTAL_SIZE + (TOTAL_SIZE / 2) - DOOR_SIZE + 1;
			sizeX = HALLWAY_SIZE;
			sizeY = DOOR_SIZE;
		} else {
			locationX = roomX * TOTAL_SIZE + (TOTAL_SIZE / 2) - DOOR_SIZE + 1;
			locationY = roomY * TOTAL_SIZE + TOTAL_SIZE - HALLWAY_SIZE;
			sizeX = DOOR_SIZE;
			sizeY = HALLWAY_SIZE;
		}

		drawContext.fill(locationX, locationY, locationX + sizeX, locationY + sizeY, color);
	}

	/**
	 * Renders the player onto the map.
	 *
	 * @param drawContext The draw context.
	 * @param player      The player to render.
	 */
	private void renderPlayer(DrawContext drawContext, DungeonPlayer player) {
		if (player == null || this.dungeonInstance.getDungeonMap().getTopLeftPixel() == null || player.isSkip()) {
			return;
		}
		player.tick();
		if (player.getPlayer() == null) {
			return;
		}
		final int max = Math.max(this.dungeonMap.getRoomsInY(), this.dungeonMap.getRoomsInX());
		int x = (int) (MathHelper.clampedMap(player.getInterpolatedX(),
				this.dungeonMap.getTopLeftPixel().x,
				this.dungeonMap.getBottomRightPixel().x,
				0,
				(6 * TOTAL_SIZE) * (((float) this.dungeonMap.getRoomsInX()) / max))) - 2;
		int y = (int) (MathHelper.clampedMap(player.getInterpolatedY(),
				this.dungeonMap.getTopLeftPixel().y,
				this.dungeonMap.getBottomRightPixel().y,
				0,
				(6 * TOTAL_SIZE) * (((float) this.dungeonMap.getRoomsInY()) / max))) - 2;

		drawContext.getMatrices().push();
		drawContext.getMatrices().translate(x, y, 0);
		if (DungeonConfig.getInstance().renderOverRoomText.getValue()) {
			drawContext.getMatrices().translate(0, 0, 1000);
		}
		drawContext.getMatrices().push();
		if (DungeonConfig.getInstance().showPlayerSkulls.getValue() && player.getPlayer() != null) {
			drawContext.getMatrices()
					.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) (player.getRotation().getValue())));
			this.drawPlayerHead(drawContext, player);
		} else {
			drawContext.getMatrices()
					.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) (player.getRotation().getValue()) - 180));
			this.drawPlayerAsArrow(drawContext, player);
		}
		drawContext.getMatrices().pop();
		if (DungeonConfig.getInstance().showPlayerNames.getValue()) {
			RenderUtils.renderTextCenteredScaled(drawContext,
					Text.literal(player.getName()),
					0.5f,
					0,
					6,
					player.isUsingMod() ? Constants.SUCCESS_COLOR : Constants.FAIL_COLOR);
		}
		drawContext.getMatrices().pop();
	}

	/**
	 * Draws the text of the room.
	 *
	 * @param drawContext The draw context.
	 * @param roomAt      The room.
	 * @param locationX   The location of the room.
	 * @param locationY   The location of the room.
	 */
	private void drawRoomText(DrawContext drawContext, DungeonRoom roomAt, int locationX, int locationY) {
		drawContext.getMatrices().push();
		drawContext.getMatrices().translate(0, 0, 100);
		if (roomAt.getRoomType() == RoomType.PUZZLE) {
			if (!DungeonConfig.getInstance().showPuzzleName.getValue()) {
				drawContext.getMatrices().pop();
				this.drawRoomCheckmark(drawContext, roomAt.getCheckmark(), locationX, locationY);
				return;
			}

			final String name;
			if (roomAt.getPuzzleType() == null) {
				name = "???";
			} else {
				name = roomAt.getPuzzleType().getDisplayName();
			}
			final String[] lines = name.split(" ");
			final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			int totalSize = lines.length * textRenderer.fontHeight;
			int offsetY = totalSize / 2;
			float scale = 1f;
			if (totalSize > ROOM_SIZE - 2) {
				scale = (ROOM_SIZE - 2) / (float) totalSize;
			}
			for (String string : lines) {
				if (textRenderer.getWidth(string) > ROOM_SIZE - 2) {
					scale = Math.min(scale, (ROOM_SIZE - 2) / (float) textRenderer.getWidth(string));
				}
			}
			if (scale < 0.60f) {
				scale = 0.5f;
			}
			for (int i = 0; i < lines.length; i++) {
				drawContext.getMatrices().push();
				drawContext.getMatrices().translate((locationX + (float) ROOM_SIZE / 2),
						locationY + (((float) ROOM_SIZE / 2)) - offsetY * scale + (i * textRenderer.fontHeight) * scale,
						0);
				if (scale != 1) {
					drawContext.getMatrices().scale(scale, scale, 1);
				}
				drawContext.drawCenteredTextWithShadow(textRenderer, lines[i], 0, 0, roomAt.getRoomTextColor());
				drawContext.getMatrices().pop();
			}

			drawContext.getMatrices().pop();
			return;
		}

		final String secretString = roomAt.getSecretString();
		if (secretString == null || !DungeonConfig.getInstance().showSecrets.getValue()) {
			drawContext.getMatrices().pop();
			this.drawRoomCheckmark(drawContext, roomAt.getCheckmark(), locationX, locationY);
		} else {
			this.renderSecretString(drawContext, secretString, locationX, locationY, roomAt.getRoomTextColor());
			drawContext.getMatrices().pop();
		}
	}

	/**
	 * Renders the room checkmark onto the map.
	 *
	 * @param drawContext The draw context.
	 * @param checkmark   The checkmark of the room.
	 * @param locationX   The location x to render at.
	 * @param locationY   The location y to render at.
	 */
	private void drawRoomCheckmark(DrawContext drawContext, Checkmark checkmark, int locationX, int locationY) {
		if (checkmark == null || checkmark.getIdentifier() == null) {
			return;
		}
		final Identifier identifier = checkmark.getIdentifier();
		drawContext.drawTexture(
				RenderLayer::getGuiTextured,
				identifier,
				locationX,
				locationY,
				0,
				0,
				ROOM_SIZE,
				ROOM_SIZE,
				ROOM_SIZE,
				ROOM_SIZE);
	}

	/**
	 * Draws a player head.
	 *
	 * @param drawContext The draw context.
	 * @param player      The player to draw the head of.
	 */
	private void drawPlayerHead(DrawContext drawContext, DungeonPlayer player) {
		drawContext.getMatrices().translate(-4, -4, 0);
		PlayerSkinDrawer.draw(drawContext, player.getPlayer().getSkinTextures().texture(), 0, 0, 8, true, true, -1);
	}

	/**
	 * Draws an arrow.
	 *
	 * @param drawContext The draw context.
	 * @param player      The player the arrow belongs to.
	 */
	private void drawPlayerAsArrow(DrawContext drawContext, DungeonPlayer player) {
		Identifier texture;
		if (player.getPlayer() instanceof ClientPlayerEntity) {
			texture = Identifier.ofVanilla("textures/map/decorations/frame.png");
		} else {
			texture = Identifier.ofVanilla("textures/map/decorations/player.png");
		}
		drawContext.drawTexture(RenderLayer::getGuiTextured, texture, -4, -4, 8, 8, 0, 0, 8, 8, 8, 8);
	}

	private void renderSecretString(DrawContext drawContext, String secretString, int x, int y, int color) {
		final int offset;
		if (secretString.length() > 3) {
			offset = (secretString.length() - 3) * 2;
		} else {
			offset = 0;
		}
		drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer,
				secretString,
				x + ROOM_SIZE / 2 + offset,
				y + ROOM_SIZE / 2 - 4,
				color);
	}
}
