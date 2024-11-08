package codes.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.config.system.editor.ConfigOptionEditor;
import codes.cookies.mod.config.system.editor.StringInputEditor;
import org.jetbrains.annotations.NotNull;

/**
 * Input option for strings.
 */
public class StringInputOption extends Option<String, StringInputOption> {
	public StringInputOption(@NotNull String translationKey, String value) {
		super(translationKey, value);
	}

	@Override
	public @NotNull ConfigOptionEditor<String, StringInputOption> getEditor() {
		return new StringInputEditor(this);
	}

	@Override
	public void read(@NotNull JsonElement jsonElement) {
		this.setValue(jsonElement.getAsString());
	}

	@Override
	public @NotNull JsonElement write() {
		return new JsonPrimitive(this.getValue());
	}
}
