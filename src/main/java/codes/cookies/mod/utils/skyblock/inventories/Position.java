package codes.cookies.mod.utils.skyblock.inventories;

/**
 * A utility class for easy coordinates in inventories.
 * @param row The row in the inventory.
 * @param column The column in the inventory.
 */
public record Position(int row, int column) {
    /**
     * Creates a new instance with the item index.
     * @param index The index of the item.
     */
    public Position(int index) {
        this(index / 9, index % 9);
    }

    /**
     * Adds another position to this.
     * @param other The position to add.
     * @return The combined position.
     */
    public Position add(Position other) {
        return new Position(row + other.row, column + other.column);
    }
}
