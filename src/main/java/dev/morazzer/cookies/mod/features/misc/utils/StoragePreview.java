package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.sub.StorageData;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemTooltipComponent;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Allows for previewing the contents of the storage inventories.
 */
public class StoragePreview {

    @SuppressWarnings("MissingJavadoc")
    public StoragePreview() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof GenericContainerScreen genericContainerScreen)) {
                return;
            }
            if (!SkyblockUtils.isCurrentlyInSkyblock()) {
                return;
            }
            if (!ConfigManager.getConfig().miscConfig.enableStoragePreview.getValue()) {
                return;
            }
            if (!genericContainerScreen.getTitle().getString().equals("Storage")) {
                return;
            }

            InventoryContentUpdateEvent.register(genericContainerScreen.getScreenHandler(),
                ExceptionHandler.wrap(this::updateInventory));
        });
    }

    private void updateInventory(int index, ItemStack itemStack) {
        final String name = itemStack.getName().getString();
        StorageData.StorageLocation location = StorageData.StorageLocation.BACKPACK;
        if (name.startsWith("Ender Chest Page")) {
            location = StorageData.StorageLocation.ENDER_CHEST;
        } else if (!name.startsWith("Backpack Slot")) {
            return;
        }

        int page = index - (location == StorageData.StorageLocation.ENDER_CHEST ? 9 : 27);
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            return;
        }
        final List<StorageData.StorageItem> items = currentProfile.get().getStorageData().getItems(page, location);
        if (items == null || items.isEmpty()) {
            return;
        }

        Map<Integer, ItemStack> map = new HashMap<>();
        for (StorageData.StorageItem item : items) {
			final ItemStack copy = item.itemStack().copy();
			map.put(item.slot(), copy);
			if (copy.contains(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED)) {
				if ( item.itemStack().getItem() == Items.PURPLE_STAINED_GLASS_PANE) {
					continue;
				}
				itemStack.set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR, copy.get(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR));
				itemStack.set(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED, 0);
				ItemSearchService.add(itemStack);
				ItemSearchService.add(copy);
			}
        }
        itemStack.set(CookiesDataComponentTypes.LORE_ITEMS, new ItemTooltipComponent(map));
    }
}
