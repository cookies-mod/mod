package codes.cookies.mod.data.farming;

import codes.cookies.mod.data.cookiesdata.CookiesModData;
import codes.cookies.mod.utils.accessors.KeyBindingAccessor;
import codes.cookies.mod.utils.json.JsonUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GardenKeybindsData implements CookiesModData
{
	@Override
	public String getFileLocation() {
		return "garden_keybinds.json";
	}

	@Override
	public void read(@NotNull JsonElement jsonElement) {
		if (!jsonElement.isJsonObject() || jsonElement.getAsJsonObject().isEmpty()) {
			return;
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		Type mapType = new TypeToken<HashMap<String, GardenKeyBindOverride>>(){}.getType();

		Map<String, GardenKeyBindOverride> jsonMap = JsonUtils.CLEAN_GSON.fromJson(jsonObject.getAsString(), mapType);

		for (KeyBinding keyBinding : KeyBinding.KEYS_BY_ID.values()) {
			KeyBindingAccessor.toAccessor(keyBinding).cookies$setGardenKey(jsonMap.getOrDefault(keyBinding.getTranslationKey(), null));
		}
	}

	public record GardenKeyBindOverride(InputUtil.Key key) {
	}

	@Override
	public JsonElement write() {
		Map<String, GardenKeyBindOverride> map = new HashMap<>();
		for (KeyBinding keyBinding : KeyBinding.KEYS_BY_ID.values()) {
			map.put(keyBinding.getTranslationKey(), ((KeyBindingAccessor) keyBinding).cookies$getGardenKey());
		}
		return JsonUtils.CLEAN_GSON.toJsonTree(map);
	}

}
