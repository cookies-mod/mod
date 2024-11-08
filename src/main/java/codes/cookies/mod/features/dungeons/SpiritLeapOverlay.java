package codes.cookies.mod.features.dungeons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import com.google.common.base.Predicates;
import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.features.dungeons.map.DungeonMapRenderer;
import codes.cookies.mod.screen.CookiesScreen;
import codes.cookies.mod.screen.DungeonMapRepositionScreen;
import codes.cookies.mod.utils.Result;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

/**
 * An overlay for the spirit leap to make using it more convenient.
 */
public class SpiritLeapOverlay {
	private static final Identifier DEBUG = DevUtils.createIdentifier("dungeons/spirit_skip");
	private static final Logger log = LoggerFactory.getLogger(SpiritLeapOverlay.class);
	public static boolean isOpen;
	private static DungeonFeatures features;
	private final HandledScreen<?> handledScreen;
	private final Player[] actualPlayers = new Player[4];
	private final Player[] players = new Player[4];
	private int amountOfPlayers = -1;
	private int rows = 1;
	private int columns = 1;
	private boolean enableCustomLeap = true;

	/**
	 * Modifies the screen to display the contents of the spirit leap instead of the spirit leap inventory.
	 *
	 * @param handledScreen The screen to modify.
	 */
	public SpiritLeapOverlay(HandledScreen<?> handledScreen) {
		isOpen = true;
		this.handledScreen = handledScreen;
		ScreenEvents.beforeRender(handledScreen).register(this::beforeRender);
		ScreenEvents.afterRender(handledScreen).register(this::afterRender);
		InventoryContentUpdateEvent.registerSlot(handledScreen.getScreenHandler(), this::update);
		ScreenMouseEvents.allowMouseClick(handledScreen).register(this::mouseClicked);
		ScreenMouseEvents.allowMouseRelease(handledScreen).register(this::mouseRelease);
		ScreenMouseEvents.allowMouseScroll(handledScreen).register(this::mouseScroll);
		ScreenKeyboardEvents.allowKeyPress(handledScreen).register(this::allowKey);
		ScreenKeyboardEvents.allowKeyRelease(handledScreen).register((screen, key, scancode, modifiers) -> false);
		ScreenEvents.remove(handledScreen).register(screen -> isOpen = false);
	}

	/**
	 * Blocks the rendering of the actual screen by enabling scissors and also translation all locations to the right.
	 */
	private void beforeRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float delta) {
		if (!enableCustomLeap) {
			return;
		}
		drawContext.enableScissor(0, 0, 0, 0);
		drawContext.getMatrices().push();
		drawContext.getMatrices().translate(10000, 0, 0);
	}

	/**
	 * Called after the main screen was rendered.
	 */
	private void afterRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float delta) {
		if (!enableCustomLeap) {
			return;
		}
		drawContext.disableScissor();
		drawContext.getMatrices().pop();
		drawContext.getMatrices().push();

		int startX = drawContext.getScaledWindowWidth() / 4;
		int endX = (drawContext.getScaledWindowWidth() / 4) * 3;
		int offsetY = drawContext.getScaledWindowHeight() / 8;
		int startY = drawContext.getScaledWindowHeight() / 4 - offsetY;
		int endY = (drawContext.getScaledWindowHeight() / 2) - offsetY;

		int width = endX - startX;
		int height = endY - startY;

		int individualWidth = width / this.columns;
		int individualHeight = height / this.rows;

		for (int i = 0; i < this.players.length; i++) {
			Player player = this.players[i];
			if (player == null || player.slot.getStack().isEmpty()) {
				continue;
			}
			int row = i / this.columns;
			int column = i % this.columns;

			int playerX = startX + (individualWidth * column) + 2;
			int playerY = startY + (individualHeight * row) + 1;
			int playerEndX = startX + (individualWidth * (column + 1)) - 3;
			int playerEndY = startY + (individualHeight * (row + 1)) - 3;

			int playerWidth = playerEndX - playerX;
			int playerHeight = playerEndY - playerY;

			player.location.x = playerX;
			player.location.y = playerY;
			player.location.width = playerWidth;
			player.location.height = playerHeight;

			Text name = player.slot.getStack().getName();
			int nameWidth = MinecraftClient.getInstance().textRenderer.getWidth(name);

			drawContext.getMatrices().push();
			drawContext.getMatrices().translate(playerX + playerWidth - 10, playerY + playerHeight / 2f, 1);
			float maxWidth = (playerWidth / 4f) * 2.5f;
			float scale = maxWidth / Math.max(nameWidth, 54);
			drawContext.getMatrices().scale(scale, scale, 1);

			drawContext.drawText(MinecraftClient.getInstance().textRenderer, name, -nameWidth, -4, -1, true);

			drawContext.getMatrices().pop();

			drawContext.fill(playerX, playerY, playerEndX, playerEndY, getColor(player));
			int size = (playerHeight / 16) * 14;
			int skullOffsetY = (playerHeight - size) / 2;
			if (player.dungeonPlayer == null || player.dungeonPlayer.getPlayer() == null) {
				drawContext.fill(
						playerX + skullOffsetY,
						playerY + skullOffsetY,
						playerX + skullOffsetY + size,
						playerY + skullOffsetY + size,
						-1);
			} else {
				drawContext.getMatrices().push();
				drawContext.getMatrices().translate(playerX + skullOffsetY + size, playerY + skullOffsetY + size, 1);
				drawContext.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
				PlayerSkinDrawer.draw(
						drawContext,
						player.dungeonPlayer.getPlayer().getSkinTextures().texture(),
						0,
						0,
						size,
						true,
						true, -1);
				drawContext.getMatrices().pop();
			}
		}

		if (!DungeonConfig.getInstance().spiritLeapFoldable.showMap.getValue()) {
			return;
		}
		@Nullable final DungeonMapRenderer mapRenderer;
		if (features.getCurrentInstance().isEmpty()) {
			if (DevUtils.isEnabled(DEBUG)) {
				mapRenderer = new DungeonMapRepositionScreen().renderer;
			} else {
				return;
			}
		} else {
			mapRenderer = features.getCurrentInstance().map(DungeonInstance::getMapRenderer).orElse(null);
		}
		if (mapRenderer == null) {
			return;
		}

		drawContext.getMatrices().push();
		final int size = 6 * DungeonMapRenderer.TOTAL_SIZE - DungeonMapRenderer.HALLWAY_SIZE;
		final float halfSize = size / 2f;
		drawContext.getMatrices().translate(
				(drawContext.getScaledWindowWidth() / 2f) - halfSize,
				drawContext.getScaledWindowHeight() / 2f * 0.8f,
				1);
		mapRenderer.render(drawContext);
		drawContext.getMatrices().pop();
	}

	/**
	 * Updates the cached data to reflect the inventory.
	 *
	 * @param slot The slot that is currently being updated.
	 */
	private void update(Slot slot) {
		int slotId = slot.id;
		if (slotId == 0) {
			this.amountOfPlayers = -1;
			Arrays.fill(this.actualPlayers, null);
		}
		if (slotId < 11 || slotId > 15) {
			return;
		}
		if (slot.getStack().getItem() != Items.PLAYER_HEAD) {
			return;
		}

		if (this.amountOfPlayers == -1 || (this.amountOfPlayers == 2 && slotId == 13)) {
			this.amountOfPlayers = switch (slotId) {
				case 11 -> 4;
				case 12 -> 2;
				case 13 -> this.amountOfPlayers == 2 ? 3 : 1;
				default -> -1;
			};
			if (this.amountOfPlayers > 1) {
				this.rows = 2;
			}
			if (this.amountOfPlayers > 2) {
				this.columns = 2;
			}
		}
		DungeonPlayer dungeonPlayer = features.getCurrentInstance()
				.map(instance -> instance.getPlayer(slot.getStack().getName().getString()))
				.orElseGet(() -> new DungeonPlayer(null, "meowora", "mage", "test"));
		int noOffset = slotId - 11;
		int index = switch (this.amountOfPlayers) {
			case 1 -> noOffset == 2 ? 0 : -1;
			case 2 -> noOffset == 1 ? 0 : noOffset == 3 ? 1 : -1;
			case 3 -> noOffset - 1;
			case 4 -> noOffset == 2 ? -1 : noOffset > 2 ? noOffset - 1 : noOffset;
			default -> -1;
		};
		if (index < 0 || index >= this.actualPlayers.length) {
			return;
		}
		this.actualPlayers[index] = new Player(slot, dungeonPlayer, new Location());
		if (index == amountOfPlayers - 1) {
			this.setPlayers();
		}
	}

	/**
	 * Handles the mouse interaction and blocks the interaction with the original inventory.
	 */
	private boolean mouseClicked(Screen screen, double mouseX, double mouseY, int button) {
		if (!enableCustomLeap) {
			return true;
		}
		for (Player player : this.actualPlayers) {
			if (player == null) {
				continue;
			}
			if (CookiesScreen.isInBound(
					(int) mouseX,
					(int) mouseY,
					player.location.x,
					player.location.y,
					player.location.width,
					player.location.height)) {
				clickedPlayer(player.slot);
				break;
			}
		}


		return false;
	}

	/**
	 * Blocks all release interaction with the inventory.
	 */
	private boolean mouseRelease(Screen screen, double mouseX, double mouseY, int button) {
		return !enableCustomLeap;
	}

	/**
	 * Blocks all scroll interaction with the inventory.
	 */
	private boolean mouseScroll(Screen screen, double v, double v1, double v2, double v3) {
		return !enableCustomLeap;
	}

	/**
	 * Blocks all key interaction with the inventory.
	 */
	private boolean allowKey(Screen screen, int key, int scancode, int modifiers) {
		if (!enableCustomLeap) {
			return true;
		}
		if (this.handledScreen.shouldCloseOnEsc() && key == GLFW.GLFW_KEY_ESCAPE) {
			this.handledScreen.close();
		}
		if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(key, scancode)) {
			this.handledScreen.close();
		}
		return false;
	}

	private int getColor(Player player) {
		if (!DungeonConfig.getInstance().spiritLeapFoldable.colorInClassColor.getValue()) {
			return DungeonConfig.getInstance().spiritLeapFoldable.colorOption.getColorValue();
		}

		return Optional.ofNullable(player)
				.map(Player::dungeonPlayer)
				.map(DungeonPlayer::getColor)
				.orElseGet(OptionalInt::empty)
				.orElseGet(DungeonConfig.getInstance().spiritLeapFoldable.colorOption::getColorValue);
	}

	private void setPlayers() {
		if (modifyNormalIfAvailable()) {
			final List<DungeonPlayer> dungeonPlayers = new ArrayList<>(features.getCurrentInstance()
					.map(DungeonInstance::getPlayers)
					.map(Arrays::asList)
					.orElseGet(Collections::emptyList));
			dungeonPlayers.removeIf(Objects::isNull);
			final long count = dungeonPlayers.stream().map(DungeonPlayer::getDungeonClass).distinct().count();
			if (count == 5) {
				List<String> classes = List.of("Tank", "Mage", "Healer", "Berserk", "Archer");
				int slotId = 11;
				boolean failed = false;
				List<Runnable> runnables = new ArrayList<>();
				for (String currentClass : classes) {
					final Result<Runnable, String> result =
							setClass(currentClass, dungeonPlayers, actualPlayers, slotId++);
					if (result.isError()) {
						failed = true;
						result.getError().ifPresent(CookiesUtils::sendFailedMessage);
						break;
					}
					result.getResult().ifPresent(runnables::add);
				}
				if (!failed) {
					enableCustomLeap = false;
					isOpen = false;
					try {
						runnables.forEach(Runnable::run);
					} catch (Exception e) {
						CookiesUtils.sendFailedMessage("An error occurred while modifying the leap ui!");
						log.error("Error while modifying leap ui", e);
					}
					return;
				}
			}
		}
		if (!DungeonConfig.getInstance().spiritLeapFoldable.spiritLeapUi.getValue()) {
			enableCustomLeap = false;
			return;
		}
		if (DungeonConfig.getInstance().spiritLeapFoldable.sortByClassName.getValue()) {
			System.arraycopy(this.actualPlayers, 0, this.players, 0, this.actualPlayers.length);
			return;
		}
		enableCustomLeap = true;
		final List<Player> players = Arrays.stream(this.actualPlayers)
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(player -> Optional.ofNullable(player.dungeonPlayer())
						.map(DungeonPlayer::getDungeonClass)
						.orElse(""), String::compareToIgnoreCase))
				.toList();
		for (int i = 0; i < players.size(); i++) {
			final Player player = players.get(i);
			this.players[i] = player;
		}
	}

	public void clickedPlayer(Slot slot) {
		MinecraftClient.getInstance().interactionManager.clickSlot(
				this.handledScreen.getScreenHandler().syncId,
				slot.id,
				0,
				SlotActionType.PICKUP,
				MinecraftClient.getInstance().player);
	}

	private boolean modifyNormalIfAvailable() {
		return DungeonConfig.getInstance().spiritLeapFoldable.modifyNormalIfAvailable.getValue() &&
			   amountOfPlayers == 4;
	}

	private Result<Runnable, String> setClass(
			String name, List<DungeonPlayer> players, Player[] leapPlayers, int slotId) {
		final Slot slot = handledScreen.getScreenHandler().getSlot(slotId);
		final DungeonPlayer dungeonPlayer = players.stream()
				.filter(Objects::nonNull)
				.filter(player -> player.getDungeonClass().equals(name))
				.findFirst()
				.orElse(null);
		Player leapPlayer = Arrays.stream(leapPlayers)
				.filter(Objects::nonNull)
				.filter(player -> player.dungeonPlayer == dungeonPlayer)
				.findFirst()
				.orElse(null);
		boolean isSelf = leapPlayer == null;

		if (dungeonPlayer == null) {
			return Result.error("No player found for class %s".formatted(name));
		}

		return Result.success(() -> {
			final Result<ItemBuilder, String> item =
					dungeonPlayer.getItem(DungeonConfig.getInstance().spiritLeapFoldable.usePlayerHeadsInsteadOfClassItems.getValue());
			final ItemBuilder itemBuilder =
					item.getResult()
							.orElseGet(() -> new ItemBuilder(Items.RED_DYE))
							.setName("Leap to " + name)
							.setLore(TextUtils.literal("Left-click to leap to the " + name, Formatting.YELLOW),
									Text.empty(),
									TextUtils.literal(name + ": " + dungeonPlayer.getName(), Formatting.GRAY))
							.setGlint(isSelf)
							.setClickRunnable(() -> {
								if (isSelf) {
									handledScreen.close();
									CookiesUtils.sendFailedMessage("Can't leap to yourself!");
								} else {
									clickedPlayer(leapPlayer.slot);
								}
							});

			if (DevUtils.isEnabled(DEBUG)) {
				itemBuilder.appendLore(Text.literal("Leap player: " + Optional.ofNullable(leapPlayer)
						.map(Player::dungeonPlayer)
						.map(DungeonPlayer::getName)
						.orElse("<empty>")), Text.literal("Dungeon Player: " + dungeonPlayer.getName()));
			}

			if (DungeonConfig.getInstance().spiritLeapFoldable.colorInClassColor.getValue()) {
				itemBuilder.set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR, dungeonPlayer.getColor().orElse(0));
			}

			if (item.isError()) {
				itemBuilder.appendLore(
						Text.empty(),
						TextUtils.literal(item.getError().orElse("An error occurred while creating this item!")));
			}

			slot.getStack().set(CookiesDataComponentTypes.OVERRIDE_ITEM, itemBuilder.build());
		});
	}

	/**
	 * Initializes the feature.
	 *
	 * @param features The dungeon features.
	 */
	public static void init(DungeonFeatures features) {
		SpiritLeapOverlay.features = features;
		InventoryEvents.beforeInit(
				"Spirit Leap",
				Predicates.<HandledScreen<?>>alwaysTrue()
						.and(o -> DungeonFeatures.getInstance().getCurrentInstance().isPresent())
						.or(o -> DevUtils.isEnabled(DEBUG)),
				SpiritLeapOverlay::open);
	}

	/**
	 * Handles the creation of the spirit leap ui.
	 *
	 * @param handledScreen The screen to modify.
	 */
	private static void open(HandledScreen<?> handledScreen) {
		if (!DungeonConfig.getInstance().spiritLeapFoldable.spiritLeapUi.getValue() &&
			!DungeonConfig.getInstance().spiritLeapFoldable.modifyNormalIfAvailable.getValue()) {
			return;
		}
		new SpiritLeapOverlay(handledScreen);
	}

	/**
	 * A player that can be leaped to.
	 *
	 * @param slot          The slot the item is in.
	 * @param dungeonPlayer The dungeon player corresponding with the player.
	 * @param location      The location information of the player.
	 */
	private record Player(Slot slot, @Nullable DungeonPlayer dungeonPlayer, @NotNull Location location) {

	}

	/**
	 * The location information about a player's widget.
	 */
	private static class Location {
		int x, y, width, height;
	}

}
