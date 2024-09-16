package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ColorEditor;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import java.awt.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Option to select a color in the config.
 */
@Slf4j
@Getter
public class ColorOption extends Option<Color, ColorOption> {

    private boolean allowAlpha;

    public ColorOption(String key, Color value) {
        super(key, value);
    }

    /**
     * Allows the color to have an alpha value. By default, this will be off.
     *
     * @return The option.
     */
    public ColorOption withAlpha() {
        this.allowAlpha = true;
        return this;
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (this.expectPrimitiveNumber(jsonElement, log)) {
            return;
        }
        int argb = jsonElement.getAsInt();
        this.value = new Color(argb, this.allowAlpha);
    }

    @Override
    public @NotNull JsonElement write() {
        return new JsonPrimitive(this.value.getRGB());
    }

    @Override
    public @NotNull ConfigOptionEditor<Color, ColorOption> getEditor() {
        return new ColorEditor(this);
    }

	/**
	 * Gets the color value of the option, this is a null safe alternative to {@link #getValue()}.
	 * @return The color value.
	 */
	public int getColorValue() {
		final Color value = this.getValue();
		if (value == null) {
			return 0;
		}
		return value.getRGB();
	}
}
