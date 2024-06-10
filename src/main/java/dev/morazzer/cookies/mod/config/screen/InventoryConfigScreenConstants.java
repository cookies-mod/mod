package dev.morazzer.cookies.mod.config.screen;

import net.minecraft.util.Identifier;

/**
 * Constants for the option screen.
 */
public interface InventoryConfigScreenConstants {

    /**
     * The scroll bar texture.
     */
    Identifier SCROLLER_TEXTURE = Identifier.of("container/creative_inventory/scroller");
    /**
     * The disabled scroll bar texture.
     */
    Identifier SCROLLER_DISABLED_TEXTURE = Identifier.of("container/creative_inventory/scroller_disabled");
    /**
     * An array of unselected tab textures for the top row.
     */
    Identifier[] TAB_TOP_UNSELECTED_TEXTURES =
        new Identifier[] {Identifier.of("container/creative_inventory/tab_top_unselected_1"),
            Identifier.of("container/creative_inventory/tab_top_unselected_2"),
            Identifier.of("container/creative_inventory/tab_top_unselected_3"),
            Identifier.of("container/creative_inventory/tab_top_unselected_4"),
            Identifier.of("container/creative_inventory/tab_top_unselected_5"),
            Identifier.of("container/creative_inventory/tab_top_unselected_6"),
            Identifier.of("container/creative_inventory/tab_top_unselected_7")};
    /**
     * An array of selected tab textures for the top row.
     */
    Identifier[] TAB_TOP_SELECTED_TEXTURES =
        new Identifier[] {Identifier.of("container/creative_inventory/tab_top_selected_1"),
            Identifier.of("container/creative_inventory/tab_top_selected_2"),
            Identifier.of("container/creative_inventory/tab_top_selected_3"),
            Identifier.of("container/creative_inventory/tab_top_selected_4"),
            Identifier.of("container/creative_inventory/tab_top_selected_5"),
            Identifier.of("container/creative_inventory/tab_top_selected_6"),
            Identifier.of("container/creative_inventory/tab_top_selected_7")};
    /**
     * An array of unselected tab textures for the bottom row.
     */
    Identifier[] TAB_BOTTOM_UNSELECTED_TEXTURES =
        new Identifier[] {Identifier.of("container/creative_inventory/tab_bottom_unselected_1"),
            Identifier.of("container/creative_inventory/tab_bottom_unselected_2"),
            Identifier.of("container/creative_inventory/tab_bottom_unselected_3"),
            Identifier.of("container/creative_inventory/tab_bottom_unselected_4"),
            Identifier.of("container/creative_inventory/tab_bottom_unselected_5"),
            Identifier.of("container/creative_inventory/tab_bottom_unselected_6"),
            Identifier.of("container/creative_inventory/tab_bottom_unselected_7")};
    /**
     * An array of selected tab textures for the bottom row.
     */
    Identifier[] TAB_BOTTOM_SELECTED_TEXTURES =
        new Identifier[] {Identifier.of("container/creative_inventory/tab_bottom_selected_1"),
            Identifier.of("container/creative_inventory/tab_bottom_selected_2"),
            Identifier.of("container/creative_inventory/tab_bottom_selected_3"),
            Identifier.of("container/creative_inventory/tab_bottom_selected_4"),
            Identifier.of("container/creative_inventory/tab_bottom_selected_5"),
            Identifier.of("container/creative_inventory/tab_bottom_selected_6"),
            Identifier.of("container/creative_inventory/tab_bottom_selected_7")};
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
    /**
     * The width of one tab texture.
     */
    int ITEM_TAB_WIDTH = 27;

}
