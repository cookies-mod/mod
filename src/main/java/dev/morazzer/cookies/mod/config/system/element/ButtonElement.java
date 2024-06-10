package dev.morazzer.cookies.mod.config.system.element;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * GUI element that works as a button.
 */
public class ButtonElement {

    private static final Identifier BUTTON = Identifier.of("cookiesmod", "gui/config/button.png");
    private final Runnable runnable;
    private final Text text;

    /**
     * Creates a new button that can be rendered everywhere.
     *
     * @param runnable The runnable that will be executed if the button is clicked.
     * @param text     The text that will be displayed on the button.
     */
    public ButtonElement(Runnable runnable, Text text) {
        this.runnable = runnable;
        this.text = text;
    }

    /**
     * Renders the button onto the current draw context.
     *
     * @param drawContext The current draw context.
     */
    public void render(DrawContext drawContext) {
        // TODO render
    }

    /**
     * Checks if the mouse click occurred above the button and executes the runnable if so.
     *
     * @param mouseX The current x position of the mouse.
     * @param mouseY The current y position of the mouse.
     * @return If the runnable was executed.
     */
    public boolean mouseClicked(double mouseX, double mouseY) {
        if ((mouseX > 0) && (mouseX < (48))
            && (mouseY > 0) && (mouseY < (16))) {
            this.runnable.run();
            return true;
        }
        return false;
    }

}
