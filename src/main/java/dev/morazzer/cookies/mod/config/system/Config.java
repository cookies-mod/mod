package dev.morazzer.cookies.mod.config.system;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import java.lang.reflect.Field;
import java.util.Optional;
import net.minecraft.text.Text;

/**
 * A config that can be displayed in-game.
 *
 * @param <T> The type of the config.
 */
public abstract class Config<T extends Config<T>> {

    /**
     * Saves the config to a {@linkplain JsonObject}.
     *
     * @return The config as json object.
     */
    public JsonObject save() {
        JsonObject jsonObject = new JsonObject();

        for (Field declaredField : this.getClass().getDeclaredFields()) {
            if (Optional
                .ofNullable(declaredField.getType().getSuperclass())
                .map(Category.class::equals)
                .orElse(false)) {
                Category category = (Category) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                jsonObject.add(declaredField.getName(), category.save());
            }
        }

        return jsonObject;
    }

    /**
     * Loads all categories and values from a {@linkplain JsonObject}.
     *
     * @param jsonObject The json object.
     */
    public void load(JsonObject jsonObject) {
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            if (!declaredField.isAnnotationPresent(Expose.class)) {
                continue;
            }
            if (Optional
                .ofNullable(declaredField.getType().getSuperclass())
                .map(Category.class::equals)
                .orElse(false)) {
                Category category = (Category) ExceptionHandler.removeThrows(() -> declaredField.get(this));
                if (!jsonObject.has(declaredField.getName())) {
                    continue;
                }
                category.load(jsonObject.get(declaredField.getName()));
            }
        }
    }

    /**
     * Gets the title of the config that will always be displayed.
     *
     * @return The title.
     */
    public abstract Text getTitle();

}
