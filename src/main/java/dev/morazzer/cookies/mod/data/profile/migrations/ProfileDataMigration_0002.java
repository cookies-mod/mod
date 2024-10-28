package dev.morazzer.cookies.mod.data.profile.migrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.data.Migration;

public class ProfileDataMigration_0002 implements Migration<JsonObject> {
	@Override
	public int getNumber() {
		return 2;
	}

	@Override
	public void apply(JsonObject value) {
		final JsonElement selectedCraftHelperItem = value.remove("selected_craft_helper_item");
		if (selectedCraftHelperItem != null) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("id", selectedCraftHelperItem);
			jsonObject.addProperty("amount",2);
			jsonObject.add("collapsed",new JsonArray());
			value.add("craft_helper_data", jsonObject);
		}
	}

	@Override
	public Type getType() {
		return Type.PROFILE;
	}
}
