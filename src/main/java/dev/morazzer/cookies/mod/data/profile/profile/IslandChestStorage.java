package dev.morazzer.cookies.mod.data.profile.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Optional;

import lombok.Getter;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The data for the island chests.
 */
@Getter
public class IslandChestStorage implements JsonSerializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(IslandChestStorage.class);
	private final List<ChestItem> items = new ArrayList<>();

	@Override
	public void read(@NotNull JsonElement jsonElement) {
		try {
			if (jsonElement.isJsonArray()) {
				final DataResult<List<ChestItem>> parse = ChestItem.LIST_CODEC.parse(JsonOps.INSTANCE, jsonElement);
				if (parse.isSuccess()) {
					this.items.addAll(parse.getOrThrow());
				} else {
					LOGGER.warn("Failed to load island chest data, trying to load partial. {}",
							parse.error().get().message());
					try {
						this.items.addAll(parse.getOrThrow());
					} catch (Exception e) {
						LOGGER.error("Failed to load partial data, continuing with empty list.");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public @NotNull JsonElement write() {
		final DataResult<JsonElement> result = ChestItem.LIST_CODEC.encodeStart(JsonOps.INSTANCE, this.items);
		if (result.isError()) {
			LOGGER.warn("Failed to save island chest data! {}", result.error().get().message());
			return new JsonArray();
		}
		return result.getOrThrow();
	}

	public void save(BlockPos blockPos, BlockPos secondChest, ItemStack stack, int slot) {
		this.items.add(ChestItem.create(blockPos, secondChest, stack, slot));
	}

	public void remove(BlockPos blockPos, int slot) {
		this.items.removeIf(item -> item.blockPos.equals(blockPos) && item.slot == slot);
	}

	public void remove(ChestItem chestItem) {
		this.items.remove(chestItem);
	}

	public void remove(BlockPos blockPos) {
		this.items.removeIf(item -> item.blockPos.equals(blockPos));
		final Iterator<ChestItem> iterator = this.items.iterator();
		List<ChestItem> toAdd = new ArrayList<>();
		while (iterator.hasNext()) {
			final ChestItem chestItem = iterator.next();
			if (chestItem.secondChest != null && chestItem.secondChest.equals(blockPos)) {
				iterator.remove();
				toAdd.add(ChestItem.create(chestItem.blockPos, null, chestItem.itemStack, chestItem.slot));
			}
		}
		this.items.addAll(toAdd);
	}

	public void clear() {
		this.items.clear();
	}

	public record ChestItem(BlockPos blockPos, Optional<BlockPos> secondChest, ItemStack itemStack, int slot) {
		public static ChestItem create(BlockPos pos, BlockPos secondChest, ItemStack itemStack, int slot) {
			itemStack.remove(DataComponentTypes.ENCHANTMENTS);
			itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE);
			return new ChestItem(pos, Optional.ofNullable(secondChest), itemStack, slot);
		}

		public static Codec<ChestItem> CODEC =
				RecordCodecBuilder.create(instance -> instance.group(BlockPos.CODEC.fieldOf("block_pos")
										.forGetter(ChestItem::blockPos),
								BlockPos.CODEC.optionalFieldOf("second_chest").forGetter(ChestItem::secondChest),
								ItemStack.CODEC.fieldOf("item").forGetter(ChestItem::itemStack),
								Codecs.NONNEGATIVE_INT.fieldOf("slot").forGetter(ChestItem::slot))
						.apply(instance, ChestItem::new));
		private static final Codec<List<ChestItem>> LIST_CODEC = CODEC.listOf();
	}
}
