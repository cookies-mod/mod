package dev.morazzer.cookies.mod.utils.accessors;

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

    int cookies$getBackgroundWidth();
    int cookies$getBackgroundHeight();

    int cookies$getX();
    int cookies$getY();

}
