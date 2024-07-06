package dev.morazzer.cookies.mod.features.farming.jacob;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Feature to highlight all unclaimed jacobs contests in his inventory.
 */
public class HighlightUnclaimedJacobsContest {

    @SuppressWarnings("MissingJavadoc")
    public static void load() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?> handledScreen)) {
                return;
            }
            if (!SkyblockUtils.isCurrentlyInSkyblock()) {
                return;
            }
            if (!ConfigManager.getConfig().farmingConfig.highlightUnclaimedJacobContests.getValue()) {
                return;
            }
            if (!handledScreen.getTitle().getString().equals("Your Contests")) {
                return;
            }

            InventoryContentUpdateEvent.register(((HandledScreen<?>) screen).getScreenHandler(),
                HighlightUnclaimedJacobsContest::updateItem);
        });
    }

    private static void updateItem(int i, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }


        final LoreComponent loreComponent = ItemUtils.getData(itemStack, DataComponentTypes.LORE);
        if (loreComponent == null || loreComponent.lines() == null ||loreComponent.lines().isEmpty()    ) {
            return;
        }


        final Text last = loreComponent.lines().getLast();

        if (!last.getString().equals("Click to claim reward!")) {
            return;
        }

        itemStack.set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR, Constants.MAIN_COLOR);
    }
}
