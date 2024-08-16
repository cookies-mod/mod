package dev.morazzer.cookies.mod.data.profile.sub;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;

/**
 * Data that contains all items that are currently in the storage of the selected profile.
 */
public class StorageData implements JsonSerializable {
    private static final ItemStack VOID_ITEM = new ItemStack(Items.DEBUG_STICK);

    private static final Codec<StorageItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StorageLocation.CODEC.fieldOf("location").forGetter(StorageItem::storageLocation),
        Codec.INT.fieldOf("page").forGetter(StorageItem::page),
        Codec.INT.fieldOf("slot").forGetter(StorageItem::slot),
        ItemStack.CODEC.fieldOf("item").forGetter(StorageItem::itemStack)).apply(instance, StorageItem::create));

    private static final Codec<List<StorageItem>> LIST_CODEC = CODEC.listOf();
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
        itemStacks.stream().map(pair -> StorageItem.create(location, page, pair.getLeft(), pair.getRight())).forEach(this.items::add);
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

    /**
     * Gets all items across both ender chest and backpacks.
     *
     * @return All items.
     */
    public List<StorageItem> getAllItems() {
        return this.items;
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        final JsonArray asJsonArray = jsonElement.getAsJsonArray();
        final DataResult<List<StorageItem>> parse = LIST_CODEC.parse(JsonOps.INSTANCE, asJsonArray);

        final Optional<List<StorageItem>> storageItems = parse.resultOrPartial();
        if (storageItems.isEmpty()) {
            return;
        }
        final List<StorageItem> items = storageItems.get();
        this.items.addAll(items);
    }

    @Override
    public @NotNull JsonElement write() {
        final DataResult<JsonElement> jsonElementDataResult = LIST_CODEC.encodeStart(JsonOps.INSTANCE, this.items);
        if (jsonElementDataResult.isError()) {
            final DataResult.Error<JsonElement> jsonElementError = jsonElementDataResult.error().get();
            ExceptionHandler.handleException(new RuntimeException(jsonElementError.message()));
        }
        final Optional<JsonElement> jsonElement = jsonElementDataResult.resultOrPartial();
        return Optional.ofNullable(jsonElement).orElseGet(Optional::empty).orElseGet(JsonArray::new);
    }

    public void clear() {
        this.items.clear();
    }


    public enum StorageLocation implements StringIdentifiable {
        ENDER_CHEST,
        BACKPACK;

        public static final Codec<StorageLocation> CODEC = StringIdentifiable.createCodec(StorageLocation::values);

        @Override
        public String asString() {
            return name();
        }
    }

    public record StorageItem(StorageLocation storageLocation, int page, int slot, ItemStack itemStack) {
        public static StorageItem create(StorageLocation location, int page, int slot, ItemStack itemStack) {
            itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE);
            itemStack.remove(DataComponentTypes.ENCHANTMENTS);
            if (itemStack.isEmpty()) {
                return new StorageItem(location, page, slot, VOID_ITEM);
            } else if (itemStack.isOf(Items.DEBUG_STICK)) {
                return new StorageItem(location, page, slot, ItemStack.EMPTY);
            } else {
                return new StorageItem(location, page, slot, itemStack);
            }
        }

        @Override
        public ItemStack itemStack() {
            if (itemStack.isOf(Items.DEBUG_STICK)) {
                return ItemStack.EMPTY;
            }
            return itemStack;
        }
    }
}
