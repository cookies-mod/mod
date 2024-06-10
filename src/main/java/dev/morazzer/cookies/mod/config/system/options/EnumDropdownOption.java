package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.editor.EnumDropdownEditor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * A dropdown menu in the config which gets its values from an enum instance.
 *
 * @param <T> The enum to get the values from.
 */
@Slf4j
@Getter
public class EnumDropdownOption<T extends Enum<T>> extends Option<T, EnumDropdownOption<T>> {

    private TextSupplier<T> textSupplier;

    /**
     * Creats an enum dropdown option.
     *
     * @param name        The name of the option.
     * @param description The description of the option.
     * @param value       The initial value of the option.
     */
    public EnumDropdownOption(Text name, Text description, T value) {
        super(name, description, value);
        this.textSupplier =
            enumValue -> Text.literal(StringUtils.capitalize(enumValue.name().replace('_', ' ').toLowerCase()));
    }

    /**
     * Sets a supplier for the text.
     *
     * @param textSupplier The supplier.
     * @return The option.
     */
    public EnumDropdownOption<T> withSupplier(TextSupplier<T> textSupplier) {
        this.textSupplier = textSupplier;
        return this;
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (expectPrimitive(jsonElement, log)) {
            return;
        }
        //Can't fail under normal circumstances
        //noinspection unchecked
        this.value = (T) Enum.valueOf(this.value.getClass(), jsonElement.getAsString());
    }

    @Override
    public @NotNull JsonElement write() {
        return new JsonPrimitive(this.value.toString());
    }

    @Override
    public @NotNull ConfigOptionEditor<T, EnumDropdownOption<T>> getEditor() {
        return new EnumDropdownEditor<>(this);
    }

    /**
     * Functional interface to map the keys to text.
     *
     * @param <T> The type of the enum.
     */
    @FunctionalInterface
    public interface TextSupplier<T extends Enum<T>> {

        /**
         * Maps the value of type T to a minecraft text.
         *
         * @param value The value.
         * @return The text.
         */
        Text supplyText(T value);

    }

}
