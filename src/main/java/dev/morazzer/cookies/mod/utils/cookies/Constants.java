package dev.morazzer.cookies.mod.utils.cookies;

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
        /**
         * Emoji to indicate a changeable/changed value.
         */
        String PEN = "✎";
        /**
         * Emoji to indicate a box that might have a value.
         */
        String EMPTY_BOX = "☐";
        /**
         * Emoji to indicate a true value.
         */
        String CHECKED_BOX = "☑";
        /**
         * Emoji to indicate a checked value.
         */
        String CROSSED_BOX = "☒";
        /**
         * Emoji to indicate a repetition.
         */
        String REPEAT_ARROW = "\uD83D\uDD01";
		String MOVE =  "✥";
	}

}
