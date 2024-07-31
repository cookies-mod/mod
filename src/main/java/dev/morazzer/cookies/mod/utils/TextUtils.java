package dev.morazzer.cookies.mod.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Utility class for better text creation.
 */
public class TextUtils {

    /**
     * Creates a new literal text without italics.
     *
     * @param text The literal string.
     * @return The text.
     */
    public static MutableText literal(String text) {
        return Text.literal(text).setStyle(Style.EMPTY.withItalic(false));
    }

    /**
     * Creates a new literal text without italics.
     *
     * @param text       The literal string.
     * @param formatting The formatting of the text.
     * @return The text.
     */
    public static MutableText literal(String text, Formatting formatting) {
        return Text.literal(text).setStyle(Style.EMPTY.withItalic(false)).formatted(formatting);
    }

    /**
     * Creates a new literal text without italics.
     *
     * @param text  The literal string.
     * @param color The color of the text.
     * @return The text.
     */
    public static MutableText literal(String text, int color) {
        return Text.literal(text).setStyle(Style.EMPTY.withItalic(false)).withColor(color);
    }

}
