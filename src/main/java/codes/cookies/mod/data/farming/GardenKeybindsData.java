package codes.cookies.mod.data.farming;

import codes.cookies.mod.data.cookiesdata.CookiesModData;
import codes.cookies.mod.utils.json.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;


import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		gardenKeyBindOverrides.clear();
		gardenKeyBindOverrides.addAll(JsonUtils.CLEAN_GSON.fromJson(jsonObject, List.class));
	}

	public List<GardenKeyBindOverride> gardenKeyBindOverrides = new ArrayList<>();

	@Getter
	public static final class GardenKeyBindOverride {
		private final KeyBinding keyBinding;

		public GardenKeyBindOverride(KeyBinding keyBinding)
		{
			this.keyBinding = keyBinding;
		}
	}

	@Override
	public JsonElement write() {
		return JsonUtils.CLEAN_GSON.toJsonTree(gardenKeyBindOverrides);
	}

}
