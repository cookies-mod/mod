package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.element.DropdownElement;
import dev.morazzer.cookies.mod.config.system.options.EnumCycleOption;
import dev.morazzer.cookies.mod.utils.CookiesUtils;
import dev.morazzer.cookies.mod.utils.sound.SoundUtils;
import java.util.Arrays;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to select a value from an enum.
 *
 * @param <T> The type of the enum to get the values from.
 */
public class EnumCycleEditor<T extends Enum<T>> extends ConfigOptionEditor<T, EnumCycleOption<T>> {

    private ButtonWidget buttonWidget;
    private int amount = 0;
    private Text text;

    @SuppressWarnings("MissingJavadoc")
    public EnumCycleEditor(EnumCycleOption<T> option) {
        super(option);
    }

    @Override
    public void init() {
        this.buttonWidget = new ButtonWidget.Builder(Text.empty(), this::onClick).build();
        this.amount = this.option.getValue().getDeclaringClass().getEnumConstants().length;
        this.text = this.getOptionText();
    }

    private void onClick(ButtonWidget buttonWidget) {
        int index = (this.option.getValue().ordinal() + 1) % this.amount;
        this.option.setValue(this.option.getValue().getDeclaringClass().getEnumConstants()[index]);
        this.text = this.getOptionText();
    }

    private Text getOptionText() {
        return this.option.getTextSupplier().supplyText(this.option.getValue());
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
        drawContext.drawText(this.getTextRenderer(), this.option.getName(), 2,
            this.getHeight(optionWidth) / 2 - this.getTextRenderer().fontHeight / 2, 0xFFFFFFFF, true);

        this.buttonWidget.setX(optionWidth - 55);
        this.buttonWidget.setY(this.getHeight() - 16);

        this.buttonWidget.setWidth(50);
        this.buttonWidget.setHeight(14);
        this.buttonWidget.render(drawContext, mouseX, mouseY, tickDelta);

        drawContext.drawCenteredTextWithShadow(this.getTextRenderer(), this.text,
            this.buttonWidget.getX() + this.buttonWidget.getWidth() / 2, this.buttonWidget.getY() +3, 0xFFFFFFFF);
    }

    @Override
    public boolean doesMatchSearch(@NotNull String search) {
        return super.doesMatchSearch(search) || Arrays
            .stream(this.option.getValue().getDeclaringClass().getEnumConstants())
            .map(this.option.getTextSupplier()::supplyText)
            .map(Text::getString)
            .anyMatch(option -> option.contains(search));
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
