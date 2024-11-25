package codes.cookies.mod.features.farming.garden.visitors;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.List;

public class VisitorDropProtection {

	private VisitorDropProtection(HandledScreen<?> handledScreen) {
		InventoryContentUpdateEvent.registerSlot(handledScreen.getScreenHandler(),
				ExceptionHandler.wrap(this::updateSlots));
	}

	private void updateSlots(Slot slot) {
		if(slot.id == 33) {
			handleRejectButton(slot.getStack(), handleAcceptItem(slot.inventory.getStack(29)));
		}
	}



	private void handleRejectButton(ItemStack rejectButton, boolean visitorItemLore)
	{
		if(rejectButton.isEmpty() || !rejectButton.isOf(Items.RED_CONCRETE_POWDER) || !visitorItemLore) {
			return;
		}

		CookiesUtils.sendMessage("Rejecting visitor drop protection");
		rejectButton.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, Runnables.doNothing());
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						CookiesUtils.sendMessage("Re-enabling visitor drop protection");
						rejectButton.remove(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
					}
				},
				//ConfigManager.getConfig().farmingConfig.visitorDropProtectionDelay.getValue()
				5000
		);
	}

	private boolean handleAcceptItem(ItemStack visitorItem)
	{
		if(visitorItem == null || visitorItem.isEmpty() || !visitorItem.isOf(Items.GREEN_TERRACOTTA) || !visitorItem.contains(DataComponentTypes.LORE)) {
			return false;
		}

        for (Text text : visitorItem.get(DataComponentTypes.LORE).lines()) {
            if (text.getString().contains("+")) {
                CookiesUtils.sendMessage("drop: " + text.getString());

				for (String rareDrop : rareDrops) {
					if (text.getString().contains(rareDrop)) {
						return true;
					}
				}

				if(ConfigManager.getConfig().farmingConfig.visitorNotAsRareDropProtection.getValue()) {
					for (String commonDrop : commonDrops) {
						if (text.getString().contains(commonDrop)) {
							return true;
						}
					}
				}

            }
        }

        return false;
	}

	private static final String[] rareDrops = new String[] {
			"Bandana",
			"Music",
			"Dedication",
			"Jungle Key",
			"Soul",
			"Space",
			"Harbinger",
			"Overgrown Grass",
	};

	private static final String[] commonDrops = new String[] {
			"Candy",
			"Biofuel", //
			"Pet Cake", //
			"Fine Flour",
			"Pelt",
			"Velvet",
			"Cashmere",
			"Satin",
			"Oxford",
			"Powder",
	};

	public static void init() {
		InventoryEvents.beforeInit("cookies-regex:.*", inv -> LocationUtils.Island.GARDEN.isActive() && ConfigManager.getConfig().farmingConfig.visitorRareDropProtection.getValue(), VisitorDropProtection::new);
	}
}
