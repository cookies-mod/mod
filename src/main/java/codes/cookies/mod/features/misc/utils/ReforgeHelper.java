package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;

public class ReforgeHelper {
	public ReforgeHelper(HandledScreen<?> handledScreen) {
		
	}

	public static void init() {
		InventoryEvents.beforeInit("Reforge Item", inv -> ConfigManager.getConfig().miscConfig.enableReforgeTooltip.getValue(), ReforgeHelper::new);
	}
}
