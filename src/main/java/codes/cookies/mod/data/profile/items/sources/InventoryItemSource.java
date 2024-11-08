package codes.cookies.mod.data.profile.items.sources;

import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSource;
import codes.cookies.mod.data.profile.items.ItemSources;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

/**
 * Item source for the player inventory.
 */
public class InventoryItemSource implements ItemSource<Integer> {

    @Getter
    private static final InventoryItemSource instance = new InventoryItemSource();

    @Override
    public Collection<Item<?>> getAllItems() {
        Set<Item<?>> items = new HashSet<>();
		if (MinecraftClient.getInstance().player == null) {
			return items;
		}
        final PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        for (int i = 0; i < inventory.size() - 1; i++) {
            final ItemStack stack = inventory.getStack(i);
            items.add(new Item<>(stack, ItemSources.INVENTORY, stack.getCount(), i));
        }
        return items;
    }

    @Override
    public ItemSources getType() {
        return ItemSources.INVENTORY;
    }

	@Override
	public void remove(Item<?> item) {
		// won't work on inventory
	}
}
