package codes.cookies.mod.data.profile.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import codes.cookies.mod.events.ChestSaveEvent;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

/**
 * The data for the island chests.
 */
@Getter
public class IslandChestStorage implements CodecJsonSerializable<List<IslandChestStorage.ChestItem>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IslandChestStorage.class);
	private final List<ChestItem> items = new ArrayList<>();

	public void save(BlockPos blockPos, BlockPos secondChest, ItemStack stack, int slot) {
		Optional.ofNullable(ChestItem.create(blockPos, secondChest, stack, slot))
				.ifPresent(items::add);
	}

	private List<ChestItem> getItems(BlockPos pos) {
		return this.items.stream().filter(chestItem -> pos.equals(chestItem.blockPos))
				.toList();
	}

	public void removeBlockSlot(BlockPos blockPos, int slot) {
		this.items.removeIf(item -> blockPos.equals(item.blockPos) && item.slot == slot);
	}

	public void remove(ChestItem chestItem) {
		this.items.remove(chestItem);
	}

	public void removeBlock(BlockPos blockPos) {
		this.items.removeIf(item -> blockPos.equals(item.blockPos));
		final Iterator<ChestItem> iterator = this.items.iterator();
		List<ChestItem> toAdd = new ArrayList<>();
		while (iterator.hasNext()) {
			final ChestItem chestItem = iterator.next();
			if (chestItem.secondChest != null && chestItem.secondChest.isPresent() && chestItem.secondChest.get()
					.equals(blockPos)) {
				iterator.remove();
				Optional.ofNullable(ChestItem.create(chestItem.blockPos, null, chestItem.itemStack, chestItem.slot))
						.ifPresent(toAdd::add);
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

	public void sendEvent(BlockPos blockPos, BlockPos second) {
		if (blockPos != null && second != null) {
			final ArrayList<ChestItem> bothItems = new ArrayList<>();
			bothItems.addAll(this.getItems(blockPos));
			bothItems.addAll(this.getItems(second));
			ChestSaveEvent.EVENT.invoker().onSave(blockPos, second, bothItems);
			return;
		}
		if (blockPos != null) {
			ChestSaveEvent.EVENT.invoker().onSave(blockPos, second,this.getItems(blockPos));
		}
	}

	public record ChestItem(BlockPos blockPos, Optional<BlockPos> secondChest, ItemStack itemStack, int slot) {
		public static Codec<ChestItem> CODEC =
				RecordCodecBuilder.create(instance -> instance.group(
								BlockPos.CODEC.fieldOf("block_pos")
										.forGetter(ChestItem::blockPos),
								BlockPos.CODEC.optionalFieldOf("second_chest").forGetter(ChestItem::secondChest),
								ItemStack.CODEC.fieldOf("item").forGetter(ChestItem::itemStack),
								Codecs.NONNEGATIVE_INT.fieldOf("slot").forGetter(ChestItem::slot))
						.apply(instance, ChestItem::new));
		private static final Codec<List<ChestItem>> LIST_CODEC = CODEC.listOf();

		public static ChestItem create(BlockPos pos, BlockPos secondChest, ItemStack itemStack, int slot) {
			if (pos == null) {
				return null;
			}
			itemStack.remove(DataComponentTypes.ENCHANTMENTS);
			itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE);
			return new ChestItem(pos, Optional.ofNullable(secondChest), itemStack, slot);
		}
	}
}
