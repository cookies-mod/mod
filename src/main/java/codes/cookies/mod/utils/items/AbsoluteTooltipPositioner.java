package codes.cookies.mod.utils.items;

import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * TooltipPositioner without any restrictions on width and height.
 */
public class AbsoluteTooltipPositioner implements TooltipPositioner {
    public static final AbsoluteTooltipPositioner INSTANCE = new AbsoluteTooltipPositioner();
    @Override
    public Vector2ic getPosition(int screenWidth, int screenHeight, int x, int y, int width, int height) {
        return new Vector2i(x, y).add(12, -12);
    }
}
