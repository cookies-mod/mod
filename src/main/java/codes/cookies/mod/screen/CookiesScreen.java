package codes.cookies.mod.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Common code for all screens used in the mod.
 */
public abstract class CookiesScreen extends Screen {
    protected CookiesScreen(Text title) {
        super(title);
    }

    /**
     * Checks if a coordinate is within the bounds of a box.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param regionX The start x of the box.
     * @param regionY The start y of the box.
     * @param regionWidth The width of the box.
     * @param regionHeight The height of the box.
     * @return Whether the position is within the bounds.
     */
    public static boolean isInBound(
        final int x, final int y, final int regionX, final int regionY, final int regionWidth, final int regionHeight) {
        return (x >= regionX && x <= regionX + regionWidth) && (y >= regionY && y <= regionY + regionHeight);
    }
}
