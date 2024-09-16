package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.editor.StringDropdownEditor;
import java.util.Set;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * A dropdown menu in the config which gets its values from an enum instance.
 */
@Getter
@Slf4j
public class StringDropdownOption extends Option<String, StringDropdownOption> {

    private final Set<String> possibleValues;

    public StringDropdownOption(String key, String value, String... possibleValues) {
        super(key, value);
        this.possibleValues = Set.of(possibleValues);
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (!jsonElement.isJsonPrimitive()) {
            log.warn("Error while loading config value, expected any of [%s] got %s".formatted(Strings.join(
					this.possibleValues,
                ", "
            ), jsonElement.isJsonObject() ? "json-object" : "json-array"));
            return;
        }
        if (!jsonElement.getAsJsonPrimitive().isString()) {
            log.warn("Error while loading config value, expected any of [%s] got %s".formatted(Strings.join(
                this.possibleValues,
                ", "
            ), jsonElement.getAsString()));
            return;
        }
        if (!this.possibleValues.contains(jsonElement.getAsString())) {
            log.warn("Error while loading config value, expected any of [%s] found %s".formatted(Strings.join(
					this.possibleValues,
                ", "
            ), jsonElement.getAsString()));
            return;
        }
        this.value = jsonElement.getAsString();
    }

    @Override
    public @NotNull JsonElement write() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public @NotNull ConfigOptionEditor<String, StringDropdownOption> getEditor() {
        return new StringDropdownEditor(this);
    }

}
