package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.BooleanEditor;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for simple toggle buttons in the config.
 */
public class BooleanOption extends Option<Boolean, BooleanOption> {
    private static final Logger logger = LoggerFactory.getLogger(BooleanOption.class);

    @SuppressWarnings("MissingJavadoc")
    public BooleanOption(Text name, Text description, Boolean value) {
        super(name, description, value);
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject jsonObject) {
            if (!jsonObject.has("value")) {
                logger.warn("Error while loading config value, boolean object doesnt have a value");
                return;
            }
            this.value = jsonObject.get("value").getAsBoolean();
            return;
        }
        if (!jsonElement.isJsonPrimitive()) {
            logger.warn("Error while loading config value, expected boolean got %s".formatted(
                jsonElement.isJsonObject() ? "json-object" : "json-array"));
            return;
        }
        if (!jsonElement.getAsJsonPrimitive().isBoolean()) {
            logger.warn(
                "Error while loading config value, expected boolean got %s".formatted(jsonElement.getAsString()));
            return;
        }
        this.value = jsonElement.getAsBoolean();
    }

    @Override
    public @NotNull JsonElement write() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public @NotNull ConfigOptionEditor<Boolean, BooleanOption> getEditor() {
        return new BooleanEditor(this);
    }

}
