package dev.morazzer.cookies.mod.data.profile.sub;

import com.mojang.serialization.Codec;
import dev.morazzer.cookies.mod.utils.json.CodecJsonSerializable;

import net.minecraft.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EquipmentData implements CodecJsonSerializable<List<ItemStack>> {
	private final Logger LOGGER = LoggerFactory.getLogger(EquipmentData.class);

	private final List<ItemStack> equipment = new ArrayList<>();


	public void reset() {
		equipment.clear();
	}

	public void add(ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		equipment.add(stack);
	}

	@Override
	public Codec<List<ItemStack>> getCodec() {
		return ItemStack.CODEC.listOf();
	}

	@Override
	public void load(List<ItemStack> value) {
		equipment.addAll(value);
	}

	@Override
	public List<ItemStack> getValue() {
		return equipment;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}
}
