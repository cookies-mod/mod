package dev.morazzer.cookies.mod.data.profile.sub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Profile data to track the contents of a players sacks.
 */
@Getter
public class SackTracker implements JsonSerializable {

    private final Map<RepositoryItem, Integer> items = new ConcurrentHashMap<>();

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) {
            logger.error("Failed to read sack tracker because element is not an object.\nResetting tracker.");
            return;
        }

        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (String key : jsonObject.keySet()) {
            final RepositoryItem repositoryItem = RepositoryItem.of(key);
            if (repositoryItem == null) {
                continue;
            }
            this.items.put(repositoryItem, jsonObject.get(key).getAsInt());
        }
    }

    @Override
    public @NotNull JsonElement write() {
        JsonObject jsonObject = new JsonObject();

        this.items.forEach((repositoryItem, integer) -> {
            jsonObject.addProperty(repositoryItem.getInternalId(), integer);
        });

        return jsonObject;
    }

    /**
     * Adds the specified value to the specified item.
     *
     * @param item  The item.
     * @param value The value.
     */
    public void modify(RepositoryItem item, int value) {
        items.compute(item, (key, oldValue) -> oldValue == null ? value : oldValue + value);
    }
}
