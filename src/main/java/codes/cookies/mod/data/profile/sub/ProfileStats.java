package codes.cookies.mod.data.profile.sub;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.serialization.Codec;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileStats implements CodecJsonSerializable<Map<String, Double>> {
	private static final String LOG_KEY = "profile-stats";
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileStats.class);

	private final Map<String, Double> stats = new ConcurrentHashMap<>();

	Codec<Map<String, Double>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.DOUBLE);


	public void saveStat(String key, double value) {
		DevUtils.log(LOG_KEY, "Saved " + key + ": " + value);
		stats.put(key, value);
	}

	public OptionalDouble getStat(String key) {
		if (stats.containsKey(key)) {
			return Optional.ofNullable(stats.get(key)).map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
		}
		return OptionalDouble.empty();
	}

	@Override
	public Codec<Map<String, Double>> getCodec() {
		return CODEC;
	}

	@Override
	public void load(Map<String, Double> value) {
		stats.putAll(value);
	}

	@Override
	public Map<String, Double> getValue() {
		return stats;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}
}
