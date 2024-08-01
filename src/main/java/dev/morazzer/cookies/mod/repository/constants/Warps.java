package dev.morazzer.cookies.mod.repository.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * All available warp points.
 */
@Getter
public class Warps {
    // Key: command name (shortcut), Value: warp name
    private final Map<String, String> warps = new HashMap<>();

    public Warps(JsonArray jsonArray) {
        if (jsonArray == null) {
            return;
        }

        jsonArray.forEach(entry -> {
            if (entry instanceof JsonObject jsonObject) {
                String warpName = jsonObject.get("warp_name").getAsString();
                warps.put(warpName, warpName);
                jsonObject.getAsJsonArray("aliases").forEach(alias -> warps.put(alias.getAsString(), warpName));
            }
        });
    }
}
