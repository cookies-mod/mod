package codes.cookies.mod.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Utility class for better text creation.
 */
public class TextUtils {
    /**
     * Creates a new translatable text without italics.
     *
     * @param key The key of the text.
     * @return The text.
     */
    public static MutableText translatable(String key) {
        return Text.translatable(key);
    }

    /**
     * Creates a new translatable text without italics.
     *
     * @param key The key of the text.
     * @return The text.
     */
    public static MutableText translatableWithKeys(String key, Object... args) {
        return Text.translatable(key, args);
    }

    /**
     * Creates a new translatable text without italics.
     *
     * @param key        The key of the text.
     * @param formatting The formatting of the text.
     * @return The text.
     */
    public static MutableText translatable(String key, Formatting formatting) {
        return Text.translatable(key).setStyle(Style.EMPTY.withItalic(false)).formatted(formatting);
    }

    /**
     * Creates a new translatable text without italics.
     *
     * @param key   The key of the text.
     * @param color The color of the text.
     * @return The text.
     */
    public static MutableText translatable(String key, int color) {
        return Text.translatable(key).setStyle(Style.EMPTY.withItalic(false)).withColor(color);
    }

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
