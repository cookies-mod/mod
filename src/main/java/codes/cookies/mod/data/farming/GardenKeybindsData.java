package codes.cookies.mod.data.farming;

import codes.cookies.mod.data.cookiesdata.CookiesModData;
import codes.cookies.mod.repository.RepositoryItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import net.minecraft.client.option.KeyBinding;

import net.minecraft.client.util.InputUtil;

import org.jetbrains.annotations.NotNull;

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

			this.allKeyBinds = new Gson().fromJson(jsonObject.get("keybinds").getAsString(), Map.class);
		}

		public Map<String, KeyBinding> allKeyBinds = new HashMap<>();

		@Override
		public JsonElement write() {
			JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("keybinds", new Gson().toJson(this.allKeyBinds));

			return jsonObject;
		}

	}
