package codes.cookies.mod.config.system.editor;

import codes.cookies.mod.config.system.options.ButtonOption;

import java.util.Locale;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to display a single button.
 */
public class ButtonEditor extends ConfigOptionEditor<Runnable, ButtonOption> {

    private ButtonWidget buttonWidget;
    private Text text;
    private int maxButtonWidth;

    @SuppressWarnings("MissingJavadoc")
    public ButtonEditor(ButtonOption option) {
        super(option);
    }

    @Override
    public void init() {
        this.buttonWidget = new ButtonWidget.Builder(Text.empty(), this::onClick).build();
        this.text = this.getOptionText();

        maxButtonWidth = getTextRenderer().getWidth(this.text);

        this.buttonWidget.setWidth(maxButtonWidth + 6);

        this.buttonWidget.setY(this.getHeight() - 16);
        this.buttonWidget.setHeight(14);
    }

    private void onClick(ButtonWidget buttonWidget) {
        this.option.getValue().run();
    }

    private Text getOptionText() {
        return this.option.getButtonText();
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
        drawContext.drawText(this.getTextRenderer(), this.option.getName(), 2,
            this.getHeight(optionWidth) / 2 - this.getTextRenderer().fontHeight / 2, 0xFFFFFFFF, true);

        this.buttonWidget.setX(optionWidth - maxButtonWidth - 8);
        this.buttonWidget.render(drawContext, mouseX, mouseY, tickDelta);

        drawContext.drawCenteredTextWithShadow(this.getTextRenderer(), this.text,
            this.buttonWidget.getX() + this.buttonWidget.getWidth() / 2, this.buttonWidget.getY() +3, 0xFFFFFFFF);
    }

    @Override
    public boolean doesMatchSearch(@NotNull String search) {
        return super.doesMatchSearch(search) || this.getOptionText().getString().toLowerCase(Locale.ROOT).contains(search);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
        if (this.buttonWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button, optionWidth);
    }

    @Override
    public int getHeight() {
        return 18;
    }

}
