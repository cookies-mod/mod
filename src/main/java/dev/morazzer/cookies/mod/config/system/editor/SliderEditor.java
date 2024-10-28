package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.SliderOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to select a numeric value with a min and a max value.
 *
 * @param <T> The type of the number.
 */
public class SliderEditor<T extends Number> extends ConfigOptionEditor<T, SliderOption<T>> {

    private static final Identifier SLIDER_CAP_ON = Identifier.of("cookiesmod", "gui/config/slider_cap_on.png");
    private static final Identifier SLIDER_CAP_OFF = Identifier.of("cookiesmod", "gui/config/slider_cap_off.png");
    private static final Identifier SLIDER_SEGMENT_ON = Identifier.of("cookiesmod", "gui/config/slider_segment_on.png");
    private static final Identifier SLIDER_SEGMENT_OFF = Identifier.of(
        "cookiesmod",
        "gui/config/slider_segment_off.png"
    );
    private static final Identifier SLIDER_NOTCH_ON = Identifier.of("cookiesmod", "gui/config/slider_notch_on.png");
    private static final Identifier SLIDER_NOTCH_OFF = Identifier.of("cookiesmod", "gui/config/slider_notch_off.png");
    private static final Identifier SLIDER_BUTTON = Identifier.of("cookiesmod", "gui/config/slider_button.png");
    private TextFieldWidget textFieldWidget;
    private boolean isCurrentlyClicked = false;

    @SuppressWarnings("MissingJavadoc")
    public SliderEditor(SliderOption<T> option) {
        super(option);
    }

    @Override
    public void init() {
        this.textFieldWidget = new TextFieldWidget(this.getTextRenderer(), 0, 0, 0, 16, Text.of(""));
        this.isCurrentlyClicked = false;
        this.textFieldWidget.setTextPredicate(text -> {
            try {
                double v = Double.parseDouble(text);
                return (v >= this.option.getMin().doubleValue()) && (v <= this.option.getMax().doubleValue());
            } catch (Exception e) {
                return text.matches("-?\\d*\\.?\\d*");
            }
        });
        this.textFieldWidget.setRenderTextProvider((text, integer) -> {
            if (this.textFieldWidget.isFocused()) {
                return Text.literal(text).asOrderedText();
            }

            return Text.literal(getValue()).asOrderedText();
        });
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
        int height = getHeight();

        int fullWidth = Math.min(optionWidth / 3 - 10, 80);
        int sliderWidth = (fullWidth - 5) * 3 / 4;
        int textFieldWidth = (fullWidth - 5) / 4;

        if (!this.textFieldWidget.isFocused()) {
            this.textFieldWidget.setText(getValue());
        }
        this.textFieldWidget.setWidth(textFieldWidth + this.getTextRenderer().getWidth(this.textFieldWidget.getText()));
        textFieldWidget.setPosition(optionWidth / 6 - fullWidth / 2 + sliderWidth + 5, height - 21);
        textFieldWidget.render(drawContext, mouseX, mouseY, tickDelta);

        float sliderAmount = Math.max(
            0,
            Math.min(
                1,
                (this.option.getValue().floatValue() - this.option.getMin().floatValue()) / (this.option
                                                                                                 .getMax()
                                                                                                 .floatValue() -
                                                                                             this.option.getMin()
                                                                                                 .floatValue())
            )
        );
        int sliderPosition = (int) (sliderWidth * sliderAmount);

        int sliderX = optionWidth / 6 - fullWidth / 2;
        int sliderY = height - 7 - 14;

        drawContext.drawTexture(RenderLayer::getGuiTextured, SLIDER_CAP_ON, sliderX, sliderY, 0, 0, 4, 16, 4, 16);
        drawContext.drawTexture(RenderLayer::getGuiTextured, SLIDER_CAP_OFF, sliderX + sliderWidth - 4, sliderY, 0, 0, 4, 16, 4, 16);

        if (sliderPosition > 5) {
            drawContext.drawTexture(
					RenderLayer::getGuiTextured,
					SLIDER_SEGMENT_ON,
                sliderX + 4,
                sliderY,
                0,
                0,
                sliderPosition - 4,
                16,
                sliderPosition - 4,
                16
            );
        }
        if (sliderPosition < sliderWidth - 5) {
            drawContext.drawTexture(RenderLayer::getGuiTextured,
					SLIDER_SEGMENT_OFF,
                sliderX + sliderPosition,
                sliderY,
                0,
                0,
                sliderWidth - 4 - sliderPosition,
                16,
                sliderWidth - 4 - sliderPosition,
                16
            );
        }

        for (int i = 1; i < 4; i++) {
            int notchX = sliderX + sliderWidth * i / 4 - 1;
            Identifier identifier = (notchX > sliderX + sliderPosition) ? SLIDER_NOTCH_OFF : SLIDER_NOTCH_ON;
            drawContext.drawTexture(RenderLayer::getGuiTextured, identifier, notchX, sliderY + 6, 0, 0, 2, 4, 2, 4);
        }

        drawContext.drawTexture(RenderLayer::getGuiTextured, SLIDER_BUTTON, sliderX + sliderPosition - 4, sliderY, 0, 0, 8, 16, 8, 16);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputUtil.GLFW_KEY_ENTER) {
            this.saveValue();
        }
        return this.textFieldWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
        int fullWidth = Math.min(optionWidth / 3 - 10, 80);
        int sliderWidth = (fullWidth - 5) * 3 / 4;
        int textFieldWidth = (fullWidth - 5) / 4;

        int sliderX = optionWidth / 6 - fullWidth / 2;
        int sliderY = getHeight() - 7 - 14;
        if (mouseX > sliderX + sliderWidth + 5
            && mouseX < sliderX + sliderWidth + 5 + textFieldWidth + this
            .getTextRenderer()
            .getWidth(this.textFieldWidget.getText())
            && mouseY > getHeight() - 21
            && mouseY < getHeight() - 5) {
            this.textFieldWidget.setFocused(true);
            this.textFieldWidget.active = true;
            return true;
        }
        if (this.textFieldWidget.isFocused()) {
            this.saveValue();
        }

        isCurrentlyClicked =
            (mouseX > sliderX) && (mouseX < (sliderX + sliderWidth)) && (mouseY > sliderY) && (mouseY < (sliderY + 16));
        this.setIfClicked((float) mouseX, optionWidth);

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isCurrentlyClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
        double mouseX, double mouseY, int button, double deltaX, double deltaY, int optionWidth
    ) {
        this.setIfClicked((float) mouseX, optionWidth);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY, optionWidth);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return this.textFieldWidget.charTyped(character, modifiers);
    }

    /**
     * Sets the value based on the current mouse position if the mouse is currently dragging the slider.
     *
     * @param mouseX      The current x position of the mouse.
     * @param optionWidth The width the option is rendered at.
     */
    private void setIfClicked(float mouseX, int optionWidth) {
        if (isCurrentlyClicked) {
            int fullWidth = Math.min(optionWidth / 3 - 10, 80);
            int sliderWidth = (fullWidth - 5) * 3 / 4;
            int sliderX = optionWidth / 6 - fullWidth / 2;
            float mouseClickPercentage = (mouseX - sliderX) / sliderWidth;
            float value = mouseClickPercentage * (this.option.getMax().floatValue() - this.option
                .getMin()
                .floatValue()) + this.option.getMin().floatValue();
            value = Math.max(this.option.getMin().floatValue(), Math.min(this.option.getMax().floatValue(), value));
            value = (float) (Math.round(value / this.option.getStep().floatValue()) * (double) this.option
                .getStep()
                .floatValue());
            this.option.setValue(value);
        }
    }

    /**
     * Saves the current value to the option instance and updates the text field.
     */
    private void saveValue() {
        this.option.setValue(Double.parseDouble(this.textFieldWidget.getText()));
        this.textFieldWidget.setFocused(false);
        this.textFieldWidget.active = false;
    }

    /**
     * Gets the value as string.
     *
     * @return The value as string.
     */
    @NotNull
    private String getValue() {
        float value = this.option.getValue().floatValue();
        if (value - ((int) value) == 0) {
            return String.valueOf((long) value);
        }

        String returnValue = Float.toString(value);
        returnValue = returnValue.replaceAll("(\\.\\d{3})\\d+", "$1");
        return returnValue.replaceAll("0+$", "");
    }

}
