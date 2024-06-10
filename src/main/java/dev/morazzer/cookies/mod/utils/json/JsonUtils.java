package dev.morazzer.cookies.mod.utils.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import java.lang.reflect.Field;

/**
 * Various constants and methods related to json/gson.
 */
public class JsonUtils {

    /**
     * Global gson instance.
     */
    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();

    /**
     * Global gson instance without pretty printing or field naming.
     */
    public static final Gson CLEAN_GSON = new Gson();

    /**
     * Writes an object to a {@linkplain JsonObject}.
     *
     * @param instance The source object.
     * @return The json object.
     */
    public static JsonObject toJsonObject(Object instance) {
        JsonObject jsonObject = new JsonObject();
        for (Field field : instance.getClass().getDeclaredFields()) {
            field.trySetAccessible();
            final String fieldName = getEffectiveName(field);

            if (JsonSerializable.class.isAssignableFrom(field.getType())) {
                final JsonSerializable serializer = (JsonSerializable) ExceptionHandler.removeThrows(
                    () -> field.get(instance)
                );
                jsonObject.add(fieldName, serializer.write());
            } else if (field.isAnnotationPresent(Safe.class)) {
                jsonObject.add(fieldName, ExceptionHandler.removeThrows(() -> toJsonObject(field.get(instance))));
            } else {
                jsonObject.add(
                    fieldName,
                    ExceptionHandler.removeThrows(() -> CLEAN_GSON.toJsonTree(field.get(instance)))
                );
            }
        }
        return jsonObject;
    }

    /**
     * Parses an instance of an object from a {@linkplain JsonObject} but keep defaults if not present.
     *
     * @param instance   The instance of the object with defaults.
     * @param jsonObject The json object to parse from.
     * @param <T>        The type of the object.
     * @return The object with modified fields.
     */
    public static <T> T fromJson(T instance, JsonObject jsonObject) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            field.trySetAccessible();
            final String fieldName = getEffectiveName(field);

            if (JsonSerializable.class.isAssignableFrom(field.getType())) {
                if (!jsonObject.has(fieldName)) {
                    continue;
                }
                final JsonSerializable serializer = (JsonSerializable) ExceptionHandler.removeThrows(
                    () -> field.get(instance)
                );
                serializer.read(jsonObject.get(fieldName));
            } else if (field.isAnnotationPresent(Safe.class)) {
                if (!jsonObject.has(fieldName)) {
                    continue;
                }
                fromJson(
                    ExceptionHandler.removeThrows(() -> field.get(instance)),
                    jsonObject.get(fieldName).getAsJsonObject()
                );
            } else {
                if (!jsonObject.has(fieldName)) {
                    continue;
                }
                try {
                    field.set(instance, CLEAN_GSON.fromJson(jsonObject.get(fieldName), field.getType()));
                } catch (IllegalAccessException e) {
                    ExceptionHandler.handleException(
                        new RuntimeException(
                            field
                                .getDeclaringClass()
                                .getName() + "#" + field.getName(),
                            e
                        )
                    );
                }
            }
        }
        return instance;
    }

    private static String getEffectiveName(Field field) {
        if (field.isAnnotationPresent(SerializedName.class)) {
            return field.getAnnotation(SerializedName.class).value();
        }

        return field.getName().replaceAll("([a-z1-9])([A-Z])", "$1_$2").toLowerCase();
    }

}
