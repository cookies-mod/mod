package codes.cookies.mod.features.misc.utils;

import java.util.List;
import java.util.Locale;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.sub.EquipmentData;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

/**
 * Tracks the players stats and saves them.
 */
public interface StatsTracker {
	static void init() {
		ScreenEvents.AFTER_INIT.register(StatsTracker::afterInitScreen);
	}

	static void afterInitScreen(MinecraftClient minecraftClient, Screen screen, int scaledWidth, int scaledHeight) {
		if (!(screen instanceof GenericContainerScreen genericContainerScreen)) {
			return;
		}
		if (!SkyblockUtils.isCurrentlyInSkyblock()) {
			return;
		}
		if (!genericContainerScreen.getTitle().getString().contains("Your Equipment and Stats")) {
			return;
		}
		InventoryContentUpdateEvent.registerSlot(genericContainerScreen.getScreenHandler(), StatsTracker::track);
	}

	static void track(Slot slot) {
		final ItemStack stack = slot.getStack();
		final int index = slot.getIndex();
		if (slot.inventory instanceof PlayerInventory) {
			return;
		}
		if (stack.getName().getString().endsWith("Stats")) {
			StatsTracker.handle(stack);
		}
		if (index == 0) {
			ProfileStorage.getCurrentProfile().map(ProfileData::getEquipmentData).ifPresent(EquipmentData::reset);
		}
		if (stack.contains(CookiesDataComponentTypes.SKYBLOCK_ID) && index < 40 && index > 9) {
			ProfileStorage.getCurrentProfile()
					.map(ProfileData::getEquipmentData)
					.ifPresent(equipmentData -> equipmentData.add(stack));
		}
	}

	static void handle(ItemStack stack) {
		final LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
		if (loreComponent == null) {
			return;
		}
		final List<Text> texts = loreComponent.styledLines();
		final List<String> list = texts.stream().map(Text::getString).map(CookiesUtils::stripColor).toList();

		for (String s : list) {
			if (s.matches(" . [\\w ]+ [\\d,.]+%?")) {
				String name = s.replaceAll(" . ([\\w ]+) [\\d,.]+%?", "$1");
				String literalAmount = s.replaceAll(" . [\\w ]+ ([\\d,.]+)%?", "$1");

				String id = name.toLowerCase(Locale.ROOT).replaceAll("[^\\w ]", "").replace(" ", "_");
				double amount = Double.parseDouble(literalAmount.replaceAll(",", ""));

				ProfileStorage.getCurrentProfile().ifPresent(profile -> profile.getProfileStats().saveStat(id, amount));
			}
		}
	}
}
