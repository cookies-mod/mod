package dev.morazzer.cookies.mod.data.profile.sub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Data that contains all items that are currently in the storage of the selected profile.
 */
public class StorageData implements JsonSerializable {
    private static final Codec<StorageDataEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("slot").forGetter(StorageDataEntry::slot),
            ItemStack.CODEC.fieldOf("item_stack").forGetter(StorageDataEntry::itemStack))
        .apply(instance, StorageDataEntry::new));
    private static final Codec<List<StorageDataEntry>> LIST_CODEC = CODEC.listOf();
    private final List<List<StorageDataEntry>> enderChestItems = Arrays.asList(new List[9]);
    private final List<List<StorageDataEntry>> backpackItems = Arrays.asList(new List[18]);

    /**
     * Saves the contents of a specific page.
     *
     * @param itemStacks   The content of the page.
     * @param page         The page.
     * @param isEnderChest Whether to save it as enderchest or backpack.
     */
    public void saveItems(List<StorageDataEntry> itemStacks, int page, boolean isEnderChest) {
        (isEnderChest ? enderChestItems : backpackItems).set(page, itemStacks);
    }

    /**
     * Gets the content of a specific page of the storage.
     *
     * @param page         The page to get.
     * @param isEnderChest Whether to get the enderchest or the backpack.
     * @return The page.
     */
    public List<StorageDataEntry> getItems(int page, boolean isEnderChest) {
        return (isEnderChest ? enderChestItems : backpackItems).get(page);
    }

    /**
     * Gets all items across both ender chest and backpacks.
     *
     * @return All items.
     */
    public List<StorageDataEntry> getAllItems() {
        List<StorageDataEntry> items = new ArrayList<>();
        enderChestItems.stream().filter(Objects::nonNull).flatMap(List::stream).forEach(items::add);
        backpackItems.stream().filter(Objects::nonNull).flatMap(List::stream).forEach(items::add);
        return items;
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) {
            return;
        }

        process(jsonElement.getAsJsonObject().getAsJsonObject("ender_chest"), enderChestItems);
        process(jsonElement.getAsJsonObject().getAsJsonObject("backpack"), backpackItems);
    }

    private void process(JsonObject storage, List<List<StorageDataEntry>> itemsList) {
        if (storage == null) {
            return;
        }
        for (String s : storage.keySet()) {
            final int index;

            try {
                index = Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                continue;
            }

            final JsonElement jsonElement = storage.get(s);
            if (!jsonElement.isJsonArray()) {
                continue;
            }
            final DataResult<List<StorageDataEntry>> parse =
                LIST_CODEC.parse(JsonOps.INSTANCE, jsonElement.getAsJsonArray());
            if (parse.isError() || !parse.isSuccess()) {
                continue;
            }
            final List<StorageDataEntry> values = parse.getOrThrow();
            itemsList.set(index, values);
        }
    }

    @Override
    public @NotNull JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("ender_chest", save(this.enderChestItems));
        jsonObject.add("backpack", save(this.backpackItems));
        return jsonObject;
    }

    private JsonElement save(List<List<StorageDataEntry>> itemList) {
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < itemList.size(); i++) {
            final List<StorageDataEntry> itemStacks = itemList.get(i);
            if (itemStacks == null) {
                continue;
            }
            itemStacks.forEach(stack -> stack.itemStack.remove(DataComponentTypes.ENCHANTMENTS));
            itemStacks.forEach(stack -> stack.itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE));
            final DataResult<JsonElement> jsonElementDataResult = LIST_CODEC.encodeStart(JsonOps.INSTANCE, itemStacks);
            if (jsonElementDataResult.isSuccess()) {
                jsonObject.add(String.valueOf(i), jsonElementDataResult.getOrThrow());
            }
        }
        return jsonObject;
    }

    /**
     * An entry for the storage data.
     *
     * @param slot      The slot the item is in.
     * @param itemStack The item.
     */
    public record StorageDataEntry(int slot, ItemStack itemStack) {}
}
