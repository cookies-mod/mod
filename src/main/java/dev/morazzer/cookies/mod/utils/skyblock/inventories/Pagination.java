package dev.morazzer.cookies.mod.utils.skyblock.inventories;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

/**
 * Utility class to allow for easy pagination of items.
 */
public class Pagination {

    private final List<ItemStack> items;
    @Getter
    private final Position from;
    @Getter
    private final Position to;
    private final ItemStack firstItem;
    private final int columns;
    private final int rows;
    @Getter
    private final int maxPage;
    @Getter
    @Setter
    private int currentPage = 1;
    private final int firstItemSize;
    private boolean isDirty;

    /**
     * Creates a new pagination instance.
     * @param items The items to add.
     * @param from The starting position of the pagination (inclusive).
     * @param to The end position of the pagination (inclusive).
     * @param firstItem The first item (or null).
     */
    public Pagination(List<ItemStack> items, Position from, Position to, ItemStack firstItem) {
        this.items = items;
        this.from = from;
        this.to = to;
        this.firstItem = firstItem;

        rows = Math.max(from.row(), to.row()) - Math.min(from.row(), to.row()) + 1;
        columns = Math.max(from.column(), to.column()) - Math.min(from.column(), to.column()) + 1;
        maxPage = (items.size() + (firstItem != null ? 1 : 0)) / (rows * columns) + 1;
        firstItemSize = firstItem != null ? 1 : 0;
        this.isDirty = true;
    }

    /**
     * Switches to the next page, if available.
     */
    public void nextPage() {
        if (currentPage < maxPage) {
            currentPage++;
            this.isDirty = true;
        }
    }

    /**
     * Switches to the previous page, if available.
     */
    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            this.isDirty = true;
        }
    }

    /**
     * Sets all items for the inventory contents.
     * @param contents The inventory contents.
     */
    public void setItems(InventoryContents contents) {
        for (int i = 0; i < rows * columns; i++) {
            if (i + (rows * columns * (currentPage - 1)) - firstItemSize >= items.size()) {
                break;
            }
            if (i == 0 && currentPage == 1 && firstItem != null) {
                contents.set(from, firstItem);
                continue;
            }
            ItemStack item = items.get(i + (rows * columns * (currentPage - 1)) - firstItemSize);
            contents.set(from.add(new Position(i / columns, i % columns)), item);
        }
        this.isDirty = false;
    }

    /**
     * @return Whether the pagination should be updated or not.
     */
    public boolean isDirty() {
        return this.isDirty;
    }

    /**
     * @return Whether the pagination has a previous page or not.
     */
    public boolean hasPreviousPage() {
        return this.currentPage > 1;
    }

    /**
     * @return Whether the pagination has a next page or not.
     */
    public boolean hasNextPage() {
        return this.currentPage < maxPage;
    }
}
