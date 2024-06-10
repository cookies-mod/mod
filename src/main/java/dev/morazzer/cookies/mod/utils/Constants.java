package dev.morazzer.cookies.mod.utils;

/**
 * Various constants used throughout the mod.
 */
public interface Constants {
    /**
     * The prefix that is appended before mod messages.
     */
    String PREFIX = "Cookies Mod ⋙ ";
    /**
     * The main color that is used.
     */
    int MAIN_COLOR = 0xFFE99DBE;
    /**
     * The color used for failed actions.
     */
    int FAIL_COLOR = 0xFFFF6961;
    /**
     * The color used for successful actions.
     */
    int SUCCESS_COLOR = 0xFF77DD77;

    /**
     * Various emojis that are used.
     */
    interface Emojis {
        /**
         * Emoji to indicate a success of true value.
         */
        String YES = "✔";
        /**
         * Emoji to indicate a failure or false value.
         */
        String NO = "❌";
        /**
         * Emoji to indicate a warning.
         */
        String WARNING = "⚠";
        /**
         * Emoji to indicate a not achieved goal.
         */
        String FLAG_EMPTY = "⚐";
        /**
         * Emoji to indicate an achieved goal.
         */
        String FLAG_FILLED = "⚑";
    }

}
