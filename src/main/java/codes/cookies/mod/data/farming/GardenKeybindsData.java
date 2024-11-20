package codes.cookies.mod.data.farming;

import codes.cookies.mod.data.moddata.CookiesModData;
import codes.cookies.mod.data.moddata.ModData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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
			

		}

		@Override
		public JsonElement write() {
			return null;
		}
	}
