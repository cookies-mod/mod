package codes.cookies.mod.features.misc.items;

import com.google.common.base.Predicates;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.sub.MiscItemData;
import codes.cookies.mod.events.InventoryEvents;

import codes.cookies.mod.utils.dev.FunctionUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;

/**
 * Tracks misc items and stores their info.
 */
public class MiscItemTracker {

	private final MiscItemData.Type type;

	public MiscItemTracker(MiscItemData.Type type) {
		this.type = type;
		InventoryEvents.beforeInit(type.getName(), Predicates.alwaysTrue(), this::onOpenInventory);
	}

	private void onOpenInventory(HandledScreen<?> handledScreen) {
		ScreenEvents.remove(handledScreen).register(this::remove);
	}

	private void remove(Screen screen) {
		if (!(screen instanceof HandledScreen<?> handledScreen)) {
			return;
		}

		ProfileStorage.getCurrentProfile()
				.map(FunctionUtils.function(this::save))
				.orElseGet(FunctionUtils::noOp)
				.accept(handledScreen);
	}

	private void save(ProfileData profileData, HandledScreen<?> screen) {
		final MiscItemData miscTracker = profileData.getMiscTracker();
		miscTracker.removeAll(this.type);
		for (Slot slot : screen.getScreenHandler().slots) {
			if (slot.inventory instanceof PlayerInventory) {
				continue;
			}
			if (slot.getStack().get(CookiesDataComponentTypes.SKYBLOCK_ID) == null) {
				continue;
			}
			if (slot.getStack().isEmpty()) {
				continue;
			}

			miscTracker.save(this.type, slot.getStack(), slot.id);
		}
	}

	public static void register() {
		for (MiscItemData.Type value : MiscItemData.Type.values()) {
			new MiscItemTracker(value);
		}
	}

}
