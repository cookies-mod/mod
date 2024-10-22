package dev.morazzer.cookies.mod.config.system;

import com.google.gson.JsonElement;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.translations.TranslationKey;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * An option that can be displayed in the config.
 *
 * @param <T> The type of the value.
 * @param <O> The type of the option.
 */
@Getter
@Setter
public abstract class Option<T, O extends Option<T, O>> implements JsonSerializable {

    private final Text name;
    private final List<OrderedText> descriptionOrdered;
    private final Text[] description;
    protected T value;
    protected boolean active = true;
    protected List<ValueChangeCallback<T>> callbacks = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    /**
     * Creates a new option.
     *
     * @param translationKey The translation key to use.
     * @param value          The initial value.
     */
    public Option(@NotNull @TranslationKey String translationKey, T value) {
        this(
            Text.translatable(TranslationKeys.name(translationKey)),
            Text.translatable(TranslationKeys.tooltip(translationKey)),
            value);
    }

    /**
     * Creates a new option.
     *
     * @param name        The name.
     * @param description The description.
     * @param value       The initial value.
     */
    protected Option(@NotNull Text name, Text description, T value) {
        this.name = name;
		if (description != null) {
			this.description = new Text[] { description };
		} else {
			this.description = new Text[0];
		}
        this.descriptionOrdered = Arrays.stream(this.description).map(Text::asOrderedText).toList();
        this.value = value;
    }

    /**
     * Creates a new option.
     *
     * @param name        The name.
     * @param description The description.
     * @param value       The initial value.
     */
    public Option(String name, T value, String[] description) {
        this.name = Text.translatable(name);
        this.description =  Arrays.stream(description).map(Text::translatable).toArray(Text[]::new);
        this.descriptionOrdered = Arrays.stream(this.description).map(Text::asOrderedText).toList();
        this.value = value;
    }

    /**
     * Sets the value.
     *
     * @param value The value.
     */
    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;
        this.updateCallbacks(oldValue);
    }

    /**
     * Runs all callbacks that are currently registered.
     *
     * @param oldValue The old value.
     */
    protected void updateCallbacks(T oldValue) {
        this.callbacks.forEach(callbacks -> callbacks.valueChanged(oldValue, this.value));
    }

    /**
     * Adds a hidden key that can be searched for to the option.
     *
     * @param key The key to add.
     * @return The option.
     */
    @Deprecated(forRemoval = true)
    public final O withHiddenKey(@NotNull String key) {
        return this.withHiddenKeys(key);
    }

    /**
     * Adds multiple hidden keys that can all be searched for to the option.
     *
     * @param keys The keys to add.
     * @return The option.
     */
    @Deprecated(forRemoval = true)
    public final O withHiddenKeys(@NotNull String... keys) {
        return this.withTags(keys);
    }

    /**
     * Adds multiple tags that can all be searched for to the option.
     *
     * @param tags The tags to add.
     * @return The option.
     */
    public final O withTags(@NotNull String... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this.asOption();
    }

    /**
     * Casts the option to the type of the option.
     *
     * @return The option.
     */
    @NotNull
    public O asOption() {
        //noinspection unchecked
        return (O) this;
    }

    /**
     * Adds a tag that cna be searched for to the option.
     *
     * @param tag The tag to add.
     * @return The option.
     */
    public final O withTag(@NotNull String tag) {
        return this.withTags(tag);
    }

    /**
     * Gets the config editor which will be used to render the option in the config.
     *
     * @return The editor.
     */
    @NotNull
    public abstract ConfigOptionEditor<T, O> getEditor();

    /**
     * Whether the option can be serialized or not. Returning false will disable calls to {@link Option#write()} and
     * {@link Option#read(JsonElement)}.
     *
     * @return If the option is serializable.
     */
    public boolean canBeSerialized() {
        return true;
    }

    /**
     * Only shows this option if the other option is true.
     *
     * @param booleanOption The option to depend on.
     * @return The option.
     */
    public O onlyIf(BooleanOption booleanOption) {
        this.active = booleanOption.active;
        booleanOption.withCallback((oldValue, newValue) -> this.active = newValue);
        return this.asOption();
    }

    /**
     * Adds a callback that will be called when the value is changed.
     *
     * @param valueChangeCallback The callback to add.
     * @return The option.
     */
    @NotNull
    public final O withCallback(@NotNull ValueChangeCallback<T> valueChangeCallback) {
        this.callbacks.add(valueChangeCallback);
        return this.asOption();
    }

	/**
	 * Adds a callback that will be called when the value is changed.
	 *
	 * @param runnable The callback to add.
	 * @return The option.
	 */
	@NotNull
	public final O withCallback(@NotNull Runnable runnable) {
		this.callbacks.add((v1,v2) -> runnable.run());
		return this.asOption();
	}

    /**
     * Only shows this option if the other option is false.
     *
     * @param booleanOption The option to depend on.
     * @return The option.
     */
    public O onlyIfNot(BooleanOption booleanOption) {
        this.active = !booleanOption.active;
        booleanOption.withCallback((oldValue, newValue) -> this.active = !newValue);
        return this.asOption();
    }

    protected boolean expectPrimitiveNumber(@NotNull JsonElement jsonElement, Logger log) {
        if (!jsonElement.isJsonPrimitive()) {
            log.warn("Error while loading config value, expected number got %s".formatted(
                jsonElement.isJsonObject() ? "json-object" : "json-array"));
            return true;
        }
        if (!jsonElement.getAsJsonPrimitive().isNumber()) {
            log.warn("Error while loading config value, expected number got %s".formatted(jsonElement.getAsString()));
            return true;
        }
        return false;
    }

    protected boolean expectPrimitiveString(@NotNull JsonElement jsonElement, Logger log) {
        if (!jsonElement.isJsonPrimitive()) {
            log.warn("Error while loading config value, expected string got %s".formatted(
                jsonElement.isJsonObject() ? "json-object" : "json-array"));
            return true;
        }
        if (!jsonElement.getAsJsonPrimitive().isString()) {
            log.warn("Error while loading config value, expected string got %s".formatted(jsonElement.getAsString()));
            return true;
        }
        return false;
    }

    /**
     * Functional interface to listen to value changes.
     *
     * @param <T> The type of the value.
     */
    @FunctionalInterface
    public interface ValueChangeCallback<T> {

        /**
         * Called when the value of an option changed.
         *
         * @param oldValue The old value.
         * @param newValue The new value.
         */
        void valueChanged(T oldValue, T newValue);

    }

}
