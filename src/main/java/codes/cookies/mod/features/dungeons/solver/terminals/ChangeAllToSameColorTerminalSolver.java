package codes.cookies.mod.features.dungeons.solver.terminals;

import codes.cookies.mod.config.categories.dungeons.TerminalCategory;
import codes.cookies.mod.events.InventoryEvents;

import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Unit;

import net.minecraft.util.math.MathHelper;

import org.apache.commons.lang3.ArrayUtils;

public class ChangeAllToSameColorTerminalSolver extends TerminalSolver {
	private static final Item[] ITEMS = {Items.ORANGE_STAINED_GLASS_PANE,
			Items.YELLOW_STAINED_GLASS_PANE,
			Items.GREEN_STAINED_GLASS_PANE,
			Items.BLUE_STAINED_GLASS_PANE,
			Items.RED_STAINED_GLASS_PANE};
	private static final int[] SLOTS = {12, 13, 14, 21, 22, 23, 30, 31, 32};
	private static final String[] DISPLAY = {"‹  ", "« ", "›  ", "» "};

	public ChangeAllToSameColorTerminalSolver() {
		InventoryEvents.beforeInit("Change all to same color!",
				getFloorPredicate().or(getDebugPredicate()),
				this::modify);
	}

	private void modify(HandledScreen<?> handledScreen) {
		if (!TerminalCategory.changeAllToSameColorTerminal) {
			return;
		}
		super.openNewTerminal();
		InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this::update);
	}

	private void update(int slot, ItemStack stack) {
		if (slot > 45) {
			return;
		}
		if (slot == 0) {
			super.clear();
			this.items.clear();
		}
		this.items.add(slot, stack);
		if (slot == 44) {
			this.solve();
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
	}

	private void solve() {
		final ItemStack[] stacks = new ItemStack[SLOTS.length];
		final int[] slotClicks = new int[SLOTS.length];
		int minClicks = Integer.MAX_VALUE;

		for (int i = 0; i < SLOTS.length; i++) {
			final int slot = SLOTS[i];
			ItemStack item = this.items.get(slot);
			if (!ArrayUtils.contains(ITEMS, item.getItem())) {
				return;
			}
			stacks[i] = item;
		}

		for (int i = 0; i < ITEMS.length; i++) {
			final int[] localSlotClicks = new int[SLOTS.length];
			int clicks = 0;
			for (int itemIndex = 0; itemIndex < stacks.length; itemIndex++) {
				ItemStack stack = stacks[itemIndex];
				int index = ArrayUtils.indexOf(ITEMS, stack.getItem());
				if (index == -1) {
					break;
				}

				int distance = index > i ? i + ITEMS.length - index : i - index;
				int actualDistance = distance > 2 ? distance - ITEMS.length : distance;
				clicks += Math.abs(actualDistance);
				localSlotClicks[itemIndex] = actualDistance;
			}
			if (clicks < minClicks) {
				minClicks = clicks;
				System.arraycopy(localSlotClicks, 0, slotClicks, 0, localSlotClicks.length);
			}
		}

		for (int i = 0; i < slotClicks.length; i++) {
			ItemStack item = stacks[i];
			int clicks = slotClicks[i];

			if (clicks == 0) {
				continue;
			}
			int index;
			if (clicks < 0) {
				index = Math.abs(clicks) + 1;
			} else {
				index = Math.abs(clicks) - 1;
			}

			item.set(
					MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED_STRING,
					"%s".formatted(DISPLAY[MathHelper.clamp(index, 0, DISPLAY.length - 1)]));
		}
	}

}
