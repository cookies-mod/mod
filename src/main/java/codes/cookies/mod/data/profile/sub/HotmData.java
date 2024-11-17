package codes.cookies.mod.data.profile.sub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import codes.cookies.mod.utils.json.CodecJsonSerializable;

import it.unimi.dsi.fastutil.ints.IntBooleanImmutablePair;
import it.unimi.dsi.fastutil.ints.IntBooleanPair;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data saved for the heart of the mountain.
 */
public class HotmData implements CodecJsonSerializable<List<HotmData.Entry>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(HotmData.class);

	private final CopyOnWriteArrayList<Entry> list = new CopyOnWriteArrayList<>();

	public void remove(String id) {
		this.list.removeIf(entry -> entry.id.equalsIgnoreCase(id));
	}

	public void save(String id, int level, boolean enabled) {
		this.remove(id);
		this.list.add(new Entry(id, level, enabled));
	}

	@Nullable
	public IntBooleanPair getStatus(String id) {
		return this.list.stream()
				.filter(entry -> entry.id.equalsIgnoreCase(id))
				.findFirst()
				.map(this::toPair)
				.orElse(null);
	}

	private IntBooleanPair toPair(Entry entry) {
		return new IntBooleanImmutablePair(entry.level, entry.enabled);
	}

	@Override
	public Codec<List<Entry>> getCodec() {
		return Entry.LIST_CODEC;
	}

	@Override
	public void load(List<Entry> value) {
		this.list.addAll(value);
	}

	@Override
	public List<Entry> getValue() {
		return this.list;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public record Entry(String id, int level, boolean enabled) {
		public static Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(Entry::id),
				Codec.INT.fieldOf("level").forGetter(Entry::level),
				Codec.BOOL.fieldOf("enabled").forGetter(Entry::enabled)).apply(instance, Entry::new));
		public static Codec<List<Entry>> LIST_CODEC = CODEC.listOf();
	}

	/**
	 * Gets the current quick forge multiplier.
	 */
	public double getQuickForgeMultiplier() {
		final IntBooleanPair quickForge = this.getStatus("quick_forge");
		if (quickForge == null || !quickForge.secondBoolean()) {
			return 1f;
		}

		int level = quickForge.firstInt();
		double reduction = Math.min(30, 10 + (level * 0.5f) + (Math.floor(level/20f) * 10));
		double reductionPercentage = reduction / 100d;
		return 1 - reductionPercentage;
	}
}
