package codes.cookies.mod.features.dungeons.solver.terminals;

import codes.cookies.mod.config.categories.dungeons.TerminalCategory;
import com.google.common.base.Predicates;
import com.google.common.util.concurrent.Runnables;
import codes.cookies.mod.features.dungeons.DungeonFeatures;
import codes.cookies.mod.features.dungeons.DungeonInstance;
import codes.cookies.mod.features.dungeons.map.DungeonPhase;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Predicate;

import java.util.function.Supplier;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public abstract class TerminalSolver {
	public static Identifier DEBUG = DevUtils.createIdentifier("terminals/disable_click_all_check");
	protected boolean localToggle = true;

	protected final List<ItemStack> items = new ArrayList<>();

	protected final ItemStack shouldClick =
			new ItemBuilder(Items.GREEN_STAINED_GLASS_PANE).hideTooltips().hideAdditionalTooltips().build();

	protected final ItemStack doneItem =
			new ItemBuilder(Items.BLACK_STAINED_GLASS_PANE).hideAdditionalTooltips().hideTooltips().build();


	public void openNewTerminal() {
		if (TerminalCategory.preventMissclicks) {
			this.doneItem.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, Runnables.doNothing());
		} else {
			this.doneItem.remove(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
		}
	}

	protected final ItemStack toggleOn =
			new ItemBuilder(Items.GREEN_WOOL).setName(TextUtils.literal("Click to turn on", Formatting.DARK_GREEN))
					.setLore(TextUtils.literal("Enables/Disables the solver for this terminal!", Formatting.GRAY))
					.setClickRunnable(this::turnOn)
					.hideAdditionalTooltips()
					.build();

	private void turnOn() {
		this.localToggle = true;
		this.setItems();
	}

	public void setItems() {
		for (ItemStack item : this.items) {
			if (item.contains(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED)) {
				item.set(
						CookiesDataComponentTypes.OVERRIDE_ITEM,
						item.get(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED));
			} else if (item.contains(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED_SUPPLIER)) {
				final Supplier<ItemStack> supplier =
						item.get(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED_SUPPLIER);
				if (supplier != null) {
					item.set(CookiesDataComponentTypes.OVERRIDE_ITEM, supplier.get());
				}
			} else if (item.contains(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED_STRING)) {
				item.set(
						CookiesDataComponentTypes.CUSTOM_SLOT_TEXT,
						item.get(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED_STRING));
			}
			if (item.contains(MiscDataComponentTypes.TERMINAL_SOLVER_TOGGLE)) {
				item.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOff);
			}
		}
	}

	protected static Predicate<HandledScreen<?>> getFloorPredicate() {
		return Predicates.<HandledScreen<?>>alwaysTrue()
				.and(o -> DungeonFeatures.getInstance().getCurrentInstance().isPresent())
				.and(o -> DungeonFeatures.getInstance().getCurrentInstance().map(DungeonInstance::floor).orElse(-1) == 7)
				.and(o -> DungeonFeatures.getInstance().getCurrentInstance().map(DungeonInstance::getPhase).orElse(null) == DungeonPhase.BOSS);
	}

	protected static Predicate<HandledScreen<?>> getDebugPredicate() {
		return t -> DevUtils.isEnabled(TerminalSolver.DEBUG);
	}

	protected final ItemStack toggleOff =
			new ItemBuilder(Items.RED_WOOL).setName(TextUtils.literal("Click to turn off", Formatting.RED))
					.setLore(TextUtils.literal("Enables/Disables the solver for this terminal!", Formatting.GRAY))
					.setClickRunnable(this::turnOff)
					.hideAdditionalTooltips()
					.build();

	private void turnOff() {
		this.localToggle = false;
		this.clear();
	}

	protected void clear() {
		for (ItemStack item : this.items) {
			item.remove(CookiesDataComponentTypes.OVERRIDE_ITEM);
			if (item.contains(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED_STRING)) {
				item.remove(CookiesDataComponentTypes.CUSTOM_SLOT_TEXT);
			}
			if (item.contains(MiscDataComponentTypes.TERMINAL_SOLVER_TOGGLE)) {
				item.set(CookiesDataComponentTypes.OVERRIDE_ITEM, this.toggleOn);
			}
		}
	}

}
