package codes.cookies.mod.data.profile.sub;

import com.mojang.serialization.Codec;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlotData implements CodecJsonSerializable<Map<String, Long>> {
	Logger LOGGER = LoggerFactory.getLogger(PlotData.class);
	static Codec<Map<String, Long>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.LONG);

	private static final Map<String, Long> plots = new ConcurrentHashMap<>();

	public boolean isPlotSprayed(int plotId) {
		if (plots.isEmpty()) {
			return false;
		}
		return plots.getOrDefault(String.valueOf(plotId), 0L) > System.currentTimeMillis();
	}

	@Override
	public Codec<Map<String, Long>> getCodec() {
		return CODEC;
	}

	@Override
	public void load(Map<String, Long> value) {
		plots.putAll(value);
	}

	@Override
	public Map<String, Long> getValue() {
		return plots;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public void setSprayed(int lastPlotId) {
		plots.put(String.valueOf(lastPlotId), System.currentTimeMillis() + 1800000 /* 30m */);
	}

	public boolean isAnySprayed() {
		for (Long value : plots.values()) {
			if (value > System.currentTimeMillis()) {
				return true;
			}
		}
		return false;
	}
}
