package dev.morazzer.cookies.mod.utils.skyblock.inventories;

import java.util.Optional;
import net.minecraft.item.ItemStack;

/**
 * Contents of a {@link ClientSideInventory} to make interacting/programming easier.
 */
public class InventoryContents {

    protected final ItemStack[][] items;
    protected final int rows;
    protected final int columns;
    private final ClientSideInventory clientSideInventory;

    /**
     * Creates a new content instance.
     * @param rows The amount of rows in the inventory.
     * @param clientSideInventory The screen representing the inventory.
     */
    public InventoryContents(int rows, ClientSideInventory clientSideInventory) {
        this.clientSideInventory = clientSideInventory;

        this.rows = rows;
        this.columns = 9;
        this.items = new ItemStack[this.rows][this.columns];
    }

    /**
     * Adds an item in the first empty slot.
     * @param item The item to add.
     */
    public void add(ItemStack item) {
        Optional<Position> position = firstEmpty();
        position.ifPresent(value -> set(value, item));
    }

    /**
     * Gets the first empty slot.
     * @return The first empty slot.
     */
    public Optional<Position> firstEmpty() {
        for (int row = 0; row < items.length; row++) {
            for (int column = 0; column < items[row].length; column++) {
                if (get(new Position(row, column)).isEmpty()) {
                    return Optional.of(new Position(row, column));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Sets the item at the provided position.
     * @param position The position.
     * @param item The item.
     */
    public void set(Position position, ItemStack item) {
        if (position.row() < 0 || position.row() >= rows || position.column() < 0 || position.column() >= columns) {
            throw new IllegalArgumentException(
                "Position " + position.row() + "x" + position.column() + " is out of bounds of inventory with size " +
                rows + "x" + columns);
        }

        items[position.row()][position.column()] = item;
        update(position, item);
    }

    /**
     * Gets the item at the requested position.
     * @param position The position.
     * @return The item.
     */
    public Optional<ItemStack> get(Position position) {
        if (position.row() < 0 || position.row() >= rows || position.column() < 0 || position.column() >= columns) {
            throw new IllegalArgumentException(
                "Position " + position.row() + "x" + position.column() + " is out of bounds of inventory with size " +
                rows + "x" + columns);
        }

        return Optional.ofNullable(items[position.row()][position.column()]);
    }

    private void update(Position position, ItemStack itemStack) {
        this.clientSideInventory.setSlot(position.row(), position.column(), itemStack);
    }

    /**
     * Removes the item from the position.
     * @param position The position to remove the item from.
     */
    public void remove(Position position) {
        if (position.row() < 0 || position.row() >= rows || position.column() < 0 || position.column() >= columns) {
            throw new IllegalArgumentException(
                "Position " + position.row() + "x" + position.column() + " is out of bounds of inventory with size " +
                rows + "x" + columns);
        }

        items[position.row()][position.column()] = null;
        update(position, null);
    }

    /**
     * Fills the whole inventory with one item.
     * @param item The item.
     */
    public void fill(ItemStack item) {
        for (int row = 0; row < items.length; row++) {
            for (int column = 0; column < items[row].length; column++) {
                set(new Position(row, column), item);
            }
        }
    }

    /**
     * Gets an array of all items.
     * @return The items.
     */
    public ItemStack[][] items() {
        return items;
    }

    /**
     * Gets the amount of rows.
     */
    public int rows() {
        return this.rows;
    }

    /**
     * Gets the amount of columns.
     */
    public int columns() {
        return this.columns;
    }

    /**
     * Fills the entire row with the item.
     * @param row The row to fill.
     * @param item The item.
     */
    public void fillRow(int row, ItemStack item) {
        for (int column = 0; column < this.columns; column++) {
            set(new Position(row, column), item);
        }
    }

    /**
     * Fills the entire column with an item.
     * @param column The column to fill.
     * @param item The item.
     */
    public void fillColumn(int column, ItemStack item) {
        for (int row = 0; row < this.rows; row++) {
            set(new Position(row, column), item);
        }
    }

    /**
     * Fills all borders (most outer slot) with an item.
     * @param item The item.
     */
    public void fillBorders(ItemStack item) {
        fillRow(0, item);
        fillRow(this.rows - 1, item);
        fillColumn(0, item);
        fillColumn(this.columns - 1, item);
    }

    /**
     * Fills a rectangle inside the screen.
     * @param from The starting position (inclusive).
     * @param to The ending position (inclusive).
     * @param item The item to set.
     */
    public void fillRectangle(Position from, Position to, ItemStack item) {
        for (int row = from.row(); row <= to.row(); row++) {
            for (int column = from.column(); column <= to.column(); column++) {
                set(new Position(row, column), item);
            }
        }
    }
}
