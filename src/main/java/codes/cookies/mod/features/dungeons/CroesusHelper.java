package codes.cookies.mod.features.dungeons;

import java.util.List;
import java.util.Optional;

import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import com.google.common.base.Predicates;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class CroesusHelper {

	public CroesusHelper(HandledScreen<?> handledScreen) {
		InventoryContentUpdateEvent.registerSlot(handledScreen.getScreenHandler(), this::update);
	}

	public static void init() {
		InventoryEvents.beforeInit(
				"Croesus", Predicates.<HandledScreen<?>>alwaysTrue()
						.and(o -> LocationUtils.Island.DUNGEON_HUB.isActive()),
				CroesusHelper::open);
	}

	private static void open(HandledScreen<?> handledScreen) {
		if (!DungeonConfig.getInstance().croesusFoldable.highlightUnclaimedChests.getValue()) {
			return;
		}
		new CroesusHelper(handledScreen);
	}

	private void update(Slot slot) {
		final ItemStack stack = slot.getStack();

		if (!stack.isOf(Items.PLAYER_HEAD)) {
			return;
		}

		final String literalStackName = CookiesUtils.stripColor(stack.getName().getString());
		if (!this.isCatacombsOrMasterModeMame(literalStackName)) {
			return;
		}

		final Optional<List<String>> optionalLore = ItemUtils.getLore(stack);
		if (optionalLore.isEmpty()) {
			return;
		}

		final List<String> lore = optionalLore.get().stream().map(String::trim).toList();
		final boolean hasntOpenedChests = lore.contains("No Chests Opened!");

		final Item backgroundStackItem;
		if (hasntOpenedChests) {
			backgroundStackItem = Items.LIME_STAINED_GLASS_PANE;
		} else {
			backgroundStackItem = Items.GRAY_STAINED_GLASS_PANE;
		}

		stack.set(CookiesDataComponentTypes.BACKGROUND_ITEM, new ItemStack(backgroundStackItem));
	}

	private boolean isCatacombsOrMasterModeMame(String name) {
		return name.equalsIgnoreCase("Master Mode The Catacombs") || name.equalsIgnoreCase("The Catacombs");
	}

}
