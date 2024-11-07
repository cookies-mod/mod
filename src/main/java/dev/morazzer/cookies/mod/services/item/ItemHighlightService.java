package dev.morazzer.cookies.mod.services.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.categories.ItemSearchConfig;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.profile.IslandChestStorage;
import dev.morazzer.cookies.mod.events.ItemStackEvents;
import dev.morazzer.cookies.mod.render.WorldRender;
import dev.morazzer.cookies.mod.render.types.BlockHighlight;
import dev.morazzer.cookies.mod.render.types.CallbackRemovable;
import dev.morazzer.cookies.mod.services.IsSameResult;
import dev.morazzer.cookies.mod.services.item.search.ItemSearchFilter;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemHighlightService {

	@Nullable
	private static ItemHighlight itemHighlight;

	static {
		ItemStackEvents.EVENT.register(ItemHighlightService::modify);
	}

	private static Optional<ItemHighlight> getHighlight() {
		return Optional.ofNullable(itemHighlight);
	}

	private static Optional<ItemSearchFilter> getFilter() {
		return getHighlight().map(ItemHighlight::filter);
	}

	private static List<ItemStack> getModifiedItems() {
		return getHighlight().map(ItemHighlight::modified).orElseGet(ArrayList::new);
	}

	private static Optional<CompletableFuture<Boolean>> getFuture() {
		return getHighlight().map(ItemHighlight::remove);
	}

	/**
	 * Modifies the provided stack to be highlighted in the same color as the currently searched item.
	 *
	 * @param itemStack The item stack to modify.
	 */
	public static void modify(ItemStack itemStack) {
		getHighlight().ifPresent(highlight -> {
			if (itemStack.contains(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED)) {
				add(itemStack);
				return;
			}
			final IsSameResult isSame = highlight.filter.doesMatch(itemStack);
			if (isSame != IsSameResult.NO) {
				final int saveColor;
				final Integer i = itemStack.get(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
				if (i == null) {
					saveColor = 0;
				} else {
					saveColor = i;
				}
				final int color = highlight.filter.getColor() & 0xFFFFFF;
				itemStack.set(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED, saveColor);
				itemStack.set(
						CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR,
						(isSame == IsSameResult.YES ? 0xFF : 0x66) << 24 | color);
				highlight.modified.add(itemStack);
			}
		});
	}

	/**
	 * Sets the active item stack and initiates the removal of the highlight.
	 *
	 * @param filter The stack.
	 */
	public static void setActive(ItemSearchFilter filter) {
		getHighlight().ifPresent(ItemHighlightService::removeHighlight);
		ItemHighlight itemHighlight = new ItemHighlight(new ArrayList<>(), new CompletableFuture<>(), filter);
		ItemHighlightService.itemHighlight = itemHighlight;
		CookiesMod.getExecutorService().schedule(
				() -> ItemHighlightService.removeHighlight(itemHighlight),
				ItemSearchConfig.getInstance().highlightTime.getValue().getTime(),
				TimeUnit.SECONDS);
	}

	private static void removeHighlight(ItemHighlight toRemove) {
		if (ItemHighlightService.itemHighlight == toRemove) {
			ItemHighlightService.itemHighlight = null;
		}
		toRemove.remove.complete(true);
		for (ItemStack modifiedStack : new ArrayList<>(toRemove.modified)) {
			if (modifiedStack.contains(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED)) {
				modifiedStack.remove(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
				final Integer remove = modifiedStack.remove(MiscDataComponentTypes.ITEM_SEARCH_SERVICE_MODIFIED);
				if (remove != null && remove != 0) {
					modifiedStack.set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR, remove);
				}
			}
		}
	}

	/**
	 * Highlights a chest in the world.
	 *
	 * @param item  The item to highlight.
	 * @param color The color the highlight should be in.
	 */
	public static void highlightChests(Item<?> item, int color) {
		if (item.source() != ItemSources.CHESTS) {
			return;
		}

		final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) item.data();
		highlightChest(data.blockPos(), color);
		data.secondChest().ifPresent(blockPos -> highlightChest(blockPos, color));
	}

	/**
	 * Highlights a chest at the specified position.
	 *
	 * @param blockPos The position.
	 * @param color    The color to highlight in.
	 */
	public static void highlightChest(BlockPos blockPos, int color) {
		if (blockPos == null) {
			return;
		}

		getFuture().ifPresent(future -> {
			WorldRender.addRenderable(new CallbackRemovable(new BlockHighlight(blockPos, color), future));
		});
	}

	/**
	 * Highlights all possible data sources associated with the item compound.
	 *
	 * @param blockPos The list of blocks to highlight.
	 */
	public static void highlightAll(Set<BlockPos> blockPos) {
		if (!LocationUtils.Island.PRIVATE_ISLAND.isActive()) {
			return;
		}

		getFilter().ifPresent(filter -> blockPos.forEach(pos -> highlightChest(pos, filter.getColor())));
	}

	/**
	 * Adds an item to the currently modified list.
	 */
	public static void add(ItemStack itemStack) {
		getModifiedItems().add(itemStack);
	}

	private record ItemHighlight(List<ItemStack> modified, CompletableFuture<Boolean> remove,
								 ItemSearchFilter filter) {
	}
}
