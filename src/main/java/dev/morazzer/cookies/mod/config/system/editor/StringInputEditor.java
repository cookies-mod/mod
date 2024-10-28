package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.StringInputOption;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

/**
 * Input option for strings, this will add a text field and a title above said text field.
 */
public class StringInputEditor extends ConfigOptionEditor<String, StringInputOption> {

	private TextFieldWidget textField;

	/**
	 * Creates a new option editor.
	 *
	 * @param option The option the editor belongs to.
	 */
	public StringInputEditor(@NotNull StringInputOption option) {
		super(option);
	}

	@Override
	public void init() {
		this.textField = new TextFieldWidget(this.getTextRenderer(), 0, 9, Text.literal(this.option.getValue()));
		this.textField.setEditable(true);
		this.textField.setPosition(3, 18);
		this.textField.setHeight(12);
		this.textField.setChangedListener(this::changeValue);
		this.textField.active = true;
		this.textField.setVisible(true);
		this.textField.setEditable(true);
		this.textField.setWidth(12);
		this.textField.setMaxLength(1024);
		this.textField.setEditable(true);
		this.textField.setText(this.option.getValue());

		this.option.withCallback(this::updateIfChanged);
		super.init();
	}

	private void updateIfChanged(String previousValue, String newValue) {
		if (this.textField.getText().equals(newValue)) {
			return;
		}

		this.textField.setText(newValue);
	}

	private void updateWidth(int optionWidth) {
		this.textField.setWidth(Math.min(optionWidth - 8,
				this.getTextRenderer().getWidth(this.option.getValue()) + 18));
		this.textField.setText(this.option.getValue());
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

	private void changeValue(String s) {
		this.option.setValue(s);
	}

	@Override
	public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
		super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
		this.updateWidth(optionWidth);
		drawContext.drawText(this.getTextRenderer(), this.option.getName(), 2, 3, 0xFFFFFFFF, true);
		this.textField.render(drawContext, mouseX, mouseY, tickDelta);
	}

	@Override
	public int getHeight() {
		return 18 * 2;
	}
}
