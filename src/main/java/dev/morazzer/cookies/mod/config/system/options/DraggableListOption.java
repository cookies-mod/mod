package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.editor.DraggableListEditor;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Draggable list in the config to allow rearranging of various items.
 */
@Slf4j
@Getter
public class DraggableListOption extends Option<List<String>, DraggableListOption> {

    private ValueSupplier valueSupplier;

    @SuppressWarnings("MissingJavadoc")
    public DraggableListOption(Text name, Text description, List<String> value) {
        super(name, description, value);
    }

    /**
     * Adds a value supplier to the option to correctly map the strings to their respective display variant.
     *
     * @param valueSupplier The supplier for the {@linkplain Text}.
     * @return The option.
     */
    public DraggableListOption withValueSupplier(ValueSupplier valueSupplier) {
        this.valueSupplier = valueSupplier;
        return this;
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (!jsonElement.isJsonArray()) {
            log.warn("Error while loading config value, expected array got %s".formatted(
                jsonElement.isJsonObject() ? "json-object" : "json-primitive"));
            return;
        }
        this.value = new ArrayList<>();
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            if (!element.isJsonPrimitive()) {
                log.warn("Skip bad value, expected string got %s".formatted(
                    jsonElement.isJsonObject() ? "json-object" : "json-array"));
                continue;
            }
            if (!element.getAsJsonPrimitive().isString()) {
                log.warn("Skip bad value, expected string got %s".formatted(jsonElement.getAsString()));
                continue;
            }
            this.value.add(element.getAsString());
        }
    }

    @Override
    public @NotNull JsonElement write() {
        JsonArray jsonArray = new JsonArray();
        this.value.forEach(jsonArray::add);
        return jsonArray;
    }

    @Override
    public @NotNull ConfigOptionEditor<List<String>, DraggableListOption> getEditor() {
        return new DraggableListEditor(this);
    }

    /**
     * Functional interface to map the keys to text.
     */
    @FunctionalInterface
    public interface ValueSupplier {

        /**
         * Maps the key to a minecraft text value.
         *
         * @param key The key.
         * @return The text value.
         */
        Text getText(String key);

    }

}
