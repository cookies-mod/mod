package codes.cookies.mod.config.data;

import java.util.Map;

import codes.cookies.mod.render.hud.HudManager;
import codes.cookies.mod.render.hud.internal.HudPosition;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import com.mojang.serialization.Codec;
import org.slf4j.Logger;

import net.minecraft.util.Identifier;

/**
 * Parent element that contains all hud data.
 */
public class HudData implements CodecJsonSerializable<Map<Identifier, HudPosition>> {

	private static final Codec<Map<Identifier, HudPosition>> CODEC = Codec.unboundedMap(Identifier.CODEC, HudPosition.CODEC);

	@Override
	public Codec<Map<Identifier, HudPosition>> getCodec() {
		return CODEC;
	}

	@Override
	public void load(Map<Identifier, HudPosition> value) {
		HudManager.applyAll(value);
	}

	@Override
	public Map<Identifier, HudPosition> getValue() {
		return HudManager.getSettings();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
