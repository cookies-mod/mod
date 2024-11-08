package codes.cookies.mod.data.profile.sub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import codes.cookies.mod.utils.json.CodecJsonSerializable;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Misc item data store, it currently is used for the vault, sack-of-sacks and the potion bag.
 */
public class MiscItemData implements CodecJsonSerializable<List<MiscItemData.MiscItem>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MiscItemData.class);
	private final List<MiscItem> items = new ArrayList<>();

	@Override
	public Codec<List<MiscItem>> getCodec() {
		return MiscItem.LIST_CODEC;
	}

	@Override
	public void load(List<MiscItem> value) {
		this.items.clear();
		this.items.addAll(value);
	}

	@Override
	public List<MiscItem> getValue() {
		return this.items;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public void removeAll(Type type) {
		this.items.removeIf(item -> item.type == type);
	}

	public void save(Type type, ItemStack stack, int slot) {
		this.items.add(MiscItem.create(type, stack, slot));
	}

	public void remove(Type type, int slot) {
		this.items.removeIf(item -> item.type == type && item.slot == slot);
	}

	public record MiscItem(Type type, ItemStack itemStack, int slot) {
		public static MiscItem create(Type type, ItemStack itemStack, int slot) {
			itemStack.remove(DataComponentTypes.JUKEBOX_PLAYABLE);
			itemStack.remove(DataComponentTypes.ENCHANTMENTS);
			return new MiscItem(type, itemStack, slot);
		}

		private static final Codec<MiscItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Type.CODEC.fieldOf("type").forGetter(MiscItem::type),
				ItemStack.CODEC.fieldOf("item").forGetter(MiscItem::itemStack),
				Codec.INT.fieldOf("slot").forGetter(MiscItem::slot)).apply(instance, MiscItem::create));
		private static final Codec<List<MiscItem>> LIST_CODEC = CODEC.listOf();
	}

	@RequiredArgsConstructor
	@Getter
	public enum Type implements StringIdentifiable {
		VAULT("Personal Vault"),
		SACK_OF_SACKS("Sack of Sacks"),
		POTION_BAG("Potion Bag");
		public static final Codec<Type> CODEC = StringIdentifiable.createCodec(Type::values);
		private final String name;

		@Override
		public String asString() {
			return this.name();
		}
	}
}
