package codes.cookies.mod.features.farming.inventory;

import codes.cookies.mod.config.categories.FarmingCategory;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
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
            if (!FarmingCategory.showRancherSpeed) {
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
        final int ranchersSpeed = customData.getNbt().getInt("ranchers_speed", 0);

        itemStack.set(CookiesDataComponentTypes.CUSTOM_SLOT_TEXT, String.valueOf(ranchersSpeed));
    }

}
