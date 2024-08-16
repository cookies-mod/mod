package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.generated.utils.ItemAccessor;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@SuppressWarnings("MissingJavadoc")
public class StackCountContext {
    private static final ItemSources[] ITEM_SOURCES = {ItemSources.INVENTORY, ItemSources.SACKS, ItemSources.STORAGE};

    Map<RepositoryItem, Long> itemMap = new HashMap<>();
    Stack<Integer> integers = new Stack<>();
    private final ItemSources[] itemSources;

    public StackCountContext(ItemSources... itemSources) {
        integers.push(0);
        this.itemSources = itemSources;
    }

    public StackCountContext() {
        this(ITEM_SOURCES);
    }

    public int take(RepositoryItem id, int max) {
        if (id == null) {
            return 0;
        }

        if (!itemMap.containsKey(id)) {
            itemMap.put(
                id,
                ItemSources.getItems(itemSources)
                    .stream()
                    .filter(item -> id.equals(ItemAccessor.repositoryItemOrNull(item.itemStack())))
                    .mapToLong(Item::amount)
                    .sum());
        }

        long l = itemMap.getOrDefault(id, 0L);
        int used = (int) (l >> 32);
        int total = (int) l;
        int available = total - used;
        if (max >= available) {
            itemMap.put(id, ((long) total << 32) | total);
            return available;
        }

        used += max;
        l = l & 0xFFFFFFFFL;
        l |= (long) used << 32;
        itemMap.put(id, l);
        return max;
    }
}
