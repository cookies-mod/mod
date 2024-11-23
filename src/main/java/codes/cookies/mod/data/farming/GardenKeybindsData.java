package codes.cookies.mod.data.farming;

import codes.cookies.mod.data.cookiesmoddata.CookiesModData;
import codes.cookies.mod.utils.accessors.KeyBindingAccessor;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import codes.cookies.mod.utils.json.JsonUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import com.mojang.logging.LogUtils;

import com.mojang.serialization.Codec;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GardenKeybindsData implements CookiesModData, CodecJsonSerializable<Map<String, GardenKeybindsData.GardenKeyBindOverride>>
{
	private static final Codec<Map<String, GardenKeyBindOverride>> CODEC = Codec.unboundedMap(Codec.STRING, GardenKeyBindOverride.CODEC);

	@Override
	public String getFileLocation() {
		return "garden_keybinds.json";
	}


	@Override
	public Codec<Map<String, GardenKeyBindOverride>> getCodec() {
		return CODEC;
	}

	@Override
	public void load(Map<String, GardenKeyBindOverride> value) {
		for (var entry : value.entrySet()) {
			if (entry.getValue() != null) {
				var keyBinding = KeyBindingAccessor.toAccessor(KeyBinding.KEYS_BY_ID.get(entry.getKey()));
				keyBinding.cookies$setGardenKey(entry.getValue());
			}
		}
	}

	@Override
	public Map<String, GardenKeyBindOverride> getValue() {
		var gardenOverrides = new HashMap<String, GardenKeyBindOverride>();

		for (var keybind : KeyBinding.KEYS_BY_ID.values()) {
			if (keybind instanceof KeyBindingAccessor gardenKeyBind && gardenKeyBind.cookies$getGardenKey() != null) {
				gardenOverrides.put(keybind.getTranslationKey(), gardenKeyBind.cookies$getGardenKey());
			}
		}

		return gardenOverrides;
	}

	@Override
	public Logger getLogger() {
		return LogUtils.getLogger();
	}

	public record GardenKeyBindOverride(InputUtil.Key key) {
		private static final Codec<GardenKeyBindOverride> CODEC = Codec.STRING.xmap(
				s -> new GardenKeyBindOverride(InputUtil.fromTranslationKey(s)),
				gardenKeyBindOverride -> gardenKeyBindOverride.key.getTranslationKey());
	}

}
