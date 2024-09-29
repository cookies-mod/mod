package dev.morazzer.cookies.mod.data.profile.sub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.json.CodecJsonSerializable;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data that contains all items that are currently in the storage of the selected profile.
 */
public class StorageData implements CodecJsonSerializable<List<StorageData.StorageItem>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageData.class);
    private static final ItemStack VOID_ITEM = new ItemStack(Items.DEBUG_STICK);
    private final List<StorageItem> items = new ArrayList<>();

    /**
     * Saves the contents of a specific page.
     *
     * @param itemStacks The content of the page.
     * @param page       The page.
     * @param location   The location the item is in.
     */
    public void saveItems(List<Pair<Integer, ItemStack>> itemStacks, int page, StorageLocation location) {
        this.items.removeAll(this.getItems(page, location));
        itemStacks.stream().map(pair -> this.createItem(pair.getRight(), page, pair.getLeft(), location)).forEach(this.items::add);
    }


	public StorageItem createItem(ItemStack itemStack, int page, int slot, StorageLocation location) {
		return StorageItem.create(location, page, slot, itemStack);
	}

	public void saveItem(ItemStack itemStack, int page, int slot, StorageLocation location) {
		this.removeItem(page, slot, location);
		this.items.add(this.createItem(itemStack, page, slot, location));
	}

	public void removeItem(int page, int slot, StorageLocation location) {
		this.items.remove(this.getItem(page, slot, location));
	}

	/**
     * Gets the content of a specific page of the storage.
     *
     * @param page     The page to get.
     * @param location The location to search in.
     * @return The page.
     */
    public List<StorageItem> getItems(int page, StorageLocation location) {
        return this.items.stream()
            .filter(item -> item.storageLocation() == location)
            .filter(item -> item.page() == page)
            .toList();
    }

	public StorageItem getItem(int page, int slot, StorageLocation location) {
		return this.items.stream()
				.filter(item -> item.storageLocation() == location)
				.filter(item -> item.page() == page)
				.filter(item -> item.slot() == slot)
				.findFirst().orElse(null);
	}

    /**
     * Gets all items across both ender chest and backpacks.
     *
     * @return All items.
     */
    public List<StorageItem> getAllItems() {
        return this.items;
    }

    public void clear() {
        this.items.clear();
    }

	@Override
	public Codec<List<StorageItem>> getCodec() {
		return StorageItem.LIST_CODEC;
	}

	@Override
	public void load(List<StorageItem> value) {
		this.items.addAll(value);
	}

	@Override
	public List<StorageItem> getValue() {
		return this.items;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@Getter@AllArgsConstructor
    public enum StorageLocation implements StringIdentifiable {
        ENDER_CHEST(TranslationKeys.ITEM_SOURCE_ENDERCHEST),
        BACKPACK(TranslationKeys.ITEM_SOURCE_BACKPACK),;

		private final String translationKey;
        public static final Codec<StorageLocation> CODEC = StringIdentifiable.createCodec(StorageLocation::values);

        @Override
        public String asString() {
            return this.name();
        }
    }

    public record StorageItem(StorageLocation storageLocation, int page, int slot, ItemStack itemStack) {
		private static final Codec<StorageItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				StorageLocation.CODEC.fieldOf("location").forGetter(StorageItem::storageLocation),
				Codec.INT.fieldOf("page").forGetter(StorageItem::page),
				Codec.INT.fieldOf("slot").forGetter(StorageItem::slot),
				ItemStack.CODEC.fieldOf("item").forGetter(StorageItem::getActualItemStack)).apply(instance, StorageItem::create));
		private static final Codec<List<StorageItem>> LIST_CODEC = CODEC.listOf();

		public static StorageItem create(StorageLocation location, int page, int slot, ItemStack itemStack) {
            itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE);
            itemStack.remove(DataComponentTypes.ENCHANTMENTS);
            if (itemStack.isEmpty()) {
                return new StorageItem(location, page, slot, VOID_ITEM);
            } else {
                return new StorageItem(location, page, slot, itemStack);
            }
        }

        @Override
        public ItemStack itemStack() {
            if (this.itemStack.isOf(Items.DEBUG_STICK)) {
                return ItemStack.EMPTY;
            }
            return this.itemStack;
        }

        public ItemStack getActualItemStack() {
            if (this.itemStack.isEmpty()) {
                return VOID_ITEM;
            }
            return this.itemStack;
        }
    }
}
