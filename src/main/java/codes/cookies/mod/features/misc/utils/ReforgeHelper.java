package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;

public class ReforgeHelper {
	public ReforgeHelper(HandledScreen<?> handledScreen) {
		handledScreen.getScreenHandler().addListener(new ScreenHandlerListener() {
			@Override
			public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
				CookiesUtils.sendMessage("onSlotUpdate: " + slotId + " " + stack);
			}

			@Override
			public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
				CookiesUtils.sendMessage("onPropertyUpdate: " + property + " " + value);
			}
		});
	}

	public static void init() {
		InventoryEvents.beforeInit("Reforge Item", inv -> ConfigManager.getConfig().miscConfig.enableReforgeTooltip.getValue(), ReforgeHelper::new);
	}
}
