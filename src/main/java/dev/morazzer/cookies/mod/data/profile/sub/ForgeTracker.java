package dev.morazzer.cookies.mod.data.profile.sub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.json.CodecJsonSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class ForgeTracker implements CodecJsonSerializable<List<ForgeTracker.Data>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ForgeTracker.class);

	private final List<Data> data = new ArrayList<>();

	public void saveItem(RepositoryItem item, int slot, long timeStartedSeconds) {
		this.removeItem(slot);
		this.data.add(new Data(item, timeStartedSeconds + 1, slot));
	}

	public void removeItem(int slot) {
		this.data.removeIf(data -> data.slot == slot);
	}

	@Override
	public Codec<List<Data>> getCodec() {
		return Data.LIST_CODEC;
	}

	@Override
	public void load(List<Data> value) {
		this.data.addAll(value);
	}

	@Override
	public List<Data> getValue() {
		return this.data;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public record Data(RepositoryItem repositoryItem, long startedSeconds, int slot) {
		public static Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				RepositoryItem.CODEC.optionalFieldOf("item")
						.xmap(item -> item.orElse(null), Optional::ofNullable)
						.forGetter(Data::repositoryItem),
				Codec.LONG.fieldOf("startedSeconds").forGetter(Data::startedSeconds),
				Codec.INT.fieldOf("slot").forGetter(Data::slot)).apply(instance, Data::new));
		public static Codec<List<Data>> LIST_CODEC = CODEC.listOf();
	}
}
