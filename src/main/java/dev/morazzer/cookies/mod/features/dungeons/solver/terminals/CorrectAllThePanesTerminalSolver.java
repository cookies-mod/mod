package dev.morazzer.cookies.mod.features.dungeons.solver.terminals;

import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.events.InventoryEvents;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Unit;

public class CorrectAllThePanesTerminalSolver extends TerminalSolver {

	public CorrectAllThePanesTerminalSolver() {
		InventoryEvents.beforeInit("Correct all the panes!",
				super.getFloorPredicate().or(super.getDebugPredicate()),
				this::modify);
	}

	private void modify(HandledScreen<?> handledScreen) {
		if (!DungeonConfig.getInstance().terminalFoldable.correctAllThePanesTerminal.getValue()) {
			return;
		}
		super.openNewTerminal();
		InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this::update);
	}

	private void update(int slot, ItemStack stack) {
		if (slot > 44) {
			return;
		}
		if (slot == 0) {
			super.clear();
			this.items.clear();
		}
		this.items.add(stack);
		if (slot == 44) {
			stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_TOGGLE, Unit.INSTANCE);
			if (this.localToggle) {
				stack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOff);
			} else {
				stack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOn);
			}
			if (this.localToggle) {
				this.setItems();
			}
		}
		if (stack.getItem() == Items.RED_STAINED_GLASS_PANE) {
			stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, shouldClick);
			return;
		}
		stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, this.doneItem);
	}
}
