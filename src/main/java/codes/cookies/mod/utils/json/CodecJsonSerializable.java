package codes.cookies.mod.utils.json;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Handles saving and loading for codec based data types.
 *
 * @param <T> The type.
 */
public interface CodecJsonSerializable<T> extends JsonSerializable {

	@Override
	default JsonElement write() {
		final T value = this.getValue();
		if (value == null) {
			return null;
		}
		final DataResult<JsonElement> result = this.getCodec().encodeStart(JsonOps.INSTANCE, value);
		if (result.isError()) {
			this.getLogger().warn("Failed to save data! {}", result.error().get().message());
			return null;
		}
		return result.getOrThrow();
	}

	@Override
	default void read(@NotNull JsonElement jsonElement) {
		try {
			final DataResult<T> parse = this.getCodec().parse(JsonOps.INSTANCE, jsonElement);
			if (parse.isSuccess()) {
				this.load(parse.getOrThrow());
			} else {
				this.getLogger()
						.warn("Failed to load data from a CodecJsonSerializable, trying to load partial. {}",
								parse.error().get().message());
				try {
					this.load(parse.getOrThrow());
				} catch (Exception e) {
					this.getLogger().error("Failed to load partial data, continuing with empty list.");
				}
			}
		} catch (Exception e) {
			this.getLogger().error("An error occurred while trying to the data.", e);
		}
	}

	/**
	 * @return The codec to use for serialization/deserialization.
	 */
	Codec<T> getCodec();

	/**
	 * Called to load the data provided by the codec.
	 */
	void load(T value);

	/**
	 * @return The data to save.
	 */
	T getValue();

	/**
	 * @return The logger to use for warnings.
	 */
	Logger getLogger();
}
