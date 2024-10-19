package dev.morazzer.cookies.mod.features.mining.commissions;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

/**
 * Highlights unclaimed commissions/replaces the item.
 */
public class CommissionCompletionHighlighter {

	@SuppressWarnings("MissingJavadoc")
	public CommissionCompletionHighlighter() {
		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof GenericContainerScreen genericContainerScreen)) {
				return;
			}

			if (!SkyblockUtils.isCurrentlyInSkyblock()) {
				return;
			}
			if (!ConfigManager.getConfig().miningConfig.modifyCommissions.getValue()) {
				return;
			}
			if (!genericContainerScreen.getTitle().getString().equals("Commissions")) {
				return;
			}
			if (!isInMines()) {
				return;
			}

			InventoryContentUpdateEvent.register(genericContainerScreen.getScreenHandler(),
					ExceptionHandler.wrap(this::updateInventory));
		});
	}

	private void updateInventory(int index, ItemStack itemStack) {
		if (itemStack.getItem() != Items.WRITABLE_BOOK) {
			return;
		}

		final LoreComponent data = ItemUtils.getData(itemStack, DataComponentTypes.LORE);
		if (data == null) {
			return;
		}
		final String lastLineLiteral = data.lines().getLast().getString();

		if (lastLineLiteral.equals("Click to claim rewards!")) {
			itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, new ItemStack(Items.KNOWLEDGE_BOOK));
			return;
		}

		if (lastLineLiteral.trim().equals("0%")) {
			itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, new ItemStack(Items.WRITTEN_BOOK));
		} else {
			itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, new ItemStack(Items.WRITABLE_BOOK));
		}

	}

	public boolean isInMines() {
		return LocationUtils.Island.DWARVEN_MINES.isActive() || LocationUtils.Island.CRYSTAL_HOLLOWS.isActive() ||
			   LocationUtils.Island.MINESHAFT.isActive();
	}

}
