package codes.cookies.mod.services.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicates;
import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.categories.ItemSearchConfig;
import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.data.profile.profile.IslandChestStorage;
import codes.cookies.mod.events.ChestSaveEvent;
import codes.cookies.mod.events.ItemStackEvents;
import codes.cookies.mod.render.Renderable;
import codes.cookies.mod.render.WorldRender;
import codes.cookies.mod.render.types.BlockHighlight;
import codes.cookies.mod.render.types.CallbackRemovable;
import codes.cookies.mod.services.IsSameResult;
import codes.cookies.mod.services.item.search.ItemSearchFilter;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemHighlightService {

	@Nullable
	private static ItemHighlight itemHighlight;

	static {
		ChestSaveEvent.EVENT.register(ItemHighlightService::checkChest);
		ItemStackEvents.EVENT.register(ItemHighlightService::modify);
	}

	private static void checkChest(
			BlockPos blockPos,
			BlockPos second,
			List<IslandChestStorage.ChestItem> chestItems
	) {
		getHighlight().ifPresent(highlight -> {
			final boolean doesHaveHighlight = chestItems.stream()
					.map(IslandChestStorage.ChestItem::itemStack).map(highlight.filter::doesMatch)
					.anyMatch(Predicates.not(IsSameResult.NO::equals));
			if (highlight.hasHighlight(blockPos)) {
				if (doesHaveHighlight) {
					return;
				}

				highlight.getHighlight(blockPos).ifPresent(chestHighlight -> {
					WorldRender.removeRenderable(chestHighlight.renderable);
					highlight.chestHighlights.remove(chestHighlight);
				});
			} else {
				if (!doesHaveHighlight) {
					return;
				}

				highlightChest(blockPos, highlight.filter.getColor());
			}
		});
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
		ItemHighlight itemHighlight = ItemHighlight.createNew(filter);
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
		getHighlight().ifPresent(itemHighlight -> {
			if (itemHighlight.hasHighlight(blockPos)) {
				return;
			}

			final ChestHighlight chestHighlight = new ChestHighlight(
					blockPos,
					new CallbackRemovable(new BlockHighlight(blockPos, color), itemHighlight.remove));
			itemHighlight.chestHighlights.add(chestHighlight);
			WorldRender.addRenderable(chestHighlight.renderable);
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

	private record ChestHighlight(BlockPos blockPos, Renderable renderable) {
	}

	private record ItemHighlight(List<ItemStack> modified, CompletableFuture<Boolean> remove,
								 ItemSearchFilter filter, List<ChestHighlight> chestHighlights) {

		public static ItemHighlight createNew(ItemSearchFilter filter) {
			return new ItemHighlight(new ArrayList<>(), new CompletableFuture<>(), filter, new ArrayList<>());
		}

		public boolean hasHighlight(BlockPos blockPos) {
			return chestHighlights.stream().map(ChestHighlight::blockPos)
					.anyMatch(blockPos::equals);
		}

		public Optional<ChestHighlight> getHighlight(BlockPos blockPos) {
			return this.chestHighlights.stream()
					.filter(highlight -> blockPos.equals(highlight.blockPos))
					.findFirst();
		}
	}
}
