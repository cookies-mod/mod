package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.element.ButtonElement;
import dev.morazzer.cookies.mod.config.system.options.ButtonOption;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to display a single button.
 */
public class ButtonEditor extends ConfigOptionEditor<Runnable, ButtonOption> {

    private final ButtonElement buttonElement;

    @SuppressWarnings("MissingJavadoc")
    public ButtonEditor(ButtonOption option) {
        super(option);
        this.buttonElement = new ButtonElement(option.getValue(), option.getButtonText());
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate((float) optionWidth / 6 - 24, this.getHeight() - 21, 1);
        this.buttonElement.render(drawContext);
        drawContext.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
        int buttonLeft = optionWidth / 6 - 24;
        int buttonTop = this.getHeight() - 21;
        return this.buttonElement.mouseClicked(mouseX - buttonLeft, mouseY - buttonTop);
    }

}
