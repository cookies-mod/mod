package codes.cookies.mod.features.misc.utils.crafthelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.data.profile.items.sources.ForgeItemSource;
import codes.cookies.mod.repository.RepositoryItem;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Pair;

public class ItemTracker {

	private final Map<RepositoryItem, TrackedItem> trackedItems = new HashMap<>();

	public ItemTracker(ItemSources... sources) {
		final Map<RepositoryItem, List<Item<?>>> collect = ItemSources.getItems(sources)
				.stream()
				.collect(Collectors.groupingBy(RepositoryItem.getMappedOrEmpty(Item::itemStack)));
		collect.forEach((repositoryItem, items) -> trackedItems.put(
				repositoryItem,
				new TrackedItem(repositoryItem, items)));
	}

	public ItemTracker(ItemTracker itemTracker) {
		itemTracker.trackedItems.forEach((repositoryItem, trackedItem) -> trackedItems.put(
				repositoryItem,
				trackedItem.copy()));
	}

	public int getAmount(RepositoryItem repositoryItem) {
		if (trackedItems.containsKey(repositoryItem)) {
			return trackedItems.get(repositoryItem).amount;
		}
		return 0;
	}

	public int take(RepositoryItem repositoryItem, int amount) {
		if (trackedItems.containsKey(repositoryItem)) {
			return trackedItems.get(repositoryItem).take(amount);
		}
		return 0;
	}

	public ItemTracker copy() {
		return new ItemTracker(this);
	}

	public TrackedItem get(RepositoryItem repositoryItem) {
		return trackedItems.getOrDefault(repositoryItem, TrackedItem.EMPTY);
	}

	public static class TrackedItem {
		public static TrackedItem EMPTY = new TrackedItem(RepositoryItem.EMPTY);

		@NotNull
		private final RepositoryItem repositoryItem;
		private final Map<ItemSources, List<Item<?>>> sources = new HashMap<>();
		private final Map<ItemSources, Integer> sourceAmounts = new HashMap<>();
		@Getter
		private int amount = 0;
		@Getter
		private int consumed = 0;

		public TrackedItem(@NotNull RepositoryItem repositoryItem) {
			this.repositoryItem = repositoryItem;
		}


		public TrackedItem(@NotNull RepositoryItem repositoryItem, List<Item<?>> items) {
			this.repositoryItem = repositoryItem;
			for (Item<?> item : items) {
				if (item.source() == ItemSources.CRAFTABLE) {
					continue; // Not supported in craft helper, since we basically do the same anyway
				}
				sources.computeIfAbsent(item.source(), s -> new ArrayList<>());
				sourceAmounts.computeIfPresent(item.source(), (itemSources, integer) -> integer + item.amount());
				sourceAmounts.computeIfAbsent(item.source(), s -> item.amount());
				sources.get(item.source()).add(item);
				amount += item.amount();
			}
		}

		public TrackedItem(TrackedItem trackedItem) {
			this.repositoryItem = trackedItem.repositoryItem;
			this.amount = trackedItem.amount;
			this.consumed = trackedItem.consumed;
			this.sources.putAll(trackedItem.sources);
			this.sourceAmounts.putAll(trackedItem.sourceAmounts);
		}

		public int take(int max) {
			int remaining = amount - consumed;
			if (remaining > max) {
				consumed += max;
				return max;
			}

			consumed = amount;
			return remaining;
		}

		private int getAmountBefore(ItemSources source) {
			return sourceAmounts.keySet()
					.stream()
					.filter(current -> getPriority(current) < getPriority(source))
					.map(sourceAmounts::get)
					.filter(Objects::nonNull)
					.mapToInt(Integer::intValue)
					.sum();
		}

		public boolean hasUsed(ItemSources source) {
			if (!sourceAmounts.containsKey(source) || sourceAmounts.getOrDefault(source, 0) == 0) {
				return false;
			}

			final int sum = getAmountBefore(source);
			return consumed > sum;
		}

		public List<ForgeItemSource.Context> getAllForgeStart() {
			if (!hasUsed(ItemSources.FORGE)) {
				return Collections.emptyList();
			}

			final int amountBefore = getAmountBefore(ItemSources.FORGE);
			final int forgeAmount = sourceAmounts.getOrDefault(ItemSources.FORGE, 0);
			int index = Math.min(consumed - amountBefore, forgeAmount - 1);
			final List<Item<?>> orDefault = this.sources.getOrDefault(ItemSources.FORGE, Collections.emptyList());
			if (orDefault.size() - 1 < index) {
				return Collections.emptyList();
			}

			return orDefault.stream().limit(index + 1)
					.map(Item::data)
					.map(ForgeItemSource.Context.class::cast)
					.sorted(Comparator.comparingInt(ForgeItemSource.Context::slot))
					.toList();
		}

		public OptionalLong getLastForgeStarted() {
			if (!hasUsed(ItemSources.FORGE)) {
				return OptionalLong.empty();
			}

			final int amountBefore = getAmountBefore(ItemSources.FORGE);
			final int forgeAmount = sourceAmounts.getOrDefault(ItemSources.FORGE, 0);
			int index = Math.min(consumed - amountBefore, forgeAmount - 1);
			final List<Item<?>> orDefault = this.sources.getOrDefault(ItemSources.FORGE, Collections.emptyList());
			if (orDefault.size() - 1 < index) {
				return OptionalLong.empty();
			}
			return OptionalLong.of(((ForgeItemSource.Context) orDefault.get(index).data()).startTime());
		}

		public int getPriority(ItemSources source) {
			return switch (source) {
				case INVENTORY -> 0;
				case SACKS -> 1;
				case STORAGE -> 2;
				case SACK_OF_SACKS -> 3;
				case VAULT -> 4;
				case POTION_BAG -> 5;
				case ACCESSORY_BAG -> 6;
				case FORGE -> 7;
				case CHESTS -> 8;
				default -> -1;
			};
		}

		public TrackedItem copy() {
			return new TrackedItem(this);
		}

		public List<Pair<ItemSources, Integer>> getUsedSources(int amount) {
			final int preConsume = this.consumed - amount;
			int consumed = 0;
			int currentAmount = 0;
			List<Pair<ItemSources, Integer>> sources = new ArrayList<>();
			for (ObjectIntPair<ItemSources> pair : this.sourceAmounts.keySet().stream()
					.sorted(Comparator.comparingInt(this::getPriority))
					.map(source -> ObjectIntPair.of(source, this.sourceAmounts.getOrDefault(source, 0)))
					.toList()) {
				if ((currentAmount + pair.rightInt()) > preConsume && consumed != amount) {
					int available = pair.rightInt() - (currentAmount - consumed);
					if (available <= (amount - consumed)) {
						sources.add(new Pair<>(pair.left(), available));
						consumed += available;
					} else {
						sources.add(new Pair<>(pair.left(), (amount - consumed)));
						consumed = amount;
					}
				}
				currentAmount += pair.rightInt();
			}
			return sources;
		}
	}

}
