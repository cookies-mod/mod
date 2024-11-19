package codes.cookies.mod.render.hud;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.render.hud.elements.HudElement;
import codes.cookies.mod.render.hud.internal.Action;
import codes.cookies.mod.render.hud.internal.BoundingBox;
import codes.cookies.mod.render.hud.internal.HudEditAction;
import codes.cookies.mod.render.hud.internal.HudElementSettings;
import codes.cookies.mod.render.hud.settings.HudElementSetting;
import codes.cookies.mod.render.hud.settings.HudElementSettingBuilder;
import codes.cookies.mod.utils.RenderUtils;
import codes.cookies.mod.utils.minecraft.SoundUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HudEditScreen extends Screen {
	private HudElement currentlyHovered;
	private Action action = Action.NONE;
	private Action afterMoveAction = Action.NONE;
	private List<HudElementSetting> currentSettings = Collections.emptyList();
	private int sidebarSize = 0;
	private boolean isOnLeft;
	private HudEditAction editAction = HudEditAction.DEFAULT;
	private long lastEditActionChange = -1;
	private long lastSizeChange = -1;

	public HudEditScreen() {
		super(Text.empty());
		getElements().forEach(hudElement -> hudElement.setHudEditAction(HudEditAction.DEFAULT));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		applyBlur();
		if (action == Action.NONE) {
			HudElement element = this.currentlyHovered;
			this.currentlyHovered = getElementUnder(mouseX, mouseY).orElse(null);
			if (element != this.currentlyHovered) {
				this.resetSettings();
				this.lastSizeChange = -1;
			}
		}
		StringBuilder stringBuilder = new StringBuilder();
		if (action == Action.NONE) {
			stringBuilder.append("§6Click §7element to §bmove").append("\n");
			stringBuilder.append("§6Click + scroll §7or use §a+ §7and §c- §7to change §bscale").append("\n");
			stringBuilder.append("§6Ctrl + Click §7element for element §bsettings").append("\n");
			stringBuilder.append("§7Press §6H §7to show all hud elements");
			if (this.lastEditActionChange + 2000 > System.currentTimeMillis()) {
				stringBuilder.append(" (§a%s§7)".formatted(StringUtils.capitalize(this.editAction.name()
						.replace("_", " ")
						.toLowerCase())));
			}
			stringBuilder.append("\n");
		}
		if (this.currentlyHovered != null && this.lastSizeChange + 2000 > System.currentTimeMillis()) {
			stringBuilder.append("§7Scale: §a%.2f".formatted(this.currentlyHovered.getScale())).append("\n");
		}
		final String string = stringBuilder.toString();

		int index = 0;
		final int fontHeight = this.textRenderer.fontHeight;
		for (OrderedText wrapLine : textRenderer.wrapLines(Text.literal(string), width)) {
			context.drawCenteredTextWithShadow(
					this.textRenderer,
					wrapLine,
					context.getScaledWindowWidth() / 2,
					(context.getScaledWindowHeight() / 2 - fontHeight) + fontHeight * index++,
					0xFFAAAAAA);
		}

		context.cm$withMatrix(stack -> {
			stack.translate(0, 0, 100);
			this.getVisibleElements().forEach(hudElement -> this.renderElement(context, hudElement, delta));
		});

		if (this.currentlyHovered != null && this.action == Action.NONE) {
			context.drawTooltip(getTextRenderer(), this.currentlyHovered.getName(), mouseX, mouseY);
		}

		if (action == Action.EDIT && this.currentlyHovered != null) {
			applyBlur();
			context.drawCenteredTextWithShadow(
					this.textRenderer,
					Text.literal("Editing ")
							.append(this.currentlyHovered.getName())
							.append(" :3")
							.formatted(Formatting.GRAY),
					context.getScaledWindowWidth() / 2,
					(context.getScaledWindowHeight() / 2),
					0xFFAAAAAA);
			if (this.isOnLeft) {
				RenderUtils.renderBackgroundBox(context, 0, 0, sidebarSize, height, -1);
			} else {
				RenderUtils.renderBackgroundBox(context, width - sidebarSize, 0, sidebarSize, height, -1);
			}
			for (Drawable drawable : this.drawables) {
				drawable.render(context, mouseX, mouseY, delta);
			}
			context.cm$withMatrix(stack -> {
				stack.translate(0, 0, 1000);
				this.renderElement(context, currentlyHovered, delta);
			});
		}
	}

	@Override
	public void close() {
		super.close();
		this.getElements().forEach(hudElement -> hudElement.setHudEditAction(HudEditAction.NONE));
		ConfigManager.saveConfig(true, "hud-edit-screen");
	}

	private void renderElement(DrawContext context, HudElement hudElement, float delta) {
		int elementX = hudElement.getX();
		int elementY = hudElement.getY();

		context.cm$withMatrix(stack -> {
			stack.translate(elementX, elementY, 0);
			stack.scale(hudElement.getScale(), hudElement.getScale(), 0);
			if (currentlyHovered == hudElement) {
				hudElement.getNormalizedBoundingBox().fill(context, 0xFFABABAB);
			} else {
				hudElement.getNormalizedBoundingBox().fill(context, 0xAB000000);
			}
			hudElement.renderChecks(
					context,
					getTextRenderer(),
					delta);
		});
		context.fill(0,0,0,0,0);
		// hacky solution but for some reason it fixes the text rendering over the applied blur from the hud edit screen???
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (chr == '-' && action == Action.MOVE && currentlyHovered != null) {
			this.currentlyHovered.getPosition().applyScale(-0.1f);
			this.lastSizeChange = System.currentTimeMillis();
			return true;
		} else if (chr == '+' && action == Action.MOVE && currentlyHovered != null) {
			this.currentlyHovered.getPosition().applyScale(0.1f);
			this.lastSizeChange = System.currentTimeMillis();
			return true;
		}
		return super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_H) {
			this.lastEditActionChange = System.currentTimeMillis();
			this.editAction = HudEditAction.values()[this.editAction.getNext()];
			this.getElements().forEach(hudElement -> hudElement.setHudEditAction(this.editAction));
			SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
			return true;
		}
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.action == Action.EDIT) {
			this.action = Action.NONE;
			this.afterMoveAction = Action.NONE;
			this.resetSettings();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (action == Action.MOVE && currentlyHovered != null) {
			final HudElementSettings position = currentlyHovered.getPosition();
			final float relativeDeltaX = (float) (deltaX / width);
			final float relativeDeltaY = (float) (deltaY / height);
			position.applyDelta(relativeDeltaX, relativeDeltaY);
			return true;
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.action == Action.MOVE) {
			this.action = this.afterMoveAction;
			if (this.action == Action.EDIT) {
				this.buildSettings();
			} else {
				this.resetSettings();
			}
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		if (action == Action.EDIT) {
			if (isOnLeft && mouseX < 120) {
				return true;
			} else if (mouseX > width - 120) {
				return true;
			}
			if (this.getElementUnder((int) mouseX, (int) mouseY).orElse(null) == this.currentlyHovered) {
				this.resetSettings();
				this.action = Action.MOVE;
				this.afterMoveAction = Action.EDIT;
				return true;
			}
		}
		if (currentlyHovered != null) {
			if (hasControlDown()) {
				action = Action.EDIT;
				this.afterMoveAction = Action.NONE;
				this.buildSettings();
				return true;
			}
			if (action == Action.EDIT) {
				action = Action.NONE;
				this.afterMoveAction = Action.NONE;
				this.resetSettings();
				return true;
			}
			action = Action.MOVE;
			this.afterMoveAction = Action.NONE;
			return true;
		}

		return true;
	}

	private void resetSettings() {
		this.currentSettings.forEach(this::remove);
		this.currentSettings = Collections.emptyList();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		this.buildSettings();
	}

	private void buildSettings() {
		this.resetSettings();
		if (this.currentlyHovered == null) {
			return;
		}

		int elementX = currentlyHovered.getX();
		this.isOnLeft = elementX > width / 2;

		final HudElementSettingBuilder hudElementSettingBuilder = new HudElementSettingBuilder();
		this.currentlyHovered.buildSettings(hudElementSettingBuilder);
		final List<HudElementSetting> build = hudElementSettingBuilder.build();

		build.forEach(HudElementSetting::init);
		sidebarSize = Math.max(build.stream().mapToInt(HudElementSetting::getWidth)
				.max().orElse(0), 100) + 10;
		final int xStart;
		if (this.isOnLeft) {
			xStart = 10;
		} else {
			xStart = width - sidebarSize + 10;
		}
		int y = 20;
		int heightPer = build.stream().mapToInt(HudElementSetting::getHeight)
				.max().orElse(0);

		if (heightPer == 0) {
			return;
		}

		for (HudElementSetting hudElementSetting : build) {
			hudElementSetting.setSidebarElementHeight(heightPer);
			hudElementSetting.setSidebarWidth(sidebarSize - 10);
			hudElementSetting.setX(xStart);
			hudElementSetting.setY(y);
			y += heightPer;
		}

		this.currentSettings = build;
		this.currentSettings.forEach(this::addDrawableChild);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (action == Action.MOVE && currentlyHovered != null) {
			currentlyHovered.getPosition().applyScale((float) verticalAmount);
			this.lastSizeChange = System.currentTimeMillis();
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	public Optional<HudElement> getElementUnder(int x, int y) {
		return getVisibleElements().stream()
				.filter(hudElement -> expandBox(hudElement.getScaledBoundingBox(), 0).isPointInsideBox(x, y))
				.findFirst();
	}

	public BoundingBox expandBox(BoundingBox box, float amount) {
		return box.expand(amount);
	}

	public List<HudElement> getVisibleElements() {
		return HudManager.elements.stream().filter(HudElement::shouldRender).toList();
	}

	public List<HudElement> getElements() {
		return HudManager.elements;
	}
}
