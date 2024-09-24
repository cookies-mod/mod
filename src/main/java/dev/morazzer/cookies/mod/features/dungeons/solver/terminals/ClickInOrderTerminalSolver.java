package dev.morazzer.cookies.mod.features.dungeons.solver.terminals;

import com.google.common.util.concurrent.Runnables;
import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.events.InventoryEvents;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;

public class ClickInOrderTerminalSolver extends TerminalSolver {
	private final ItemStack todo = this.doneItem.copy().withItem(Items.RED_STAINED_GLASS_PANE);
	private final ItemStack thirdItem = this.doneItem.copy().withItem(Items.ORANGE_STAINED_GLASS_PANE);
	private final ItemStack secondItem = this.doneItem.copy().withItem(Items.YELLOW_STAINED_GLASS_PANE);

	private static final ItemStack FIRST_CLICK = SHOULD_CLICK;
	private final ItemStack[] itemList = new ItemStack[] {FIRST_CLICK, this.secondItem, this.thirdItem, this.todo};

	private int lowestAmount = Integer.MAX_VALUE;

	public ClickInOrderTerminalSolver() {
		InventoryEvents.beforeInit("Click in order!",
				super.getFloorPredicate().or(super.getDebugPredicate()),
				this::modify);
	}

	private void modify(HandledScreen<?> handledScreen) {
		if (!DungeonConfig.getInstance().terminalFoldable.clickInOrderTerminal.getValue()) {
			return;
		}
		super.openNewTerminal();
		if (DungeonConfig.getInstance().terminalFoldable.preventMissclicks.getValue()) {
			this.todo.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, Runnables.doNothing());
			this.thirdItem.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, Runnables.doNothing());
		} else {
			this.todo.remove(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
			this.thirdItem.remove(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
		}
		InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this::update);
	}

	private void update(int slot, ItemStack stack) {
		if (slot > 35) {
			return;
		}
		if (slot == 0) {
			super.clear();
			this.lowestAmount = Integer.MAX_VALUE;
			this.items.clear();
		}
		this.items.add(stack);
		if (slot == 35) {
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

		if (!(stack.getItem() == Items.RED_STAINED_GLASS_PANE)) {
			stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, this.doneItem);
			return;
		}

		if (this.lowestAmount > stack.getCount()) {
			this.lowestAmount = stack.getCount();
		}
		stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED_SUPPLIER, () -> this.getStackFor(stack));
	}

	private ItemStack getStackFor(ItemStack stack) {
		int delta = stack.getCount() - this.lowestAmount;
		if (delta < 0) {
			return this.doneItem;
		}

		return this.itemList[MathHelper.clamp(delta, 0, this.itemList.length - 1)].copyWithCount(stack.getCount());
	}
}
