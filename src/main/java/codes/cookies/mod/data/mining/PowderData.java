package codes.cookies.mod.data.mining;

import java.util.HashMap;
import java.util.Map;

import codes.cookies.mod.events.mining.PowderUpdateEvent;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import com.mojang.serialization.Codec;
import org.slf4j.Logger;

public class PowderData implements CodecJsonSerializable<Map<PowderType, Integer>> {

	public static final Codec<Map<PowderType, Integer>> CODEC = Codec.unboundedMap(PowderType.CODEC, Codec.INT);
	private final Map<PowderType, Integer> trackedPowder = new HashMap<>();

	public void update(PowderType powderType, int amount) {
		if (trackedPowder.containsKey(powderType)) {
			final int old = this.remove(powderType);
			int delta = amount - old;
			trackedPowder.put(powderType, amount);
			this.broadcastUpdate(powderType, amount, delta);
			return;
		}

		this.trackedPowder.put(powderType, amount);
		this.broadcastUpdate(powderType, amount, 0);
	}

	private void broadcastUpdate(PowderType powderType, int amount, int delta) {
		if (delta == 0) {
			return;
		}
		PowderUpdateEvent.EVENT.invoker().update(powderType, amount, delta);
	}

	private int remove(PowderType powderType) {
		final Integer remove = trackedPowder.remove(powderType);
		if (remove == null) {
			return 0;
		}
		return remove;
	}

	@Override
	public Codec<Map<PowderType, Integer>> getCodec() {
		return CODEC;
	}

	@Override
	public void load(Map<PowderType, Integer> value) {
		trackedPowder.putAll(value);
	}

	@Override
	public Map<PowderType, Integer> getValue() {
		return trackedPowder;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	public void reset() {
		this.trackedPowder.clear();
	}
}
