package codes.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.config.system.editor.ButtonEditor;
import codes.cookies.mod.config.system.editor.ConfigOptionEditor;
import lombok.Getter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A simple button in the config.
 */
@Getter
public class ButtonOption extends Option<Runnable, ButtonOption> {

    private final Text buttonText;

    public ButtonOption(String key, Runnable value, String buttonText) {
        super(key, value);
        this.buttonText = Text.translatable(buttonText);
    }

    @Override
    @Contract("_->fail")
    public void read(@NotNull JsonElement jsonElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract("->fail")
    public @NotNull JsonElement write() {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public ConfigOptionEditor<Runnable, ButtonOption> getEditor() {
        return new ButtonEditor(this);
    }

    @Override
    public boolean canBeSerialized() {
        return false;
    }

}
