package dev.morazzer.cookies.mod.utils.skyblock.inventories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Optional;

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
	private final List<ItemStack> firstItems;
	private int columns;
	private int rows;
	@Getter
	private int maxPage;
	@Getter
	@Setter
	private int currentPage = 1;
	private int firstItemSize;
	private boolean isDirty;

	/**
	 * Creates a new pagination instance.
	 *
	 * @param items     The items to add.
	 * @param from      The starting position of the pagination (inclusive).
	 * @param to        The end position of the pagination (inclusive).
	 * @param firstItems The first item (or null).
	 */
	public Pagination(List<ItemStack> items, Position from, Position to, List<ItemStack> firstItems) {
		this.items = new ArrayList<>(items);
		this.from = from;
		this.to = to;
		this.firstItems = Optional.ofNullable(firstItems).orElse(Collections.emptyList());
		this.update();
	}

	/**
	 * Switches to the next page, if available.
	 */
	public void nextPage() {
		if (this.currentPage < this.maxPage) {
			this.currentPage++;
			this.isDirty = true;
		}
	}

	/**
	 * Switches to the previous page, if available.
	 */
	public void previousPage() {
		if (this.currentPage > 1) {
			this.currentPage--;
			this.isDirty = true;
		}
	}

	/**
	 * Sets all items for the inventory contents.
	 *
	 * @param contents The inventory contents.
	 */
	public void setItems(InventoryContents contents) {
		for (int i = 0; i < this.rows * this.columns; i++) {
			if (i + (this.rows * this.columns * (this.currentPage - 1)) - this.firstItemSize >= this.items.size()) {
				break;
			}
			if (this.currentPage == 1 && !this.firstItems.isEmpty() && this.firstItemSize < i) {
				contents.set(this.from, this.firstItems.get(i));
				continue;
			}
			ItemStack item =
					this.items.get(i + (this.rows * this.columns * (this.currentPage - 1)) - this.firstItemSize);
			contents.set(this.from.add(new Position(i / this.columns, i % this.columns)), item);
		}
		this.isDirty = false;
	}

	/**
	 * Updates all cached values and marks as dirty.
	 */
	public void update() {
		this.rows = Math.max(this.from.row(), this.to.row()) - Math.min(this.from.row(), this.to.row()) + 1;
		this.columns =
				Math.max(this.from.column(), this.to.column()) - Math.min(this.from.column(), this.to.column()) + 1;
		this.maxPage = (this.items.size() + (this.firstItems.size())) / (this.rows * this.columns) + 1;
		this.firstItemSize = this.firstItems.size();
		this.isDirty = true;
	}

	/**
	 * Removes an item from the item list.
	 * @param item The item to remove.
	 */
	public void removeItem(ItemStack item) {
		this.items.remove(item);
		this.update();
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
		return this.currentPage < this.maxPage;
	}
}
