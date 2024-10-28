package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.api.ScreenKeyEvents;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.accessors.InventoryScreenAccessor;
import dev.morazzer.cookies.mod.utils.compatibility.legendarytooltips.LegendaryTooltips;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

public class CraftHelperManager {

	@Setter
	@NotNull
	@Getter
	private static CraftHelperInstance active = CraftHelperInstance.EMPTY;
	private static CraftHelperLocation location;

	public static void init() {
		location = ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.getValue();
		ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.withCallback((oldValue, newValue) -> location = newValue);
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof InventoryScreenAccessor)) {
				return;
			}
			if (!SkyblockUtils.isCurrentlyInSkyblock()) {
				return;
			}
			if (!ConfigManager.getConfig().helpersConfig.craftHelper.craftHelper.getValue()) {
				return;
			}
			if (!active.equals(CraftHelperInstance.EMPTY)) {
				active.recalculate();
			}

			ScreenEvents.afterRender(screen).register(CraftHelperListener::onRender);
			ScreenMouseEvents.allowMouseClick(screen).register(CraftHelperListener::onMouseClick);
			ScreenMouseEvents.allowMouseScroll(screen).register(CraftHelperListener::onMouseScroll);
			ScreenKeyboardEvents.allowKeyPress(screen).register(CraftHelperListener::onKeyPressed);
			ScreenKeyboardEvents.allowKeyRelease(screen).register(CraftHelperListener::onKeyReleased);
			ScreenKeyEvents.getExtension(screen).cookies$allowCharTyped().register(CraftHelperListener::onCharTyped);
		});
	}

	public static void pushNewCraftHelperItem(RepositoryItem repositoryItem, int amount) {
		active = new CraftHelperInstance(repositoryItem, amount, new ArrayList<>());
		active.recalculate();
	}

	public static void remove() {
		active = CraftHelperInstance.EMPTY;
	}


	private interface CraftHelperListener {

		private static int calculateX(Screen screen) {
			return switch (CraftHelperManager.location) {
				case LEFT -> 0;
				case LEFT_INVENTORY -> InventoryScreenAccessor.getX(screen) - 1;
				case RIGHT_INVENTORY -> InventoryScreenAccessor.getX(screen) + 1;
				case RIGHT -> screen.width;
			};
		}

		private static int calculateY(Screen screen, CraftHelperInstance craftHelperInstance) {
			return InventoryScreenAccessor.getY(screen) + InventoryScreenAccessor.getBackgroundHeight(screen) / 2 - craftHelperInstance.getOffset();
		}

		private static Vector2ic getPosition(Screen screen, CraftHelperInstance craftHelperInstance) {
			int x = calculateX(screen);
			int y = calculateY(screen, craftHelperInstance);
			return CraftHelperTooltipPositioner.INSTANCE.getPosition(
					MinecraftClient.getInstance()
							.getWindow()
							.getScaledWidth(),
					MinecraftClient.getInstance().getWindow().getScaledHeight(),
					x,
					y,
					craftHelperInstance.width,
					craftHelperInstance.height);
		}

		private static Optional<CraftHelperInstance> getCurrent() {
			return Optional.of(active).filter(Predicate.not(CraftHelperInstance.EMPTY::equals));
		}

		static void onRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
			if (InventoryScreenAccessor.isDisabled(screen, InventoryScreenAccessor.Disabled.CRAFT_HELPER)) {
				return;
			}
			getCurrent().ifPresent(instance -> {
				final Vector2ic position = getPosition(screen, instance);
				int x = position.x();
				int y = position.y();

				drawContext.getMatrices().push();
				drawContext.getMatrices().translate(0, 0, -100);
				LegendaryTooltips.getInstance().beforeTooltipRender(screen, drawContext);
				instance.render(drawContext, x, y, mouseX - x, mouseY - y, tickDelta);
				LegendaryTooltips.getInstance().afterTooltipRender(screen);
				drawContext.getMatrices().pop();
			});
		}

		static boolean onMouseClick(Screen screen, double mouseX, double mouseY, int button) {
			return getCurrent().map(instance -> {
				final Vector2ic position = getPosition(screen, instance);
				int x = position.x();
				int y = position.y();
				return !instance.onMouseClicked(mouseX - x, mouseY - y, button);
			}).orElse(true);
		}

		static boolean onMouseScroll(
				Screen screen,
				double mouseX,
				double mouseY,
				double horizontalAmount,
				double verticalAmount
		) {
			return getCurrent().map(instance -> {
				final Vector2ic position = getPosition(screen, instance);
				int x = position.x();
				int y = position.y();
				return !instance.onMouseScroll(mouseX - x, mouseY - y, horizontalAmount, verticalAmount);
			}).orElse(true);
		}

		static boolean onKeyPressed(Screen screen, int key, int scancode, int modifiers) {
			return getCurrent().map(instance -> !instance.onKeyPressed(key, scancode, modifiers)).orElse(true);
		}

		static boolean onKeyReleased(Screen screen, int key, int scancode, int modifiers) {
			return getCurrent().map(instance -> !instance.onKeyReleased(key, scancode, modifiers)).orElse(true);
		}

		static boolean onCharTyped(Screen screen, char chr, int modifiers) {
			return getCurrent().map(instance -> !instance.onCharTyped(chr, modifiers)).orElse(true);
		}
	}

}
