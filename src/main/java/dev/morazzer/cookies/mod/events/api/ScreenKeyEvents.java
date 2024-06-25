package dev.morazzer.cookies.mod.events.api;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.Screen;

/**
 * Extension for key related events.
 */
public interface ScreenKeyEvents {

    /**
     * Default handler for the charTyped invocation.
     *
     * @param screen    The screen it was typed in.
     * @param chr       The char that was typed.
     * @param modifiers The modifiers.
     * @return True if the invocation should be cancelled.
     */
    static boolean handle(Screen screen, char chr, int modifiers) {
        try {
            final boolean allow =
                ScreenKeyEvents.getExtension(screen).cookies$allowCharTyped().invoker().allowKeyPress(
                    screen,
                    chr,
                    modifiers);

            if (!allow) {
                return true;
            }

            ScreenKeyEvents.getExtension(screen).cookies$beforeCharTyped().invoker().keyPressed(
                screen,
                chr,
                modifiers);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Event to cancel the invocation of the char typed method on a screen.
     *
     * @return False if the char should not be allowed, true otherwise.
     */
    Event<AllowCharTyped> cookies$allowCharTyped();

    /**
     * Gets the events for a specific screen.
     *
     * @param screen The screen to get.
     * @return The events.
     */
    static ScreenKeyEvents getExtension(Screen screen) {
        return (ScreenKeyEvents) screen;
    }

    /**
     * Event to listen to the invocation of the char typed method on a specific screen.
     *
     * @return The event.
     */
    Event<CharTyped> cookies$beforeCharTyped();

    interface AllowCharTyped {
        /**
         * Checks if a key should be allowed to be pressed.
         */
        boolean allowKeyPress(Screen screen, char chr, int modifiers);
    }

    interface CharTyped {
        /**
         * Checks if a key should be allowed to be pressed.
         */
        void keyPressed(Screen screen, char chr, int modifiers);
    }

}
