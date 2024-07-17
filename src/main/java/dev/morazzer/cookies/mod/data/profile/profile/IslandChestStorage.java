package dev.morazzer.cookies.mod.data.profile.profile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * The data for the island chests.
 */
public class IslandChestStorage implements JsonSerializable {

    private static final Codec<Map<ItemSearchService.BiBlockPosKey, List<ItemStack>>> CODEC =
        Codec.unboundedMap(ItemSearchService.BiBlockPosKey.CODEC, ItemStack.CODEC.listOf());
    private final Map<ItemSearchService.BiBlockPosKey, List<ItemStack>> chest;

    /**
     * Creates a new instance of the island chest data.
     */
    public IslandChestStorage() {
        chest = new HashMap<>();
    }

    /**
     * Adds the chest to the cached data.
     *
     * @param first  The first block of the chest.
     * @param second The second block of the chest (or null if not available).
     * @param stacks The item stacks that are in the chest.
     */
    public void add(BlockPos first, BlockPos second, List<ItemStack> stacks) {
        this.chest.keySet().removeIf(key -> key.equals(first) || key.equals(second));
        this.chest.put(new ItemSearchService.BiBlockPosKey(first, second), stacks);
    }

    /**
     * Gets all items from the item storage.
     *
     * @return The items.
     */
    public List<ItemSearchService.IslandItems> getItems() {
        return chest.entrySet()
            .stream()
            .map(entry -> new ItemSearchService.IslandItems(entry.getKey(), entry.getValue()))
            .toList();
    }

    /**
     * Gets all items at the provided block pos.
     * @param blockPos The block pos.
     * @return The items.
     */
    public Optional<List<ItemStack>> get(BlockPos blockPos) {
        return chest.entrySet()
            .stream()
            .filter(entry -> entry.getKey().equals(blockPos))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            final DataResult<Map<ItemSearchService.BiBlockPosKey, List<ItemStack>>> parse =
                CODEC.parse(JsonOps.INSTANCE, jsonElement);
            if (parse.isSuccess()) {
                final Map<ItemSearchService.BiBlockPosKey, List<ItemStack>> orThrow = parse.getOrThrow();
                chest.putAll(orThrow);
            }
        }
    }

    @Override
    public @NotNull JsonElement write() {
        chest.values().stream().flatMap(List::stream).forEach(itemStack -> {
            itemStack.remove(DataComponentTypes.ENCHANTMENTS);
            itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE);
        });
        final DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, chest);
        if (result.isError()) {
            return new JsonObject();
        }
        return result.getOrThrow();
    }

    /**
     * Removes all items from the block pos.
     * @param pos The block pos.
     */
    public void remove(BlockPos pos) {
        this.chest.keySet().removeIf(biBlockPosKey -> biBlockPosKey.equals(pos));
    }
}
