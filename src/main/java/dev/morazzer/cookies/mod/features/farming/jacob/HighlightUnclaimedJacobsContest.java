package dev.morazzer.cookies.mod.features.farming.jacob;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.api.ItemBackgroundRenderCallback;
import dev.morazzer.cookies.mod.utils.Constants;
import java.util.Optional;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

/**
 * Feature to highlight all unclaimed jacobs contests in his inventory.
 */
public class HighlightUnclaimedJacobsContest {

    @SuppressWarnings("MissingJavadoc")
    public static void load() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!ConfigManager.getConfig().farmingConfig.highlightUnclaimedJacobContests.getValue()) {
                return;
            }
            if (!(screen instanceof HandledScreen<?> handledScreen)) {
                return;
            }
            if (!handledScreen.getTitle().getString().equals("Your Contests")) {
                return;
            }
            ItemBackgroundRenderCallback.register(handledScreen,
                HighlightUnclaimedJacobsContest::renderBackgroundIfNotClaimed);
        });
    }

    private static void renderBackgroundIfNotClaimed(DrawContext drawContext, Slot slot) {
        if (slot.getStack().isEmpty()) {
            return;
        }

        final Optional<? extends LoreComponent> loreComponent =
            slot.getStack().getComponentChanges().get(DataComponentTypes.LORE);
        //noinspection OptionalAssignedToNull
        if (loreComponent == null || loreComponent.isEmpty()) {
            return;
        }

        final Text last = loreComponent.get().lines().getLast();

        if (!last.getString().equals("Click to claim reward!")) {
            return;
        }

        drawContext.fill(
            slot.x,
            slot.y,
            slot.x + 16,
            slot.y + 16,
            Constants.MAIN_COLOR
        );
    }
}
