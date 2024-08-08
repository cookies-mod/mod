package dev.morazzer.cookies.mod.data.profile.migrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.data.Migration;

/**
 * Converts an old version of the storage data to a flat version.
 */
public class ProfileDataMigration_0001 implements Migration<JsonObject> {
    @Override
    public int getNumber() {
        return 1;
    }

    @Override
    public void apply(JsonObject value) {
        final JsonElement storageData = value.remove("storage_data");
        if (storageData == null) {
            return;
        }
        JsonArray newData = new JsonArray();
        processOne(storageData.getAsJsonObject().getAsJsonObject("ender_chest"), newData, true);
        processOne(storageData.getAsJsonObject().getAsJsonObject("backpack"), newData, false);
        value.add("storage_data", newData);
    }

    private void processOne(JsonObject object, JsonArray newData, boolean isEnderchest) {
        if (object == null) {
            return;
        }
        String location = isEnderchest ? "ENDER_CHEST" : "BACKPACK";
        for (String s : object.keySet()) {
            int page = Integer.parseInt(s);
            final JsonArray asJsonArray = object.getAsJsonArray(s);
            for (JsonElement jsonElement : asJsonArray) {
                if (!jsonElement.isJsonObject()) {
                    return;
                }
                JsonObject entry = jsonElement.getAsJsonObject();
                int slot = entry.has("slot") ? entry.get("slot").getAsInt() : -1;
                JsonObject itemStack = entry.has("item_stack") ? entry.get("item_stack").getAsJsonObject() : null;
                if (slot == -1 || itemStack == null) {
                    continue;
                }
                JsonObject newEntry = new JsonObject();
                newEntry.addProperty("location", location);
                newEntry.addProperty("page", page);
                newEntry.addProperty("slot", slot);
                newEntry.add("item", itemStack);
                newData.add(newEntry);
            }
        }
    }

    @Override
    public Type getType() {
        return Type.PROFILE;
    }
}
