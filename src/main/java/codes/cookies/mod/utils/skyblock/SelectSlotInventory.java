package codes.cookies.mod.utils.skyblock;

import com.google.common.util.concurrent.Runnables;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.items.types.InventoryUtilsDataComponentTypes;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Formatting;

/**
 * Utility class to select slots in skyblock inventories.
 */
public class SelectSlotInventory {

    private static final ItemStack NOT_ELIGIBLE_FOR_SELECTION;
    private static final ItemStack ELIGIBLE_FOR_SELECTION;

    static {
        NOT_ELIGIBLE_FOR_SELECTION = new ItemBuilder(Items.BARRIER).setLore()
            .hideAdditionalTooltips()
            .setName(TextUtils.translatable(TranslationKeys.SELECT_SLOT_NOT_ELIGIBLE, Formatting.RED))
            .setClickRunnable(Runnables.doNothing())
            .setGlint(false)
            .build();

        ELIGIBLE_FOR_SELECTION = new ItemBuilder(Items.BLACK_STAINED_GLASS_PANE).setLore(TextUtils.translatable(
                TranslationKeys.CLICK_TO_SELECT,
                Formatting.YELLOW).append("!"))
            .setName(TextUtils.translatable(TranslationKeys.SELECT_SLOT_ELIGIBLE,Formatting.GREEN))
            .hideAdditionalTooltips()
            .setGlint()
            .build();
    }

    private final Predicate<ItemStack> selectable;
    private final boolean allowPlayerInventory;
    private final ItemStack eligibleForSelection;
    private final ItemStack notEligibleForSelection;
    private final Consumer<Slot> selectedSlotCallback;
    private final ScreenHandler screenHandler;

    public SelectSlotInventory(Predicate<ItemStack> selectable, Consumer<Slot> selectedSlotCallback) {
        this(selectable, false, selectedSlotCallback);
    }

    public SelectSlotInventory(
        Predicate<ItemStack> selectable, boolean allowPlayerInventory, Consumer<Slot> selectedSlotCallback) {
        this(selectable,
            allowPlayerInventory,
            ELIGIBLE_FOR_SELECTION,
            NOT_ELIGIBLE_FOR_SELECTION,
            selectedSlotCallback);
    }

    /**
     * Creates a new inventory slot selection.
     * @param selectable Whether the slot can be selected.
     * @param allowPlayerInventory Whether to include the player inventory.
     * @param eligibleForSelection The item to represent eligible slots.
     * @param notEligibleForSelection The item to represent not eligible slots.
     * @param selectedSlotCallback The callback to run when clicking a (eligible) slot.
     */
    public SelectSlotInventory(
        Predicate<ItemStack> selectable,
        boolean allowPlayerInventory,
        ItemStack eligibleForSelection,
        ItemStack notEligibleForSelection,
        Consumer<Slot> selectedSlotCallback) {

        this.selectable = selectable;
        this.allowPlayerInventory = allowPlayerInventory;
        this.eligibleForSelection = eligibleForSelection;
        this.notEligibleForSelection = notEligibleForSelection;
        this.selectedSlotCallback = selectedSlotCallback;

        final Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen == null || !(currentScreen instanceof HandledScreen<?> handledScreen)) {
            throw new UnsupportedOperationException("The current screen is not a handled inventory");
        }

        this.screenHandler = handledScreen.getScreenHandler();
        this.screenHandler.slots.forEach(this::updateSlot);
    }

    private void updateSlot(Slot slot) {
        if (!this.allowPlayerInventory && slot.inventory == MinecraftClient.getInstance().player.getInventory()) {
            return;
        }

        final ItemStack stack = slot.getStack();
        if (!this.selectable.test(stack)) {
            final ItemStack copy = this.notEligibleForSelection.copy();
            copy.set(InventoryUtilsDataComponentTypes.MODIFIED, true);
            stack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, copy);
            return;
        }

        stack.remove(CookiesDataComponentTypes.OVERRIDE_ITEM);
        final ItemStack copy = this.eligibleForSelection.copy();
        copy.set(InventoryUtilsDataComponentTypes.MODIFIED, true);
        copy.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, InventoryUtils.wrapWithSound(this.clickedSlot(slot)));
        slot.getStack().set(CookiesDataComponentTypes.OVERRIDE_ITEM, copy);
    }

    private Runnable clickedSlot(Slot slot) {
        return () -> {
            this.cleanSlots();
            this.selectedSlotCallback.accept(slot);
        };
    }

    private void cleanSlots() {
        this.screenHandler.slots.forEach(this::cleanSlot);
    }

    private void cleanSlot(Slot slot) {
        if (!this.allowPlayerInventory && slot.inventory == MinecraftClient.getInstance().player.getInventory()) {
            return;
        }

        final Boolean data = ItemUtils.getData(slot.getStack(), InventoryUtilsDataComponentTypes.MODIFIED);
        if (data != null && data) {
            slot.getStack().remove(CookiesDataComponentTypes.OVERRIDE_ITEM);
        }
    }
}
