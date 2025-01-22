package codes.cookies.mod.features.dungeons.chests;

import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.events.InventoryEvents;
import codes.cookies.mod.events.api.InventoryContentUpdateEvent;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import com.google.common.base.Predicates;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CroesusChestHelper {

	private final List<String> chestsNames = List.of("Wood Chest", "Gold Chest", "Diamond Chest", "Emerald Chest", "Obsidian Chest", "Bedrock Chest");

	public CroesusChestHelper(HandledScreen<?> handledScreen) {
		InventoryContentUpdateEvent.registerSlot(handledScreen.getScreenHandler(), this::update);
	}

	private void update(Slot slot) {
		final ItemStack stack = slot.getStack();

		if (!stack.isOf(Items.PLAYER_HEAD)) {
			return;
		}

		final String literalStackName = stack.getName().getString();

		if (!isChestName(literalStackName)) {
			return;
		}

		final Optional<ItemStack> stackForChest = this.getStackForChest(stack);
		stackForChest.ifPresent(itemStack -> this.modifyItem(stack, itemStack));
	}

	private void modifyItem(ItemStack originalStack, @NotNull ItemStack overrideStack) {
		ItemUtils.copy(DataComponentTypes.LORE, originalStack, overrideStack);
		ItemUtils.copy(DataComponentTypes.CUSTOM_NAME, originalStack, overrideStack);
		ItemUtils.copy(DataComponentTypes.ITEM_NAME, originalStack, overrideStack);

		if (overrideStack.isOf(Items.PLAYER_HEAD)) {
			originalStack.set(CookiesDataComponentTypes.FOREGROUND_ITEM, overrideStack);
		} else {
			originalStack.set(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM, overrideStack);
		}
	}

	private boolean isChestName(String name) {
		return chestsNames.contains(name);
	}

	public static void init() {
		InventoryEvents.beforeInit(
				"cookies-regex:Master Mode The Catacombs - F.*", Predicates.<HandledScreen<?>>alwaysTrue()
						.and(o -> LocationUtils.Island.DUNGEON_HUB.isActive()),
				CroesusChestHelper::open);
		InventoryEvents.beforeInit(
				"cookies-regex:The Catacombs - F.*", Predicates.<HandledScreen<?>>alwaysTrue()
						.and(o -> LocationUtils.Island.DUNGEON_HUB.isActive()),
				CroesusChestHelper::open);
	}

	private static void open(HandledScreen<?> handledScreen) {
		if (!DungeonConfig.getInstance().croesusFoldable.replaceChestItemWithHighestRarityItem.getValue()) {
			return;
		}
		new CroesusChestHelper(handledScreen);
	}

	private Optional<ItemStack> getStackForChest(ItemStack chest) {
		final Optional<List<String>> optionalLore = ItemUtils.getLore(chest);

		if (optionalLore.isEmpty()) {
			return Optional.empty();
		}

		final List<String> lore = optionalLore.get();

		final List<String> contents = lore.stream().skip(1).takeWhile(line -> !line.isEmpty()).collect(Collectors.toList());
		contents.removeIf(string -> string.contains("Essence"));

		final List<RepositoryItem> items = new ArrayList<>();
		boolean containsEnchantedBooks = false;
		for (String content : contents) {
			if (content.startsWith("Enchanted Book")) {
				containsEnchantedBooks = true;
			}

			RepositoryItem.ofName(content).ifPresent(items::add);
		}

		items.sort(Comparator.comparingInt(item -> item.getTier().ordinal()));

		if (!items.isEmpty()) {
			return Optional.ofNullable(items.getFirst().constructItemStack());
		}

		if (containsEnchantedBooks) {
			return Optional.of(new ItemStack(Items.ENCHANTED_BOOK));
		}

		return Optional.empty();
	}
}
