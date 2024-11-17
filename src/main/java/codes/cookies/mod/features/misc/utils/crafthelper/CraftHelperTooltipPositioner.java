package codes.cookies.mod.features.misc.utils.crafthelper;

import codes.cookies.mod.utils.accessors.InventoryScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Tooltip positioner for craft helper instances.
 */
public class CraftHelperTooltipPositioner implements TooltipPositioner {

    public static CraftHelperTooltipPositioner INSTANCE = new CraftHelperTooltipPositioner();

    @Override
    public Vector2ic getPosition(int screenWidth, int screenHeight, int x, int y, int width, int height) {
        Vector2ic position = new Vector2i(x,y);
        if (!(MinecraftClient.getInstance().currentScreen instanceof InventoryScreenAccessor accessor)) {
            return position;
        }
        final int screenX = accessor.cookies$getX();
        final int screenBackgroundWidth = accessor.cookies$getBackgroundWidth();

        if (position.x() + width > screenX && position.x() < screenX) {
            position = new Vector2i(screenX - width - 8, position.y());
        }
        if (position.x() > screenX && position.x() < screenX + screenBackgroundWidth) {
            position = new Vector2i(screenX + screenBackgroundWidth + 8, position.y());
        }
        if (position.x() + width > screenWidth) {
            position = new Vector2i(screenWidth - width - 8, position.y());
        }
        if (position.x() == 0) {
            position = new Vector2i(8, position.y());
        }
        return position;
    }

}
