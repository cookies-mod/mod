package dev.morazzer.cookies.mod.features.dungeons.solver.terminals;

import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.events.InventoryEvents;

import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;

import java.util.Locale;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;

public class StartsWithTerminalSolver extends TerminalSolver {

	public StartsWithTerminalSolver() {
		InventoryEvents.beforeInit(
				"cookies-regex:What starts with: '.'\\?",
				super.getFloorPredicate().or(super.getDebugPredicate()),
				this::modify);
	}

	private void modify(HandledScreen<?> handledScreen) {
		if (!DungeonConfig.getInstance().terminalFoldable.startsWithTerminal.getValue()) {
			return;
		}
		super.openNewTerminal();
		final String string = handledScreen.getTitle().getString();
		final String letter =
				string.replace("What starts with: '", "").replace("'?", "").trim().toLowerCase(Locale.ROOT);
		InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this.update(letter));
	}

	private InventoryContentUpdateEvent update(String letter) {
		return (slot, item) -> this.update(slot, item, letter);
	}

	private void update(int slot, ItemStack stack, String letter) {
		if (slot > 53) {
			return;
		}
		if (slot == 0) {
			super.clear();
			this.items.clear();
		}
		this.items.add(stack);
		if (stack.getName().getString().toLowerCase(Locale.ROOT).startsWith(letter)) {
			if (stack.hasGlint()) {
				stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, this.doneItem);
			} else {
				stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, SHOULD_CLICK);
			}
		} else if (slot == 53) {
			stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_TOGGLE, Unit.INSTANCE);
			if (this.localToggle) {
				stack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOff);
			} else {
				stack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOn);
			}
			return;
		} else {
			stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, this.doneItem);
		}
		if (this.localToggle) {
			stack.set(
					CookiesDataComponentTypes.OVERRIDE_ITEM,
					stack.get(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED));
		}
	}
}
