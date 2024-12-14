package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import codes.cookies.mod.utils.items.ItemTooltipComponent;

import codes.cookies.mod.utils.items.ItemUtils;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReforgeHelper {
	public ReforgeHelper(HandledScreen<?> handledScreen) {

		InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), (slotIndex, itemStack) -> {
			try {
				if (slotIndex == 13) {
					if (itemStack == null || itemStack.isEmpty() || itemStack.get(DataComponentTypes.CUSTOM_DATA) == null) {
						return;
					}
					var reforgeButton = handledScreen.getScreenHandler().getSlot(22).getStack();
					if (reforgeButton == null || reforgeButton.isEmpty()) {
						return;
					}

					var name = reforgeButton.getName();
					if (name == null) {
						return;
					}

					var reforge = itemStack.get(DataComponentTypes.CUSTOM_DATA).copyNbt().getString("modifier");

					var lore = new ArrayList<Text>();
					lore.add(Text.literal("Last Reforge: " + reforge).setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
					lore.addAll(reforgeButton.get(DataComponentTypes.LORE).styledLines());

					reforgeButton.set(CookiesDataComponentTypes.CUSTOM_LORE, lore);
				}
			}
			catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		});
	}

	public static void init() {
		InventoryEvents.afterInit("Reforge Item", inv -> ConfigManager.getConfig().miscConfig.enableReforgeTooltip.getValue(), ReforgeHelper::new);
	}
}
