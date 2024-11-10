package codes.cookies.mod.config.system.editor;

import codes.cookies.mod.config.system.options.ColorOption;

import codes.cookies.mod.render.widgets.ColorInputWidget;

import java.awt.Color;

import net.minecraft.client.gui.DrawContext;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Editor to select a color value.
 */
public class ColorEditor extends ConfigOptionEditor<Color, ColorOption> {

	private ColorInputWidget textField;

	public ColorEditor(ColorOption option) {
		super(option);
	}

	@Override
	public void init() {
		this.textField = new ColorInputWidget(
				this.getTextRenderer(),
				this.getTextRenderer().fontHeight + 2,
				this.option.getValue(), this.option.isAllowAlpha());
		this.textField.setColor(this.option.getValue());
		this.textField.update();
		this.textField.setCallback(this.option::setValue);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
		if (this.textField.mouseClicked(mouseX, mouseY, button)) {
			this.textField.setFocused(true);
			return true;
		}
		this.textField.setFocused(false);
		return super.mouseClicked(mouseX, mouseY, button, optionWidth);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.textField.isFocused() && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER)) {
			this.textField.setFocused(false);
			return true;
		}
		if (this.textField.isFocused()) {
			this.textField.keyPressed(keyCode, scanCode, modifiers);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char character, int modifiers) {
		if (this.textField.charTyped(character, modifiers)) {
			return true;
		}

		return super.charTyped(character, modifiers);
	}

	@Override
	public void keyReleased(int keyCode, int scanCode, int modifiers) {
		if (this.textField.keyReleased(keyCode, scanCode, modifiers)) {
			return;
		}
		super.keyReleased(keyCode, scanCode, modifiers);
	}


	@Override
	public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
		this.textField.setPosition(optionWidth - this.textField.getWidth() - 3, 4);
		super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
		drawContext.drawText(this.getTextRenderer(), this.option.getName(), 2, 3, 0xFFFFFFFF, true);
		this.textField.renderWidget(drawContext, mouseX, mouseY, tickDelta);
	}

	@Override
	public int getHeight() {
		return 18;
	}
}
