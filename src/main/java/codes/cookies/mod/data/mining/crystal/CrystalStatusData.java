package codes.cookies.mod.data.mining.crystal;

import codes.cookies.mod.utils.json.CodecJsonSerializable;
import com.mojang.serialization.Codec;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Data about the different crystal types and their respective status.
 */
public class CrystalStatusData implements CodecJsonSerializable<Map<CrystalType, CrystalStatus>> {
	private static final Codec<Map<CrystalType, CrystalStatus>> CODEC = Codec.unboundedMap(CrystalType.CODEC, CrystalStatus.CODEC);

	private final Map<CrystalType, CrystalStatus> map = new HashMap<>();

	public CrystalStatus getStatus(CrystalType type) {
		return Objects.requireNonNullElse(map.get(type), CrystalStatus.NOT_FOUND);
	}

	public void setStatus(CrystalType type, CrystalStatus status) {
		map.put(type, Objects.requireNonNullElse(status, CrystalStatus.NOT_FOUND));
	}

	@Override
	public Codec<Map<CrystalType, CrystalStatus>> getCodec() {
		return CODEC;
	}

	@Override
	public void load(Map<CrystalType, CrystalStatus> value) {
		map.putAll(value);
	}

	@Override
	public Map<CrystalType, CrystalStatus> getValue() {
		return map;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
