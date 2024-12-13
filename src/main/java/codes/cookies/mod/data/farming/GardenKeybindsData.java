package codes.cookies.mod.data.farming;

import codes.cookies.mod.data.cookiesmoddata.ClientInitializationCallback;
import codes.cookies.mod.data.cookiesmoddata.CookiesModData;
import codes.cookies.mod.utils.accessors.KeyBindingAccessor;
import codes.cookies.mod.utils.json.CodecJsonSerializable;


import com.mojang.logging.LogUtils;

import com.mojang.serialization.Codec;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GardenKeybindsData
		implements CookiesModData, ClientInitializationCallback, CodecJsonSerializable<Map<String, GardenKeybindsData.GardenKeyBindOverride>> {
	private static final Codec<Map<String, GardenKeyBindOverride>> CODEC = Codec.unboundedMap(
			Codec.STRING,
			GardenKeyBindOverride.CODEC);
	private final CompletableFuture<Map<String, GardenKeyBindOverride>> future = new CompletableFuture<>();

	@Override
	public String getFileLocation() {
		return "garden_keybinds.json";
	}


	@Override
	public Codec<Map<String, GardenKeyBindOverride>> getCodec() {
		return CODEC;
	}

	@Override
	public void gameInitialized() {
		future.whenComplete((map, error) -> {
			if (error != null) {
				logger.error("An error occurred while loading the garden keybinds!", error);
				return;
			}

			for (var entry : map.entrySet()) {
				if (entry.getValue() != null) {
					var keyBinding = KeyBindingAccessor.toAccessor(KeyBinding.KEYS_BY_ID.get(entry.getKey()));
					keyBinding.cookies$setGardenKey(entry.getValue());
				}
			}
		});
	}

	@Override
	public void load(Map<String, GardenKeyBindOverride> value) {
		future.complete(value);
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
