package dev.morazzer.cookies.mod.features.mining.utils;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.repository.constants.Hotm;
import dev.morazzer.cookies.mod.repository.constants.RepositoryConstants;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.types.HotmDataComponentTypes;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.Optional;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Utils that modify the hotm scree.
 */
public class HotmUtils {

	@SuppressWarnings("MissingJavadoc")
	public HotmUtils() {
		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof GenericContainerScreen genericContainerScreen)) {
				return;
			}

			if (!SkyblockUtils.isCurrentlyInSkyblock()) {
				return;
			}
			if (!genericContainerScreen.getTitle().getString().equals("Heart of the Mountain")) {
				return;
			}

			InventoryContentUpdateEvent.register(genericContainerScreen.getScreenHandler(),
					ExceptionHandler.wrap(this::updateInventory));
		});
	}

	private void updateInventory(int i, ItemStack itemStack) {
		this.setBaseComponents(itemStack);

		if (ConfigManager.getConfig().miningConfig.showHotmPerkLevelAsStackSize.getValue()) {
			this.setPerkLevelAsStackSize(itemStack);
		}
		if (ConfigManager.getConfig().miningConfig.highlightDisabledHotmPerks.getValue()) {
			this.setBackgroundColor(itemStack);
		}
		if (ConfigManager.getConfig().miningConfig.showNext10Cost.getValue() ||
			ConfigManager.getConfig().miningConfig.showTotalCost.getValue()) {
			this.setCost(itemStack);
		}
	}

	private void setBaseComponents(ItemStack itemStack) {
		final LoreComponent data = ItemUtils.getData(itemStack, DataComponentTypes.LORE);
		if (data == null) {
			return;
		}
		final List<Text> lines = data.lines();
		if (lines == null || lines.isEmpty()) {
			return;
		}

		final int level = this.getLevel(lines.getFirst());
		if (level > 0) {
			itemStack.set(HotmDataComponentTypes.HOTM_PERK_LEVEL, level);
		}
		final boolean disabled = this.isDisabled(lines);
		itemStack.set(HotmDataComponentTypes.HOTM_DISABLED, disabled);
		final Text name = ItemUtils.getData(itemStack, DataComponentTypes.CUSTOM_NAME);
		if (name == null) {
			return;
		}
		final String type = name.getString().toLowerCase(Locale.ROOT).replaceAll(" ", "_").replaceAll("\\W", "");
		itemStack.set(HotmDataComponentTypes.HOTM_PERK_TYPE, type);
		this.save(type, level, !disabled);
	}

	/**
	 * Saves the given parameters to the profile data.
	 * @param id The perk id.
	 * @param level The perk level.
	 * @param enabled Whether the perk is enabled or not.,
	 */
	private void save(String id, int level, boolean enabled) {
		final Optional<ProfileData> optionalProfile = ProfileStorage.getCurrentProfile();
		if (optionalProfile.isEmpty()) {
			return;
		}
		final ProfileData profileData = optionalProfile.get();
		profileData.getHotmData().save(id, level, enabled);
	}

	private void setPerkLevelAsStackSize(ItemStack itemStack) {
		if (itemStack.getItem() == Items.COAL) {
			return;
		}

		final Integer data = ItemUtils.getData(itemStack, HotmDataComponentTypes.HOTM_PERK_LEVEL);
		if (data == null) {
			return;
		}
		itemStack.set(CookiesDataComponentTypes.CUSTOM_SLOT_TEXT, String.valueOf(data));
	}

	private void setBackgroundColor(ItemStack itemStack) {
		final Boolean data = ItemUtils.getData(itemStack, HotmDataComponentTypes.HOTM_DISABLED);
		if (data == null || !data) {
			return;
		}

		itemStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, new ItemStack(Items.REDSTONE));
	}

	private void setCost(ItemStack itemStack) {
		final NumberFormat numberFormat = DecimalFormat.getNumberInstance(Locale.ENGLISH);
		final LoreComponent data = ItemUtils.getData(itemStack, DataComponentTypes.LORE);
		if (data == null || data.lines().isEmpty()) {
			return;
		}
		String perkType = ItemUtils.getData(itemStack, HotmDataComponentTypes.HOTM_PERK_TYPE);
		Integer perkLevel = ItemUtils.getData(itemStack, HotmDataComponentTypes.HOTM_PERK_LEVEL);
		if (perkType == null || perkLevel == null) {
			return;
		}

		final List<Text> lines = new ArrayList<>(data.lines());

		for (int index = 0; index < lines.size(); index++) {
			final Text current = lines.get(index);
			final String textCurrent = current.getString().trim();
			if (!textCurrent.equals("Cost")) {
				continue;
			}
			index += 2;

			if (ConfigManager.getConfig().miningConfig.showNext10Cost.getValue()) {
				lines.add(index++, Text.empty());
				lines.add(index++,
						Text.translatable(TranslationKeys.HOTM_UTILS_COST_NEXT_10).formatted(Formatting.GRAY));
				final Hotm.Perk perk = RepositoryConstants.hotm.getPerk(perkType);
				if (perk == null) {
					return;
				}
				int amount = perk.calculateNextN(9, perkLevel);
				lines.add(index++,
						Text.literal("%s ".formatted(numberFormat.format(amount)))
								.append(perk.powderTypes().getName())
								.formatted(perk.powderTypes().getFormatting()));
			}

			if (ConfigManager.getConfig().miningConfig.showTotalCost.getValue()) {
				lines.add(index++, Text.empty());
				lines.add(index++,
						Text.translatable(TranslationKeys.HOTM_UTILS_COST_TOTAL).formatted(Formatting.GRAY));
				final Hotm.Perk perk = RepositoryConstants.hotm.getPerk(perkType);
				if (perk == null) {
					return;
				}
				int amount = perk.calculateTotal(perkLevel);
				lines.add(index,
						Text.literal("%s ".formatted(numberFormat.format(amount)))
								.append(perk.powderTypes().getName())
								.formatted(perk.powderTypes().getFormatting()));
			}
			break;
		}

		itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE, lines);
	}

	private int getLevel(Text text) {
		final String line = text.getString().trim();
		if (!line.startsWith("Level")) {
			return 0;
		}
		final String perkLevel = line.split("/")[0].replaceAll("\\D", "");
		return Integer.parseInt(perkLevel);
	}

	private boolean isDisabled(List<Text> lines) {
		for (Text line : lines) {
			if (line.getString().trim().equalsIgnoreCase("disabled")) {
				return true;
			}
		}
		return false;
	}
}
