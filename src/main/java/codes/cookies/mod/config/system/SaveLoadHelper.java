package codes.cookies.mod.config.system;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import codes.cookies.mod.utils.json.JsonSerializable;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * A helper for the {@link Category} and {@link Foldable} to prevent duplicated code.
 */
public interface SaveLoadHelper {

    /**
     * Loads the values of the json object into the fields.
     *
     * @param jsonObject The json object.
     */
    default void load_(JsonElement jsonObject) {
        for (Field declaredField : this.getClass().getDeclaredFields()) {
			if (!jsonObject.isJsonObject()) {
				continue;
			}
            if (Optional.ofNullable(declaredField.getType().getSuperclass()).map(Option.class::equals).orElse(false)) {
                Option<?, ?> o = (Option<?, ?>) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                if (!o.canBeSerialized()) {
                    continue;
                }
                if (!jsonObject.getAsJsonObject().has(declaredField.getName())) {
                    continue;
                }
                o.read(jsonObject.getAsJsonObject().get(declaredField.getName()));
            } else if (Optional
                .ofNullable(declaredField.getType().getSuperclass())
                .map(Foldable.class::equals)
                .orElse(false)) {
                Foldable foldable = (Foldable) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                if (!jsonObject.getAsJsonObject().has(declaredField.getName())) {
                    continue;
                }
                foldable.load(jsonObject.getAsJsonObject().get(declaredField.getName()));
            } else if (JsonSerializable.class.isAssignableFrom(declaredField.getType())) {
                JsonSerializable jsonSerializable =
                    (JsonSerializable) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                if (!jsonObject.getAsJsonObject().has(declaredField.getName())) {
                    continue;
                }
                jsonSerializable.read(jsonObject.getAsJsonObject().get(declaredField.getName()));
            }
        }
    }

    /**
     * Saves the fields of the class as json object.
     *
     * @return The json object.
     */
    default JsonElement save_() {
        JsonObject jsonObject = new JsonObject();
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            if (Optional.ofNullable(declaredField.getType().getSuperclass()).map(Option.class::equals).orElse(false)) {
                Option<?, ?> o = (Option<?, ?>) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                if (!o.canBeSerialized()) {
                    continue;
                }

                jsonObject.add(declaredField.getName(), o.write());
            } else if (Optional
                .ofNullable(declaredField.getType().getSuperclass())
                .map(Foldable.class::equals)
                .orElse(false)) {
                Foldable foldable = (Foldable) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                jsonObject.add(declaredField.getName(), foldable.save());
            } else if (JsonSerializable.class.isAssignableFrom(declaredField.getType())) {
                JsonSerializable jsonSerializable =
                    (JsonSerializable) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                jsonObject.add(declaredField.getName(), jsonSerializable.write());
            }
        }
        return jsonObject;
    }

}
