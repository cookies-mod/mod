package dev.morazzer.cookies.mod.render.utils;

/**
 * Various helper methods for rendering things.
 */
public class RenderHelper {

    /**
     * Converts the color from 0-255 to 0-1.
     *
     * @param color The color.
     * @return The value between 0 and 1.
     */
    public static float wrapZeroOne(int color) {
        return color / 255f;
    }

    /**
     * Gets the red value from a rgb color.
     *
     * @param color The color.
     * @return The red value.
     */
    public static int getRed(int color) {
        return color >> 16 & 0xFF;
    }

    /**
     * Gets the green value from a rgb color.
     *
     * @param color The color.
     * @return The green value.
     */
    public static int getGreen(int color) {
        return color >> 8 & 0xFF;
    }

    /**
     * Gets the blue value from a rgb color.
     *
     * @param color The color.
     * @return The blue value.
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }

    /**
     * Gets the alpha value from an argb color.
     *
     * @param color The color.
     * @return The alpha value.
     */
    public static int getAlpha(int color) {
        return color >> 24 & 0xFF;
    }

}