package dev.morazzer.cookies.mod.config.screen;

import net.minecraft.util.Identifier;

/**
 * Constants for the option screen.
 */
public interface InventoryConfigScreenConstants {
    /**
     * The background texture of the screen.
     */
    Identifier BACKGROUND_TEXTURE =
        Identifier.of("cookies-mod", "textures/gui/config/config_background.png");
    /**
     * The width of the background texture.
     */
    int BACKGROUND_WIDTH = 195;
    /**
     * The height of the background texture.
     */
    int BACKGROUND_HEIGHT = 195;

}
