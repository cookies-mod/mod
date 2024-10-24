package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.config.ConfigKey;
import dev.morazzer.cookies.mod.events.InventoryEvents;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.skyblock.InventoryUtils;
import dev.morazzer.cookies.mod.utils.skyblock.SelectSlotInventory;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanMutablePair;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/**
 * Adds a moveable item to every matching inventory.
 */
public abstract class InventoryModifier {

    private final ItemStack stack;
    private final ConfigKey<Boolean> toggleEntry;
    private final ConfigKey<Integer> slotEntry;
    private final BooleanBooleanMutablePair couldPlace = new BooleanBooleanMutablePair(false, false);


    public InventoryModifier(
        ItemStack stack, String screenName, ConfigKey<Boolean> toggleEntry, ConfigKey<Integer> slotEntry) {
        this.stack = stack;
        this.toggleEntry = toggleEntry;
        this.slotEntry = slotEntry;
        InventoryEvents.beforeInit(screenName, this::shouldInstrument, this::afterInit);
    }

    protected boolean shouldInstrument(HandledScreen<?> handledScreen) {
        return toggleEntry.get();
    }

    private void afterInit(HandledScreen<?> handledScreen) {
        InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this::updateContents);
    }

    private void updateContents(int slot, ItemStack item) {
        if (slot > 53 || item == null || item.isEmpty()) {
            return;
        }

        onItem(slot, item);

        if (slot != slotEntry.get() && couldPlace.firstBoolean()) {
            return;
        }

        if (slot == slotEntry.get() || couldPlace.secondBoolean()) {
            if (InventoryUtils.isSkyblockUiElement(item)) {
                set(item);
            } else {
                couldPlace.second(true);
            }
        }

    }

    protected void onItem(int slot, ItemStack item) {

    }

    protected Consumer<Integer> getConsumer() {
        if (this.useSound()) {
            return InventoryUtils.wrapWithSound(this::click);
        } else {
            return this::click;
        }
    }

    private void set(ItemStack item) {
        final ItemStack forgeStack = this.stack.copy();
        forgeStack.set(CookiesDataComponentTypes.ITEM_CLICK_CONSUMER, this.getConsumer());
        item.set(CookiesDataComponentTypes.OVERRIDE_ITEM, forgeStack);
        couldPlace.first(true);
        couldPlace.second(false);
    }

	protected boolean shouldInstrument(int clicked) {
		return clicked == 1;
	}

    protected void click(int clicked) {
        if (clicked == 1) {
            new SelectSlotInventory(this.getItemPredicate().or(InventoryUtils.isSkyblockUiElement()),
                this::selectNewSlot);
        } else {
            this.clicked(clicked);
        }
    }

	protected void clicked(int clicked) {
		this.clicked();
	}

    private void selectNewSlot(Slot slot) {
        slotEntry.set(slot.getIndex());
        this.set(slot.getStack());
    }

    protected void clicked() {}

    protected abstract ComponentType<?> getModifiedComponentType();

    private Predicate<ItemStack> getItemPredicate() {
        return this::getItemPredicate;
    }

    private boolean getItemPredicate(ItemStack itemStack) {
        return Objects.nonNull(ItemUtils.getData(itemStack, this.getModifiedComponentType()));
    }

    protected boolean useSound() {
        return true;
    }
}
