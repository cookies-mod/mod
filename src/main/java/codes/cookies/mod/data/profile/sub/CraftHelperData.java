package codes.cookies.mod.data.profile.sub;

import java.util.Optional;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperInstance;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Profile data for the craft helper to serialize the current item and settings.
 */
public class CraftHelperData implements CodecJsonSerializable<CraftHelperInstance> {

	public CraftHelperData() {
		CraftHelperManager.remove();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CraftHelperData.class);

	@Override
	public Codec<CraftHelperInstance> getCodec() {
		return CraftHelperInstance.CODEC;
	}

	@Override
	public void load(CraftHelperInstance value) {
		CraftHelperManager.setActive(value);
	}

	@Override
	public CraftHelperInstance getValue() {
		return Optional.of(CraftHelperManager.getActive())
				.filter(Predicate.not(CraftHelperInstance.EMPTY::equals))
				.orElse(null);
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}
}
