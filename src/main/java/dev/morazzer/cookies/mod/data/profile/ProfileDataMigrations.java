package dev.morazzer.cookies.mod.data.profile;

import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.data.Migration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Migration handler for the profile data.
 */
public class ProfileDataMigrations {

    private static final String KEY = "migration";
    private static final List<Migration<JsonObject>> MIGRATIONS = new LinkedList<>();
    private static final long LATEST;

    static {

        LATEST = MIGRATIONS
            .stream()
            .min(Comparator.comparingLong(Migration::getNumber))
            .map(Migration::getNumber)
            .orElse(-1L);
    }

    /**
     * Applies all missing migrations to the {@linkplain JsonObject}.
     *
     * @param jsonObject The config object.
     */
    public static void migrate(final JsonObject jsonObject) {
        if (!jsonObject.has(KEY)) {
            jsonObject.addProperty(KEY, 0);
        }

        final long lastApplied = jsonObject.get(KEY).getAsLong();
        for (final Migration<JsonObject> migration : MIGRATIONS
            .stream()
            .sorted(Comparator.comparingLong(Migration::getNumber))
            .toList()) {
            if (migration.getNumber() > lastApplied) {
                migration.apply(jsonObject);
            }
        }
    }

    /**
     * Writes the latest migration number to the {@linkplain JsonObject}.
     *
     * @param jsonObject The config object.
     */
    public static void writeLatest(final JsonObject jsonObject) {
        jsonObject.addProperty(KEY, LATEST);
    }

}
