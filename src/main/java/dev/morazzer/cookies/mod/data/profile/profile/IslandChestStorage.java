package dev.morazzer.cookies.mod.data.profile.profile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.utils.json.CodecJsonSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Optional;

import lombok.Getter;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The data for the island chests.
 */
@Getter
public class IslandChestStorage implements CodecJsonSerializable<List<IslandChestStorage.ChestItem>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IslandChestStorage.class);
	private final List<ChestItem> items = new ArrayList<>();

	public void save(BlockPos blockPos, BlockPos secondChest, ItemStack stack, int slot) {
		this.items.add(ChestItem.create(blockPos, secondChest, stack, slot));
	}

	public void removeBlockSlot(BlockPos blockPos, int slot) {
		this.items.removeIf(item -> item.blockPos.equals(blockPos) && item.slot == slot);
	}

	public void remove(ChestItem chestItem) {
		this.items.remove(chestItem);
	}

	public void removeBlock(BlockPos blockPos) {
		this.items.removeIf(item -> item.blockPos.equals(blockPos));
		final Iterator<ChestItem> iterator = this.items.iterator();
		List<ChestItem> toAdd = new ArrayList<>();
		while (iterator.hasNext()) {
			final ChestItem chestItem = iterator.next();
			if (chestItem.secondChest != null && chestItem.secondChest.isPresent() && chestItem.secondChest.get().equals(blockPos)) {
				iterator.remove();
				toAdd.add(ChestItem.create(chestItem.blockPos, null, chestItem.itemStack, chestItem.slot));
			}
		}
		this.items.addAll(toAdd);
	}

	public void clear() {
		this.items.clear();
	}

	@Override
	public Codec<List<ChestItem>> getCodec() {
		return ChestItem.LIST_CODEC;
	}

	@Override
	public void load(List<ChestItem> value) {
		this.items.addAll(value);
	}

	@Override
	public List<ChestItem> getValue() {
		return this.items;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
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
