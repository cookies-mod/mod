package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.items.sources.ForgeItemSource;
import dev.morazzer.cookies.mod.generated.utils.ItemAccessor;
import dev.morazzer.cookies.mod.repository.RepositoryItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;

@SuppressWarnings("MissingJavadoc")
public class StackCountContext {
	private static final ItemSources[] ITEM_SOURCES =
			{ItemSources.INVENTORY, ItemSources.SACKS, ItemSources.STORAGE, ItemSources.FORGE};

	Map<RepositoryItem, Long> itemMap = new HashMap<>();
	Stack<Integer> integers = new Stack<>();
	Map<RepositoryItem, ForgeCountContext> lastForgeStart = new HashMap<>();
	private final ItemSources[] itemSources;

	public StackCountContext(ItemSources... itemSources) {
		this.integers.push(0);
		this.itemSources = itemSources;
	}

	public StackCountContext() {
		this(ITEM_SOURCES);
	}

	public int take(RepositoryItem id, int max) {
		if (id == null) {
			return 0;
		}

		if (!this.itemMap.containsKey(id)) {
			this.itemMap.put(
					id,
					ItemSources.getItems(this.itemSources)
							.stream()
							.filter(item -> id.equals(ItemAccessor.repositoryItemOrNull(item.itemStack())))
							.peek(this.addLastForge(id))
							.mapToLong(Item::amount)
							.sum());
		}

		long l = this.itemMap.getOrDefault(id, 0L);
		int used = (int) (l >> 32);
		int total = (int) l;
		int available = total - used;
		if ((available - this.getThroughForge(id)) < max) {
			int forgeUsed = max - (available - this.getThroughForge(id));
			this.takeForge(id, forgeUsed);
		}
		if (max >= available) {
			this.itemMap.put(id, ((long) total << 32) | total);
			return available;
		}

		used += max;
		l = l & 0xFFFFFFFFL;
		l |= (long) used << 32;
		this.itemMap.put(id, l);
		return max;
	}

	public Optional<ForgeCountContext> getForgeCountContext(RepositoryItem item) {
		return Optional.ofNullable(this.lastForgeStart.get(item));
	}

	private void takeForge(RepositoryItem id, int forgeUsed) {
		this.getForgeCountContext(id).ifPresent(context -> context.take(forgeUsed));
	}

	public int getThroughForge(RepositoryItem id) {
		return this.getForgeCountContext(id).map(ForgeCountContext::getAvailable).orElse(0);
	}

	private Consumer<Item<?>> addLastForge(RepositoryItem repositoryItem) {
		return item -> {
			if (item.source() != ItemSources.FORGE) {
				return;
			}

			final ForgeCountContext forgeCountContext =
					this.getForgeCountContext(repositoryItem).orElseGet(ForgeCountContext::new);

			final ForgeItemSource.Context data = (ForgeItemSource.Context) item.data();
			forgeCountContext.addStartTime(data.startTime());
			this.lastForgeStart.put(repositoryItem, forgeCountContext);
		};
	}

	public boolean usedForge(RepositoryItem id) {
		return this.getLastTimeStarted(id) > 0;
	}

	public long getLastTimeStarted(RepositoryItem id) {
		return this.getForgeCountContext(id).map(ForgeCountContext::getLastTimeStarted).orElse(-1L);
	}
}
