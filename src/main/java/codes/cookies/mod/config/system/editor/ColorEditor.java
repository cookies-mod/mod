package codes.cookies.mod.config.system.editor;

import codes.cookies.mod.config.system.options.ColorOption;

import codes.cookies.mod.utils.accessors.TextRenderUtils;

import java.awt.Color;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Editor to select a color value.
 */
public class ColorEditor extends ConfigOptionEditor<Color, ColorOption> {

	private TextFieldWidget textField;
	private final boolean canHaveAlpha;
	private final int maxLength;

	public ColorEditor(ColorOption option) {
		super(option);
		this.canHaveAlpha = option.isAllowAlpha();
		if (this.canHaveAlpha) {
			this.maxLength = 9;
		} else {
			this.maxLength = 7;
		}
	}

	@Override
	public void init() {
		this.textField = new TextFieldWidget(
				this.getTextRenderer(),
				this.maxLength * 5 + 10,
				this.getTextRenderer().fontHeight + 2,
				Text.literal(this.toText(this.option.getValue())));
		this.textField.setEditable(true);
		this.textField.setEditableColor(this.getFullAlphaColor(
				this.option.getValue() == null ? -1 : this.option.getValue().getRGB()));
		this.textField.setChangedListener(this::changeValue);
		this.textField.active = true;
		this.textField.setVisible(true);
		this.textField.setTextPredicate(this::isAllowed);
		this.textField.setEditable(true);
		this.textField.setDrawsBackground(false);
		this.textField.setMaxLength(this.maxLength);
		this.textField.setText(this.toText(this.option.getValue()));
	}

	private boolean isAllowed(String s) {
		if (!s.startsWith("#")) {
			return false;
		}
		if (s.length() == 1) {
			return true;
		}
		try {
			Integer.parseUnsignedInt(s.substring(1), 16);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private String toText(Color color) {
		if (color == null) {
			if (this.canHaveAlpha) {
				return "#00000000";
			}
			return "#000000";
		}
		return "#" + Integer.toHexString(this.canHaveAlpha ? color.getRGB() : color.getRGB() & 0xFFFFFF);
	}

	public Color toColor(String text) {
		if (StringUtils.isBlank(text)) {
			return null;
		}
		try {

			final int color = Integer.parseUnsignedInt(text.substring(1), 16);
			return new Color(color, this.canHaveAlpha);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void changeValue(String s) {
		final Color color = this.toColor(s);
		if (color == null) {
			return;
		}
		this.textField.setEditableColor(this.getFullAlphaColor(color.getRGB()));
		this.option.setValue(this.toColor(s));
	}

	private int getFullAlphaColor(int color) {
		return 0xFF << 24 | color & 0xFFFFFF;
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

	private int getBackgroundColor() {
		return 0xFF << 24 | ~this.option.getValue().getRGB() & 0xFFFFFF;
	}

	@Override
	public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
		this.textField.setPosition(optionWidth - this.textField.getWidth() - 3, 4);
		super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
		drawContext.drawText(this.getTextRenderer(), this.option.getName(), 2, 3, 0xFFFFFFFF, true);
		final int fullAlphaColor =
				this.getFullAlphaColor(this.option.getValue() == null ? -1 : this.option.getValue().getRGB());
		drawContext.fill(
				this.textField.getX() - 2,
				this.textField.getY() - 2,
				this.textField.getX() + this.textField.getWidth(),
				this.textField.getY() + this.textField.getHeight() - 1,
				fullAlphaColor);
		drawContext.fill(
				this.textField.getX() - 1,
				this.textField.getY() - 1,
				this.textField.getX() + this.textField.getWidth() - 1,
				this.textField.getY() + this.textField.getHeight() - 2,
				this.getBackgroundColor());

		TextRenderUtils.disableShadows();
		this.textField.render(drawContext, mouseX, mouseY, tickDelta);
		TextRenderUtils.enableShadows();

		drawContext.getMatrices().push();
		drawContext.getMatrices().translate(0, 0, 10);
		drawContext.fill(
				this.textField.getX() - 2,
				this.textField.getY() + this.textField.getHeight() - 2,
				this.textField.getX() + this.textField.getWidth(),
				this.textField.getY() + this.textField.getHeight() - 1,
				fullAlphaColor);
		drawContext.getMatrices().pop();
	}

	@Override
	public int getHeight() {
		return 18;
	}
}
