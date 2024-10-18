package dev.morazzer.cookies.mod.data.profile.items;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dev.morazzer.cookies.mod.data.profile.items.sources.CraftableItemSource;
import dev.morazzer.cookies.mod.data.profile.items.sources.StorageItemSource;
import dev.morazzer.cookies.mod.data.profile.profile.IslandChestStorage;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;

/**
 * Creates an item compound to store multiple {@link Item} at once.
 */
public final class ItemCompound {
	private final ItemStack itemStack;
	private final Set<Item<?>> items;
	private int amount;
	private CompoundType type;
	private Object data;

	public ItemCompound(Item<?>... items) {
		this(Arrays.stream(items).mapToInt(Item::amount).sum(), items[0].itemStack(), new HashSet<>(Set.of(items)));
	}

	public ItemCompound(int amount, ItemStack itemStack, Set<Item<?>> items) {
		this.amount = amount;
		this.itemStack = itemStack;
		this.items = items;
		this.handleAny(items.iterator().next());
	}

	public void handleAny(Item<?> item) {
		if (this.type == CompoundType.MULTIPLE) {
			return;
		}
		if (this.type != null && !ArrayUtils.contains(CompoundType.getFor(item.source()), this.type)) {
			this.type = CompoundType.MULTIPLE;
			this.data = Unit.INSTANCE;
			return;
		}
		switch (item.source()) {
			case SACKS -> this.setSacksType();
			case CHESTS -> this.handleChest(item);
			case STORAGE -> this.handleStorage(item);
			case CRAFTABLE -> {
				this.type = CompoundType.CRAFTABLE;
				this.data = item.data();
			}
			default -> {
				this.type = CompoundType.of(item.source(), null);
				this.data = Unit.INSTANCE;
			}
		}
	}

	private void setSacksType() {
		this.type = CompoundType.SACKS;
		this.data = Unit.INSTANCE;
	}

	private void handleChest(Item<?> item) {
		final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) item.data();
		if (this.type == null) {
			this.type = CompoundType.CHEST_POS;
			this.data = data;
			return;
		}

		if (data.blockPos() == this.data || data.secondChest() == this.data) {
			return;
		}

		this.type = CompoundType.CHEST;
		this.data = Unit.INSTANCE;
	}

	private void handleStorage(Item<?> item) {
		final StorageItemSource.Context data = (StorageItemSource.Context) item.data();
		if (this.type == null) {
			this.type = CompoundType.STORAGE_PAGE;
			this.data = data;
			return;
		}

		if (data.pageEquals(this.data)) {
			return;
		}

		this.type = CompoundType.STORAGE;
		this.data = Unit.INSTANCE;
	}

	public void add(Item<?> item) {
		if (item.source() == ItemSources.CRAFTABLE) {
			return;
		}
		if (this.type == CompoundType.CRAFTABLE) {
			this.type = null;
			this.data = null;
			this.items.clear();
		}
		if (this.items.contains(item)) {
			return;
		}
		this.handleAny(item);
		this.items.add(item);
		this.amount += item.amount();
	}

	public int amount() {return this.amount;}

	public ItemStack itemStack() {return this.itemStack;}

	public Set<Item<?>> items() {
		return this.items;
	}

	public Set<Item<?>> getUsedItems() {
		if (this.type == CompoundType.CRAFTABLE && this.data instanceof CraftableItemSource.Data craftData) {
			return craftData.amounts()
					.values()
					.stream()
					.filter(CraftableItemSource.IngredientData::shouldBeIncluded)
					.map(CraftableItemSource.IngredientData::items)
					.flatMap(List::stream)
					.filter(Predicate.not(item -> item.source().isSupportsSupercraft()))
					.collect(Collectors.toSet());
		}
		return items;
	}

	public CompoundType type() {return this.type;}

	public Object data() {return this.data;}

	public String name() {return this.itemStack.getName().getString();}

	@Override
	public int hashCode() {
		return Objects.hash(this.amount, this.itemStack, this.items);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (ItemCompound) obj;
		return this.amount == that.amount && Objects.equals(this.itemStack, that.itemStack) &&
			   Objects.equals(this.items, that.items);
	}

	@Override
	public String toString() {
		return "ItemCompound[" + "amount=" + this.amount + ", " + "itemStack=" + this.itemStack + ", " + "items=" +
			   this.items + ']';
	}

	public enum CompoundType {
		CHEST_POS,
		CHEST,
		STORAGE_PAGE,
		STORAGE,
		SACKS,
		MULTIPLE,
		ACCESSORIES,
		SACK_OF_SACKS,
		VAULT,
		POTION_BAG,
		CRAFTABLE;

		public static CompoundType[] getFor(ItemSources itemSources) {
			return switch (itemSources) {
				case STORAGE -> new CompoundType[] {STORAGE, STORAGE_PAGE};
				case CHESTS -> new CompoundType[] {CHEST, CHEST_POS};
				case SACKS -> new CompoundType[] {SACKS};
				case VAULT -> new CompoundType[] {VAULT};
				case POTION_BAG -> new CompoundType[] {POTION_BAG};
				case ACCESSORY_BAG -> new CompoundType[] {ACCESSORIES};
				case SACK_OF_SACKS -> new CompoundType[] {SACK_OF_SACKS};
				case CRAFTABLE -> new CompoundType[] {CRAFTABLE};
				case null, default -> new CompoundType[0];
			};
		}

		public static CompoundType of(ItemSources source, Object data) {
			return switch (source) {
				case SACKS -> SACKS;
				case CHESTS -> data != null ? CHEST_POS : CHEST;
				case STORAGE -> data != null ? STORAGE_PAGE : STORAGE;
				case VAULT -> VAULT;
				case SACK_OF_SACKS -> SACK_OF_SACKS;
				case POTION_BAG -> POTION_BAG;
				case ACCESSORY_BAG -> ACCESSORIES;
				case CRAFTABLE -> CRAFTABLE;
				case null, default -> null;
			};
		}
	}


}
