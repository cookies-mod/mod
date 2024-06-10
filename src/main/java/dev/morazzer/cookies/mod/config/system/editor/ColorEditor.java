package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.ColorOption;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to select a color value.
 */
public class ColorEditor extends ConfigOptionEditor<Color, ColorOption> {

    private static final Identifier COLOR_BUTTON = Identifier.of("cookiesmod", "gui/config/color_button.png");
    private static final Identifier COLOR_BAR_OVERLAY = Identifier.of("cookiesmod", "gui/config/color_bar_overlay.png");
    private static final Identifier COLOR_CIRCLE = Identifier.of("cookiesmod", "gui/config/color_circle.png");
    private static final Identifier COLOR_SATURATION = Identifier.of("cookiesmod", "gui/config/color_saturation.png");
    int clickedComponent = -1;
    private boolean renderOverlay = false;
    private int overlayX = 0;
    private int overlayY = 0;
    private int overlayWidth = 104;
    private TextFieldWidget textFieldWidget;
    private float wheelAngle = 0;
    private float wheelRadius = 0;
    private float[] hsb;

    @SuppressWarnings("MissingJavadoc")
    public ColorEditor(ColorOption option) {
        super(option);
        if (!option.isAllowAlpha()) {
            overlayWidth -= 15;
        }
        this.recalculateHsb();
    }

    /**
     * Updates the value in the {@linkplain dev.morazzer.cookiesmod.config.system.editor.ColorEditor#hsb} variable.
     */
    private void recalculateHsb() {
        Color color = this.option.getValue();
        this.hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getRed(), null);
        this.wheelRadius = hsb[1];
        this.wheelAngle = hsb[0] * 360;
    }

    @Override
    public void init() {
        this.textFieldWidget = new TextFieldWidget(getTextRenderer(), 0, -10, 48, 10, Text.literal("#000000"));
        this.renderOverlay = false;
        this.overlayY = 0;
        this.overlayX = 0;
        this.textFieldWidget.setRenderTextProvider((textFieldString, integer) -> {
            String s = StringUtils.leftPad("", 6 - textFieldString.length(), '0');
            return Text.literal("#").append(s).append(Text.literal(textFieldString).formatted(Formatting.WHITE))
                .formatted(Formatting.WHITE).asOrderedText();
        });
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);

        // TODO render
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
        if (this.renderOverlay
            && (mouseX >= this.overlayX) && (mouseX < this.overlayX + this.overlayWidth)
            && (mouseY >= this.overlayY) && (mouseY < this.overlayY + 89)) {
            if ((mouseX >= (overlayX + 75)) && (mouseX < (overlayX + 85))
                && (mouseY >= (overlayY + 5)) && (mouseY < (overlayY + 69))) {
                this.clickedComponent = 1;
            }

            this.handleClickOrDragged(mouseX, mouseY, button, optionWidth);
            return true;
        }

        int buttonLeft = optionWidth / 6 - 24;
        int buttonTop = this.getHeight() - 21;
        if ((mouseX > buttonLeft) && (mouseX < (buttonLeft + 48))
            && (mouseY > buttonTop) && (mouseY < (buttonTop + 16))) {
            this.renderOverlay = true;
            this.overlayX = (int) mouseX;
            this.overlayY = (int) mouseY;
            return false;
        }
        this.renderOverlay = false;
        this.overlayY = 0;
        this.overlayX = 0;

        return false;
    }

    @Override
    public void renderOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        if (!renderOverlay) {
        }

        // TODO render
    }

    /**
     * Called whenever a mouse button was clicked.
     *
     * @param mouseX      The current x position of the mouse.
     * @param mouseY      The current y position of the mouse.
     * @param button      The button that was clicked.
     * @param optionWidth The width the option is rendered at.
     */
    @SuppressWarnings("EmptyMethod")
    private void handleClickOrDragged(double mouseX, double mouseY, int button, int optionWidth) {
    }

}
