package codes.cookies.mod.config.data;

import codes.cookies.mod.utils.json.CodecJsonSerializable;
import com.mojang.serialization.Codec;
import org.slf4j.Logger;

public class CodecData<T> implements CodecJsonSerializable<T> {
	public CodecData(T defaultValue, Codec<T> codec) {
		this.value = defaultValue;
		this.codec = codec;
	}

	private T value;
	private final Codec<T> codec;

	@Override
	public Codec<T> getCodec() {
		return this.codec;
	}

	@Override
	public void load(T value) {
		this.value = value;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
