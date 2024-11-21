package codes.cookies.mod.config.data;

import java.util.Map;

import codes.cookies.mod.render.hud.HudManager;
import codes.cookies.mod.render.hud.internal.HudElementSettings;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import com.mojang.serialization.Codec;
import org.slf4j.Logger;

import net.minecraft.util.Identifier;

public class HudData implements CodecJsonSerializable<Map<Identifier, HudElementSettings>> {

	private static final Codec<Map<Identifier, HudElementSettings>> CODEC = Codec.unboundedMap(Identifier.CODEC, HudElementSettings.CODEC);

	@Override
	public Codec<Map<Identifier, HudElementSettings>> getCodec() {
		return CODEC;
	}

	@Override
	public void load(Map<Identifier, HudElementSettings> value) {
		HudManager.applyAll(value);
	}

	@Override
	public Map<Identifier, HudElementSettings> getValue() {
		return HudManager.getSettings();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
