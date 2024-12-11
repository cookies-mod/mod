package codes.cookies.mod.data.profile.sub;

import codes.cookies.mod.events.SackContentsChangeCallback;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.json.JsonSerializable;
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

        this.items.forEach((repositoryItem, integer) -> jsonObject.addProperty(repositoryItem.getInternalId(), integer));

        return jsonObject;
    }

    /**
     * Sets the amount of the item to the specified value.
     *
     * @param item  The item to set.
     * @param value The amount of the item.
     */
    public void set(RepositoryItem item, int value) {
		final Integer previous = items.get(item);
		if (previous != null) {
			SackContentsChangeCallback.DELTA_CALLBACK.invoker().accept(item, value - previous);
		}
		items.put(item, value);
		SackContentsChangeCallback.CALLBACK.invoker().accept(item, value);
    }

    /**
     * Adds the specified value to the specified item.
     *
     * @param item  The item.
     * @param value The value.
     */
    public void modify(RepositoryItem item, int value) {
		SackContentsChangeCallback.DELTA_CALLBACK.invoker().accept(item, value);
        items.compute(item, (key, oldValue) -> Math.max(oldValue == null ? value : oldValue + value, 0));
		SackContentsChangeCallback.CALLBACK.invoker().accept(item, items.get(item));
    }

    public void clear() {
        this.items.clear();
    }
}
