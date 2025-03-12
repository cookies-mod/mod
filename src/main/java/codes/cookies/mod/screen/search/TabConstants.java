package codes.cookies.mod.screen.search;

import net.minecraft.util.Identifier;

/**
 * Constants for inventory tabs.
 */
public interface TabConstants {
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
     * The width of one tab texture.
     */
    int ITEM_TAB_WIDTH = 27;
    int ITEM_TAB_HEIGHT = 32;
}
