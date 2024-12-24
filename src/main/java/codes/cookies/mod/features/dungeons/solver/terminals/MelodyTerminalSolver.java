package codes.cookies.mod.features.dungeons.solver.terminals;

import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.events.InventoryEvents;

import codes.cookies.mod.events.api.InventoryContentUpdateEvent;

import codes.cookies.mod.utils.accessors.SlotAccessor;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;
import codes.cookies.mod.utils.skyblock.ChatUtils;

import com.google.common.util.concurrent.Runnables;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Unit;

import java.util.ArrayList;

public class MelodyTerminalSolver extends TerminalSolver {
    private final ItemStack blockedInput = this.doneItem.copy().withItem(Items.BARRIER);
    private final boolean[] percentagesAnnounced = new boolean[4];
    private int currentColumn = -1;
    private int correctColumn = -1;
    private int currentRow = -1;

    public MelodyTerminalSolver() {
        InventoryEvents.beforeInit("Click the button on time!",
                getFloorPredicate().or(getDebugPredicate()),
                this::modify);
    }

    private static void melodyNotify(String message) {
        if (DungeonConfig.getInstance().terminalFoldable.melodyNotifier.getValue()) {
            ChatUtils.sendPartyMessage(message);
        }
    }

    private void modify(HandledScreen<?> handledScreen) {
        ScreenEvents.remove(handledScreen).register((screen) -> {
            if (percentagesAnnounced[3])
                melodyNotify("Melody Terminal complete!");
            else
                melodyNotify("Melody closed without completion???");

            CookiesUtils.sendMessage("Melody Terminal reset!");
            percentagesAnnounced[0] = false;
            percentagesAnnounced[1] = false;
            percentagesAnnounced[2] = false;
            percentagesAnnounced[3] = false;
            this.clear();
            this.items.clear();
            currentColumn = -1;
            correctColumn = -1;
            currentRow = -1;
        });

        openNewTerminal();

        InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), (slot, stack) -> {
            this.update(slot, stack, handledScreen.getScreenHandler());
        });
    }

    @Override
    public void openNewTerminal() {
        super.openNewTerminal();
        if (DungeonConfig.getInstance().terminalFoldable.preventMissclicks.getValue()) {
            this.blockedInput.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, Runnables.doNothing());
        } else {
            this.blockedInput.remove(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
        }
        melodyNotify("Melody Terminal start!");
    }

    private void update(int slot, ItemStack stack, ScreenHandler handler) {
        if (slot > 53) {
            return;
        }

        this.items.add(stack);

        if (slot == 53) {
            stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_TOGGLE, Unit.INSTANCE);
            if (this.localToggle) {
                stack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOff);
            } else {
                stack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOn);
            }
            if (this.localToggle) {
                this.setItems();
            }
            return;
        }

        if (slot % 9 == 0 || slot % 9 == 6 || slot % 9 == 8) {
            return;
        }

        if (stack.isOf(Items.LIME_STAINED_GLASS_PANE)) {
            currentRow = (slot / 9) - 1;
            currentColumn = slot % 9;

            if (this.localToggle && currentRow > 0 && !percentagesAnnounced[currentRow]) {
                melodyNotify("Melody is " + String.format("%.0f%%", (currentRow / 4f) * 100) + " (" + currentRow + "/4) complete!");
                percentagesAnnounced[currentRow] = true;
            }

            var selectedSlot = handler.getSlot(slot + (7 - currentColumn));
            if (!this.localToggle) {
                SlotAccessor.setInteractionLocked(selectedSlot, false);
                SlotAccessor.setItem(selectedSlot, null);
                return;
            }

            if (currentColumn == correctColumn) {
                SlotAccessor.setInteractionLocked(selectedSlot, false);
                SlotAccessor.setItem(selectedSlot, null);
            } else {
                SlotAccessor.setInteractionLocked(selectedSlot, true);
                SlotAccessor.setItem(selectedSlot, this.blockedInput);
            }

            return;
        }

        if (stack.isOf(Items.MAGENTA_STAINED_GLASS_PANE)) {
            correctColumn = slot % 9;
            return;
        }

        if (stack.isOf(Items.LIME_TERRACOTTA) && this.localToggle) {
            if (currentColumn == correctColumn) {
                SlotAccessor.setInteractionLocked(handler.getSlot(slot), false);
                SlotAccessor.setItem(handler.getSlot(slot), null);
            } else {
                SlotAccessor.setInteractionLocked(handler.getSlot(slot), true);
                SlotAccessor.setItem(handler.getSlot(slot), this.blockedInput);
            }
        }
    }
}
