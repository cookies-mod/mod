package dev.morazzer.cookies.mod.features.misc.utils;

import java.util.List;
import java.util.Locale;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.sub.EquipmentData;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

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
		InventoryContentUpdateEvent.register(genericContainerScreen.getScreenHandler(), StatsTracker::track);
	}

	static void track(int i, ItemStack stack) {
		if (stack.getName().getString().endsWith("Stats")) {
			StatsTracker.handle(stack);
		}
		if (i == 0) {
			ProfileStorage.getCurrentProfile().map(ProfileData::getEquipmentData).ifPresent(EquipmentData::reset);
		}
		if (stack.contains(CookiesDataComponentTypes.REPOSITORY_ITEM) && i < 40) {
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
