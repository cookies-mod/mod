package dev.morazzer.cookies.mod.features.farming.inventory;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;

/**
 * Displays the speed of rancher boots as the item stack size.
 */
public class RancherBootsNumbers {
    private static final RepositoryItem RANCHERS_BOOTS = RepositoryItem.of("ranchers_boots");

    @SuppressWarnings("MissingJavadoc")
    public RancherBootsNumbers() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?> handledScreen)) {
                return;
            }
            if (!SkyblockUtils.isCurrentlyInSkyblock()) {
                return;
            }
            if (!ConfigManager.getConfig().farmingConfig.showRancherSpeed.getValue()) {
                return;
            }

            InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this::updateInventory);
        });
    }

    private void updateInventory(int i, ItemStack itemStack) {
        final RepositoryItem data = ItemUtils.getData(itemStack, CookiesDataComponentTypes.REPOSITORY_ITEM);
        if (data != RANCHERS_BOOTS) {
            return;
        }

        final NbtComponent customData = ItemUtils.getData(itemStack, DataComponentTypes.CUSTOM_DATA);
        if (customData == null || !customData.contains("ranchers_speed")) {
            return;
        }
        final int ranchersSpeed = customData.getNbt().getInt("ranchers_speed");

        itemStack.set(CookiesDataComponentTypes.CUSTOM_SLOT_TEXT, String.valueOf(ranchersSpeed));
    }

}
