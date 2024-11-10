package codes.cookies.mod.render.hud.settings;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import codes.cookies.mod.config.system.options.ColorOption;
import codes.cookies.mod.render.widgets.ColorInputWidget;
import codes.cookies.mod.screen.CookiesScreen;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.text.Text;

public class ColorSetting extends HudElementSetting {

	final ColorInputWidget inputWidget;
	private final Text name;
	private final Text description;
	private final Supplier<Color> getter;

	public ColorSetting(
			Text name,
			Text description,
			Supplier<Color> getter,
			Consumer<Color> setter,
			boolean canHaveAlpha,
			HudElementSettingType type
	) {
		super(type);
		this.name = name;
		this.description = description;
		this.getter = getter;
		this.inputWidget = new ColorInputWidget(
				getTextRenderer(),
				this.getTextRenderer().fontHeight + 2,
				this.getter.get(),
				canHaveAlpha);
		this.inputWidget.setCallback(setter);
	}

	public ColorSetting(
			Text name,
			Text description,
			Supplier<Color> getter,
			Consumer<Color> setter,
			boolean canHaveAlpha
	) {
		this(name, description, getter, setter, canHaveAlpha, HudElementSettingType.CUSTOM);
	}

	public ColorSetting(ColorOption option) {
		this(
				option.getName(),
				option.getDescription(),
				option::getValue,
				option::setValue,
				option.isAllowAlpha(),
				HudElementSettingType.CUSTOM);
	}

	@Override
	public void init() {
		super.init();
		this.inputWidget.setColor(this.getter.get());
		this.inputWidget.update();
	}

	@Override
	public int getHeight() {
		return 11;
	}

	@Override
	public int getWidth() {
		return this.getTextRenderer().getWidth(this.name) + this.inputWidget.getWidth() + 10;
	}

	@Override
	int getActualWidth() {
		return super.sidebarWidth;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		int elementY = this.y + sidebarElementHeight / 2 - 4;
		this.inputWidget.setPosition(this.x + sidebarWidth - this.inputWidget.getWidth() - 5, elementY);
		context.drawText(this.getTextRenderer(), this.name, this.x, elementY, 0xFFFFFFFF, false);

		if (CookiesScreen.isInBound(
				mouseX,
				mouseY,
				this.x,
				elementY,
				this.getTextRenderer().getWidth(this.name),
				this.getTextRenderer().fontHeight)) {
			context.drawTooltip(
					getTextRenderer(),
					getTextRenderer().wrapLines(this.description, 300),
					HoveredTooltipPositioner.INSTANCE,
					mouseX,
					mouseY);
		}
		this.inputWidget.renderWidget(context, mouseX, mouseY, delta);
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.inputWidget.mouseClicked(mouseX, mouseY, button)) {
			this.inputWidget.setFocused(true);
			return true;
		}
		this.inputWidget.setFocused(false);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.inputWidget.isFocused() && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER)) {
			this.inputWidget.setFocused(false);
			return true;
		}
		if (this.inputWidget.isFocused()) {
			this.inputWidget.keyPressed(keyCode, scanCode, modifiers);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.inputWidget.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.inputWidget.keyReleased(keyCode, scanCode, modifiers) || super.keyReleased(
				keyCode,
				scanCode,
				modifiers);
	}
}
