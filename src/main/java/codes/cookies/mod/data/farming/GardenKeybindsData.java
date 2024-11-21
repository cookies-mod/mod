package codes.cookies.mod.data.farming;

import codes.cookies.mod.data.cookiesdata.CookiesModData;
import codes.cookies.mod.utils.json.JsonUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;


import net.minecraft.client.util.InputUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Getter
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

		gardenKeyBindOverrides.clear();
		gardenKeyBindOverrides.putAll(JsonUtils.CLEAN_GSON.fromJson(jsonObject.getAsString(), mapType));
	}

	private final Map<String, GardenKeyBindOverride> gardenKeyBindOverrides = new HashMap<>();

	public record GardenKeyBindOverride(InputUtil.Key key) {
	}

	@Override
	public JsonElement write() {
		return JsonUtils.CLEAN_GSON.toJsonTree(this.gardenKeyBindOverrides);
	}

}
