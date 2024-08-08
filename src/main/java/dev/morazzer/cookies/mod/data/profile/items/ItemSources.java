package dev.morazzer.cookies.mod.data.profile.items;

import dev.morazzer.cookies.mod.data.profile.items.sources.InventoryItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.IslandChestItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.SackItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.StorageItemSource;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.Text;

/**
 * A list of all supported item sources.
 */
@AllArgsConstructor
@Getter
public enum ItemSources {

    CHESTS(Text.literal("Chests"), IslandChestItemSource.getInstance()),
    STORAGE(Text.literal("Storage"), StorageItemSource.getInstance()),
    SACKS(Text.literal("Sacks"), SackItemSource.getInstance()),
    INVENTORY(Text.literal("Inventory"), InventoryItemSource.getInstance());

    private final Text name;
    private final ItemSource<?> itemSource;
    public static Collection<Item<?>> getItems() {
        return getItems(ItemSources.values());
    }
    public static Collection<Item<?>> getItems(ItemSources... itemSources) {
        Set<Item<?>> items = new HashSet<>();
        for (ItemSources itemSource : itemSources) {
            items.addAll(itemSource.itemSource.getAllItems());
        }
        return items;
    }

}
