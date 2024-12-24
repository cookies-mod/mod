package codes.cookies.mod.features.dungeons.solver.terminals;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.features.dungeons.DungeonFeatures;
import codes.cookies.mod.features.dungeons.DungeonInstance;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;
import org.joml.Vector2i;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;

public class StartsWithTerminalSolver extends TerminalSolver {

	String lastOpened;
	Set<Integer> lastOpenedSlots = new HashSet<>();
	Vector2i lastOpenPos;
	long lastDungeonStartTime = -1;

	public StartsWithTerminalSolver() {
		InventoryEvents.beforeInit(
				"cookies-regex:What starts with: '.'\\?",
				getFloorPredicate().or(getDebugPredicate()),
				this::modify);
	}

	private void modify(HandledScreen<?> handledScreen) {
		if (!DungeonConfig.getInstance().terminalFoldable.startsWithTerminal.getValue()) {
			return;
		}
		final String string = handledScreen.getTitle().getString();
		super.openNewTerminal();
		final String letter =
				string.replace("What starts with: '", "").replace("'?", "").trim().toLowerCase(Locale.ROOT);
		boolean isDifferentTitle = (lastOpenPos == null || !lastOpened.equals(letter));
		boolean isDifferentTerminal = lastOpenPos == null || lastOpenPos.distanceSquared(CookiesUtils.getPlayer()
				.map(PlayerEntity::getPos)
				.map(CookiesUtils::mapToXZ)
				.orElse(new Vector2i(0, 0))) > 25;
		boolean isDifferentDungeon = lastDungeonStartTime != DungeonFeatures.getInstance()
				.getCurrentInstance()
				.map(DungeonInstance::getTimeStarted)
				.orElse(-1L);
		if (isDifferentTitle || isDifferentTerminal || isDifferentDungeon) {
			lastOpenedSlots.clear();
			lastOpened = letter;
			lastOpenPos = CookiesUtils.getPlayer()
					.map(PlayerEntity::getPos)
					.map(CookiesUtils::mapToXZ)
					.orElse(new Vector2i(0, 0));
			lastDungeonStartTime =
					DungeonFeatures.getInstance().getCurrentInstance().map(DungeonInstance::getTimeStarted).orElse(-1L);
		}
		InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this.update(letter, lastOpenedSlots));
	}

	private InventoryContentUpdateEvent update(String letter, Set<Integer> clickedSlots) {
		return (slot, item) -> this.update(slot, item, letter, clickedSlots);
	}

	private void update(int slot, ItemStack stack, String letter, Set<Integer> clickedSlots) {
		if (slot > 44) {
			return;
		}
		if (slot == 0) {
			super.clear();
			this.items.clear();
		}
		this.items.add(stack);
		if (stack.getName().getString().trim().toLowerCase(Locale.ROOT).startsWith(letter)) {
			clickedSlots.forEach(System.out::println);
			if (stack.hasGlint() && clickedSlots.contains(slot)) {
				stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, this.doneItem);
			} else {
				final ItemStack copy = shouldClick.copy();

				copy.set(CookiesDataComponentTypes.ON_ITEM_CLICK_RUNNABLE, () -> clickedSlots.add(slot));
				stack.set(MiscDataComponentTypes.TERMINAL_SOLVER_MODIFIED, copy);
			}
		} else if (slot == 44) {
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
