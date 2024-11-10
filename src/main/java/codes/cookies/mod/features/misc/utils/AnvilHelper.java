package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

/**
 * Highlights items that fit together in the anvil, at the moment only for books.
 */
public class AnvilHelper {

    @SuppressWarnings("MissingJavadocs")
    public AnvilHelper() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GenericContainerScreen genericContainerScreen) {
                if (!SkyblockUtils.isCurrentlyInSkyblock()) {
                    return;
                }
                if (!ConfigManager.getConfig().helpersConfig.anvilHelper.getValue()) {
                    return;
                }
                if (!screen.getTitle().getString().trim().equals("Anvil")) {
                    return;
                }

                InventoryContentUpdateEvent.register(genericContainerScreen.getScreenHandler(), this::update);
                ScreenEvents.remove(genericContainerScreen).register(this::resetItems);
            }
        });
    }

    private void update(int index, ItemStack itemStack) {
        if (index != 29 && index != 33) {
            return;
        }
        final String data = ItemUtils.getData(itemStack, CookiesDataComponentTypes.SKYBLOCK_ID);
        if (data == null) {
            return;
        }
        final String secondLineOrNull = getSecondLineOrNull(itemStack);
        if (secondLineOrNull == null) {
            return;
        }
        if (itemStack.getItem() != Items.ENCHANTED_BOOK) {
            return;
        }

        final PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        for (int i = 0; i < inventory.size() - 1; i++) {
            final ItemStack otherStack = inventory.getStack(i);
            final String other = ItemUtils.getData(otherStack, CookiesDataComponentTypes.SKYBLOCK_ID);
            if (other == null) {
                continue;
            }
            if (!data.equals(other)) {
                continue;
            }
            if (otherStack.getItem() != Items.ENCHANTED_BOOK) {
                continue;
            }
            final String otherLine = getSecondLineOrNull(otherStack);
            if (!secondLineOrNull.equals(otherLine)) {
                continue;
            }
            otherStack.set(MiscDataComponentTypes.ANVIL_HELPER_MODIFIED, true);
            otherStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, new ItemStack(Items.KNOWLEDGE_BOOK));
        }
    }

    private void resetItems(Screen screen) {
        for (Slot slot : MinecraftClient.getInstance().player.playerScreenHandler.slots) {
            final Boolean data = ItemUtils.getData(slot.getStack(), MiscDataComponentTypes.ANVIL_HELPER_MODIFIED);
            if (data == null || !data) {
                return;
            }
            slot.getStack().remove(MiscDataComponentTypes.ANVIL_HELPER_MODIFIED);
            slot.getStack().remove(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM);
        }
    }

    private String getSecondLineOrNull(ItemStack itemStack) {
        final LoreComponent lore = ItemUtils.getData(itemStack, DataComponentTypes.LORE);
        if (lore == null || lore.lines().size() == 1) {
            return null;
        }
        return lore.lines().getFirst().getString();
    }
}
