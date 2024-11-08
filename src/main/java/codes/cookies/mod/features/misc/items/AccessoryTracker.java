package codes.cookies.mod.features.misc.items;

import com.google.common.base.Predicates;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.sub.AccessoryItemData;
import codes.cookies.mod.events.InventoryEvents;

import codes.cookies.mod.utils.dev.FunctionUtils;

import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;

/**
 * Saves accessory data for later use.
 */
public class AccessoryTracker {

	private static String name;

	public static void register() {
		InventoryEvents.beforeInit("cookies-regex:Accessory Bag .*", Predicates.alwaysTrue(), AccessoryTracker::open);
	}

	private static void open(HandledScreen<?> handledScreen) {
		name = handledScreen.getTitle().getString();
		ScreenEvents.remove(handledScreen).register(AccessoryTracker::remove);
	}

	private static void remove(Screen screen) {
		if (screen instanceof HandledScreen<?> handledScreen) {
			ProfileStorage.getCurrentProfile()
					.map(FunctionUtils.function(AccessoryTracker::save))
					.orElseGet(FunctionUtils::noOp2)
					.accept(handledScreen, name);
		}
	}

	private static void save(ProfileData profileData, HandledScreen<?> handledScreen, String name) {
		int page;
		if (name.endsWith(")")) {
			page = Integer.parseInt(name.substring(name.lastIndexOf("(") + 1, name.lastIndexOf("/")));
		} else {
			page = 1;
		}

		final AccessoryItemData accessoryTracker = profileData.getAccessoryTracker();

		accessoryTracker.clearPage(page);
		for (Slot slot : handledScreen.getScreenHandler().slots) {
			if (slot.inventory instanceof PlayerInventory) {
				continue;
			}
			if (slot.getStack().get(CookiesDataComponentTypes.SKYBLOCK_ID) == null) {
				continue;
			}
			if (slot.getStack().isEmpty()) {
				continue;
			}

			accessoryTracker.save(slot.getStack(), slot.id, page);
		}
	}

}
