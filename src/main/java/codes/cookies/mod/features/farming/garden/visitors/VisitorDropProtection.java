package codes.cookies.mod.features.farming.garden.visitors;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisitorDropProtection implements TranslationKeys {

	private VisitorDropProtection(HandledScreen<?> handledScreen) {
		InventoryContentUpdateEvent.registerSlot(handledScreen.getScreenHandler(),
				ExceptionHandler.wrap(this::updateSlots));
	}

	private void updateSlots(Slot slot) {
		if (slot.id == 33) {
			handleRejectButton(slot.getStack(), handleAcceptItem(slot.inventory.getStack(29)));
		}
	}

	@SuppressWarnings("DataFlowIssue")
	protected final ItemStack disabledItem = new ItemBuilder(Items.BARRIER).hideAdditionalTooltips().set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, Runnables.doNothing())
			.setName(Text.translatable(DROP_PROTECTION_MESSAGE).setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(false).withBold(true))).set(DataComponentTypes.RARITY, Rarity.COMMON).build();

	private void handleRejectButton(ItemStack rejectStack, boolean applyProtection) {
		if (!applyProtection || rejectStack == null || rejectStack.isEmpty() || !rejectStack.isOf(Items.RED_TERRACOTTA)) {
			return;
		}

		rejectStack.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, Runnables.doNothing());
		rejectStack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, disabledItem);
		CookiesMod.getExecutorService().schedule(() -> {
			rejectStack.remove(CookiesDataComponentTypes.OVERRIDE_ITEM);
			rejectStack.remove(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
		}, 5, TimeUnit.SECONDS);
	}

	private boolean handleAcceptItem(ItemStack visitorItem) {
		if (visitorItem == null || visitorItem.isEmpty() || !visitorItem.isOf(Items.GREEN_TERRACOTTA) || !visitorItem.contains(DataComponentTypes.LORE)) {
			return false;
		}
		var lore = String.join("\n", visitorItem.get(DataComponentTypes.LORE).lines().stream().map(Text::getString).toArray(String[]::new));
		Pattern pattern = Pattern.compile("Rewards:([\\S\\s]*)(?:Click|Missing)");
		Matcher matcher = pattern.matcher(lore);
		if (!matcher.find()) {
			return false;
		}

		for (String rareDrop : rareDrops) {
			if (StringUtils.containsIgnoreCase(lore, rareDrop)) {
				return true;
			}
		}

		if (ConfigManager.getConfig().farmingConfig.visitorNotAsRareDropProtection.getValue()) {
			for (String commonDrop : commonDrops) {
				if (StringUtils.containsIgnoreCase(lore, commonDrop)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final String[] rareDrops = new String[] {
			"Bandana",
			"Music",
			"Dedication",
			"Jungle Key",
			"Soul",
			"Space",
			"Harbinger",
			"Overgrown Grass",
			"Dye",
			"Copper",
	};

	private static final String[] commonDrops = new String[] {
			"Candy",
			"Biofuel",
			"Pet Cake",
			"Fine Flour",
			"Pelt",
			"Velvet",
			"Cashmere",
			"Satin",
			"Oxford",
			"Powder",
	};

	public static void init() {
		InventoryEvents.beforeInit("cookies-regex:.*", inv -> LocationUtils.Island.GARDEN.isActive() && ConfigManager.getConfig().farmingConfig.visitorRareDropProtection.getValue(), VisitorDropProtection::new);
	}
}
