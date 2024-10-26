package dev.morazzer.cookies.mod.config.system.element;

import dev.morazzer.cookies.mod.utils.maths.LinearInterpolatedInteger;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import net.minecraft.client.gui.DrawContext;
	import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * GUI element to render a button everywhere.
 */
public class SwitchElement {

    private static final Identifier BAR = Identifier.of("cookiesmod", "gui/config/toggle_bar.png");
    private static final Identifier TOGGLE_OFF = Identifier.of("cookiesmod", "gui/config/toggle_off.png");
    private static final Identifier TOGGLE_STEP_ONE = Identifier.of("cookiesmod", "gui/config/toggle_one.png");
    private static final Identifier TOGGLE_STEP_TWO = Identifier.of("cookiesmod", "gui/config/toggle_two.png");
    private static final Identifier TOGGLE_STEP_THREE = Identifier.of("cookiesmod", "gui/config/toggle_three.png");
    private static final Identifier TOGGLE_ON = Identifier.of("cookiesmod", "gui/config/toggle_on.png");
    private final LinearInterpolatedInteger value = new LinearInterpolatedInteger(200, 0);
    boolean booleanValue;

    /**
     * Creates a new button.
     *
     * @param value The value that will be used initially.
     */
    public SwitchElement(boolean value) {
        this.booleanValue = value;
    }

    /**
     * Initializes the button to be rendered correctly.
     */
    public void init() {
        value.setTargetValue(booleanValue ? 100 : 0);
    }

    /**
     * Renders the button onto the current draw context.
     *
     * @param drawContext The current draw context.
     */
    public void render(@NotNull DrawContext drawContext) {
        value.tick();

        drawContext.drawTexture(RenderLayer::getGuiTextured, BAR, 0, 0, 0, 0, 48, 14, 48, 14);

        float animationPercentage = MathUtils.sigmoidZeroOne(value.getValue() / 100F);
        Identifier buttonIdentifier;
        if (animationPercentage < 0.2) {
            buttonIdentifier = TOGGLE_OFF;
        } else if (animationPercentage < 0.4) {
            buttonIdentifier = TOGGLE_STEP_ONE;
        } else if (animationPercentage < 0.6) {
            buttonIdentifier = TOGGLE_STEP_TWO;
        } else if (animationPercentage < 0.8) {
            buttonIdentifier = TOGGLE_STEP_THREE;
        } else {
            buttonIdentifier = TOGGLE_ON;
        }

        drawContext.drawTexture(RenderLayer::getGuiTextured, buttonIdentifier, (int) (animationPercentage * 36), 0, 0, 0, 12, 14, 12, 14);
    }

    /**
     * Checks if the button was clicked and change its value if so.
     *
     * @param mouseX The current x position of the mouse.
     * @param mouseY The current y position of the mouse.
     * @param button The button that was clicked.
     * @return True if the value was changed, false otherwise.
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((mouseX >= 0) && (mouseX < (48))
            && (mouseY >= 0) && (mouseY < (14))) {
            if (button == 0) {
                this.booleanValue = !booleanValue;
                int newTarget = this.booleanValue ? 100 : 0;
                this.value.setTargetValue(newTarget);
                return true;
            }
        }
        return false;
    }

    /**
     * Changes the target value of the {@linkplain cm.utils.maths.LinearInterpolatedInteger}.
     *
     * @param targetValue The target value between 0 and 100.
     */
    public void setTargetValue(@Range(from = 0, to = 100) int targetValue) {
        value.setTargetValue(targetValue);
    }

}
