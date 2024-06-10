package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.element.DropdownElement;
import dev.morazzer.cookies.mod.config.system.options.StringDropdownOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to select a single string from a predefined list.
 */
public class StringDropdownEditor extends ConfigOptionEditor<String, StringDropdownOption> {

    private DropdownElement<String> dropdownElement;

    @SuppressWarnings("MissingJavadoc")
    public StringDropdownEditor(StringDropdownOption option) {
        super(option);
    }

    @Override
    public void init() {
        this.dropdownElement = new DropdownElement<>(
            this.option.getPossibleValues().toArray(String[]::new),
            Text::literal
        );
        this.dropdownElement.setSelected(this.option.getValue());
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
        renderDropdown(drawContext, optionWidth);
    }

    private void renderDropdown(@NotNull DrawContext drawContext, int optionWidth) {
        int dropdownWidth = this.getDropdownWidth(optionWidth);

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate((float) optionWidth / 6 - (float) dropdownWidth / 2, getHeight() - 21, 0);
        dropdownElement.render(drawContext, dropdownWidth);
        drawContext.getMatrices().pop();
    }

    /**
     * Helper to get the width of the dropdown menu.
     *
     * @param optionWidth The width the option is rendered at.
     * @return The width of the dropdown.
     */
    private int getDropdownWidth(int optionWidth) {
        return Math.min(optionWidth / 3 - 10, 80);
    }

    @Override
    public boolean doesMatchSearch(@NotNull String search) {
        return super.doesMatchSearch(search) || this.option
            .getPossibleValues()
            .stream()
            .anyMatch(key -> key.contains(search));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
        int dropdownWidth = this.getDropdownWidth(optionWidth);
        int dropdownLeft = (int) (optionWidth / 6f - dropdownWidth / 2f);
        int dropdownTop = getHeight() - 21;

        String value;
        if ((value = this.dropdownElement.mouseClicked(
            mouseX - dropdownLeft,
            mouseY - dropdownTop,
            dropdownWidth
        )) != null) {
            this.option.setValue(value);
        }

        return super.mouseClicked(mouseX, mouseY, button, optionWidth);
    }

    @Override
    public void renderOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        renderDropdown(drawContext, optionWidth);
    }

}
