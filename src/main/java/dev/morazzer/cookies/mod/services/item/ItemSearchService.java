package dev.morazzer.cookies.mod.services.item;

import java.util.function.Predicate;

import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemCompound;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.items.sources.CraftableItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.StorageItemSource;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.services.item.search.ExactItemMatch;
import dev.morazzer.cookies.mod.services.item.search.IsSameMatch;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.text.Text;

public class ItemSearchService {


	public static void handleCraftable(CraftableItemSource.Data data) {
		if (data.hasAllIngredients()) {
			sendCommand("viewrecipe " + data.output().getId());
		} else {
			CraftHelperManager.pushNewCraftHelperItem(data.output().getRepositoryItemNotNull(), 1);
		}
	}

	/**
	 * Opens the storage at the page of the provided data.
	 *
	 * @param data The data.
	 */
	public static void openStorage(StorageItemSource.Context data) {
		final String command = switch (data.location()) {
			case BACKPACK -> "bp";
			case ENDER_CHEST -> "ec";
		};
		sendCommand(command + " " + (data.page() + 1));
	}

	/**
	 * Performs the actions associated with the compound, if items are from the same source it will perform an item
	 * specific action.
	 *
	 * @param itemCompound The item compound.
	 */
	public static boolean performActions(ItemCompound itemCompound) {
		if (!LocationUtils.Island.PRIVATE_ISLAND.isActive() &&
				(itemCompound.type() == ItemCompound.CompoundType.MULTIPLE ||
						itemCompound.type() == ItemCompound.CompoundType.CHEST)) {
			if (itemCompound.type() == ItemCompound.CompoundType.CHEST) {
				CookiesUtils.sendFailedMessage(Text.literal("You need to be on your island to highlight chests!"));
				return false;
			}
			final Item<?>[] array = itemCompound.items()
					.stream()
					.filter(Predicate.not(item -> item.source() == ItemSources.CHESTS))
					.toArray(Item[]::new);
			if (array.length == 0) {
				return false;
			}
			itemCompound = new ItemCompound(array[0]);
			for (int i = 1; i < array.length; i++) {
				itemCompound.add(array[i]);
			}
		}
		if (itemCompound.type() == ItemCompound.CompoundType.MULTIPLE ||
				itemCompound.type() == ItemCompound.CompoundType.CHEST) {

			ItemHighlightService.setActive(new IsSameMatch(itemCompound.itemStack()));
			ItemHighlightService.highlightAll(ItemServices.extractChestPositions(itemCompound));
			return true;
		}
		return performAction(itemCompound.type(), itemCompound.data(), itemCompound.items().iterator().next());
	}

	/**
	 * Executes a command as the player.
	 *
	 * @param command The command to execute.
	 */
	public static void sendCommand(String command) {
		CookiesUtils.sendCommand(command);
	}

	/**
	 * Performs whatever action is associated with the provided parameters.
	 *
	 * @param type The type of action.
	 * @param data The data related to the action.
	 * @param item The item which is subject of the action.
	 */
	public static boolean performAction(ItemCompound.CompoundType type, Object data, Item<?> item) {
		if (!LocationUtils.Island.PRIVATE_ISLAND.isActive() && type == ItemCompound.CompoundType.CHEST_POS) {
			CookiesUtils.sendFailedMessage(Text.literal("You need to be on your island to highlight chests!"));
			return false;
		}
		ItemHighlightService.setActive(new ExactItemMatch(item.itemStack()));
		final RepositoryItem repositoryItem = item.itemStack().get(CookiesDataComponentTypes.REPOSITORY_ITEM);
		final int color = ItemServices.getColor(repositoryItem);
		switch (type) {
			case CHEST_POS -> ItemHighlightService.highlightChests(item, color);
			case STORAGE_PAGE -> openStorage((StorageItemSource.Context) data);
			case STORAGE -> sendCommand("storage");
			case SACKS, SACK_OF_SACKS -> sendCommand("sacks");
			case ACCESSORIES -> sendCommand("accessorybag");
			case POTION_BAG -> sendCommand("potionbag");
			case VAULT -> sendCommand("bank");
			case CRAFTABLE -> {
				handleCraftable((CraftableItemSource.Data) data);
				return false;
			}
		}
		return true;
	}
}
