package codes.cookies.mod.utils.accessors;

import java.util.List;
import net.minecraft.client.gui.screen.Screen;

/**
 * Accessor for some useful handled screen variables.
 */
public interface InventoryScreenAccessor {

    static int getBackgroundWidth(Screen screen) {
        return ((InventoryScreenAccessor) screen).cookies$getBackgroundWidth();
    }

    static int getBackgroundHeight(Screen screen) {
        return ((InventoryScreenAccessor) screen).cookies$getBackgroundHeight();
    }

    static int getX(Screen screen) {
        return ((InventoryScreenAccessor) screen).cookies$getX();
    }

    static int getY(Screen screen) {
        return ((InventoryScreenAccessor) screen).cookies$getY();
    }

    static void setDisabled(Screen screen, Disabled disabled) {
        ((InventoryScreenAccessor) screen).cookies$setDisable(disabled);
    }

    static boolean isDisabled(Screen screen, Disabled disabled) {
        return ((InventoryScreenAccessor) screen).cookies$getDisabled().contains(disabled);
    }

    int cookies$getBackgroundWidth();

    int cookies$getBackgroundHeight();

    int cookies$getX();

    int cookies$getY();


    default void cookies$setDisable(Disabled disabled) {
        cookies$getDisabled().add(disabled);
    }

    List<Disabled> cookies$getDisabled();

    enum Disabled {
        CRAFT_HELPER;
    }
}
