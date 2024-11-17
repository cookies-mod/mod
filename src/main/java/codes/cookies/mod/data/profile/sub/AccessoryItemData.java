package codes.cookies.mod.data.profile.sub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import codes.cookies.mod.utils.json.CodecJsonSerializable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracked for the accessory bag.
 */
public class AccessoryItemData implements CodecJsonSerializable<List<AccessoryItemData.AccessoryData>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessoryItemData.class);
	private final List<AccessoryData> items = new ArrayList<>();

	@Override
	public Codec<List<AccessoryData>> getCodec() {
		return AccessoryData.LIST_CODEC;
	}

	@Override
	public void load(List<AccessoryData> value) {
		this.items.clear();
		this.items.addAll(value);
	}

	@Override
	public List<AccessoryData> getValue() {
		return this.items;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Clears all data that is saved on the provided page.
	 * @param page The page number to clear.
	 */
	public void clearPage(int page) {
		this.items.removeIf(item -> item.page == page);
	}

	/**
	 * Save an item with the provided data.
	 * @param itemStack The item to save.
	 * @param slot The slot the item is in.
	 * @param page The page the item is on.
	 */
	public void save(ItemStack itemStack, int slot, int page) {
		this.items.add(AccessoryData.create(itemStack, slot, page));
	}

	/**
	 * Removes the item on the provided page in the provided slot.
	 */
	public void remove(int page, int slot) {
		this.items.removeIf(items -> items.page == page && items.slot == slot);
	}

	/**
	 * Data for the accessory bag.
	 */
	public record AccessoryData(ItemStack itemStack, int slot, int page) {
		public static AccessoryData create(ItemStack itemStack, int slot, int page) {
			itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE);
			itemStack.remove(DataComponentTypes.ENCHANTMENTS);
			return new AccessoryData(itemStack, slot, page);
		}

		public static Codec<AccessoryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStack.CODEC.fieldOf("item").forGetter(AccessoryData::itemStack),
				Codec.INT.fieldOf("slot").forGetter(AccessoryData::slot),
				Codec.INT.fieldOf("page").forGetter(AccessoryData::page)).apply(instance, AccessoryData::create));
		public static Codec<List<AccessoryData>> LIST_CODEC = CODEC.listOf();
	}

}
