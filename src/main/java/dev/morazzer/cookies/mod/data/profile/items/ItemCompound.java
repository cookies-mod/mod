package dev.morazzer.cookies.mod.data.profile.items;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.item.ItemStack;

/**
 * Creates an item compound to store multiple {@link Item} at once.
 */
public final class ItemCompound {
    private final ItemStack itemStack;
    private final Set<Item<?>> items;
    private int amount;

    public ItemCompound(Item<?>... items) {
        this(Arrays.stream(items).mapToInt(Item::amount).sum(), items[0].itemStack(), new HashSet<>(Set.of(items)));
    }

    public ItemCompound(int amount, ItemStack itemStack, Set<Item<?>> items) {
        this.amount = amount;
        this.itemStack = itemStack;
        this.items = items;
    }

    public void add(Item<?> item) {
        if (items.contains(item)) {
            return;
        }
        items.add(item);
        amount += item.amount();
    }

    public int amount() {return amount;}

    public ItemStack itemStack() {return itemStack;}

    public Set<Item<?>> items() {return items;}

    @Override
    public int hashCode() {
        return Objects.hash(amount, itemStack, items);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ItemCompound) obj;
        return this.amount == that.amount && Objects.equals(this.itemStack, that.itemStack) &&
               Objects.equals(this.items, that.items);
    }

    @Override
    public String toString() {
        return "ItemCompound[" + "amount=" + amount + ", " + "itemStack=" + itemStack + ", " + "items=" + items + ']';
    }


}
