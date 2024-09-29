package dev.morazzer.cookies.mod.features.mining;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.events.InventoryEvents;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.ForgeUtils;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import java.util.Collections;
import java.util.Optional;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/**
 * Features related to the forge, mainly saving of the forge processes.
 */
public class ForgeFeatures {

	private ForgeFeatures(HandledScreen<?> handledScreen) {
		InventoryContentUpdateEvent.registerSlot(handledScreen.getScreenHandler(),
				ExceptionHandler.wrap(this::updateSlots));
	}

	private void updateSlots(Slot slot) {
		if (slot.id < 10 || slot.id > 16) {
			return;
		}
		slot.getStack().set(CookiesDataComponentTypes.CUSTOM_SLOT_TEXT, String.valueOf(slot.id - 10));
		this.save(slot, slot.id - 10);
	}

	private void save(Slot slot, int id) {
		final Optional<ProfileData> optionalProfile = ProfileStorage.getCurrentProfile();
		if (optionalProfile.isEmpty()) {
			return;
		}

		final ProfileData profileData = optionalProfile.get();

		final ItemStack stack = slot.getStack();
		final String timeRemaining = ForgeUtils.extractTimeRemaining(Optional.ofNullable(stack.get(DataComponentTypes.LORE))
				.map(LoreComponent::lines)
				.orElseGet(Collections::emptyList));

		if (timeRemaining == null) {
			profileData.getForgeTracker().removeItem(id);
			return;
		}
		final RepositoryItem repositoryItem = stack.get(CookiesDataComponentTypes.REPOSITORY_ITEM);
		if (repositoryItem == null) {
			return;
		}

		final long startTimeSeconds = ForgeUtils.getStartTimeSeconds(timeRemaining, repositoryItem);
		profileData.getForgeTracker().saveItem(repositoryItem, id, startTimeSeconds);
	}

	public static void init() {
		InventoryEvents.beforeInit("The Forge", o -> LocationUtils.isInSkyblock(), ForgeFeatures::new);
	}

}
