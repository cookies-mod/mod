package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.editor.TextDisplayEditor;
import dev.morazzer.cookies.mod.translations.TranslationKey;
import lombok.Getter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Easy way to display text in the config.
 */
@Getter
public class TextDisplayOption extends Option<Text, TextDisplayOption> {

    private int color;
    private int secondColor;

    /**
     * Creates a text display.
     *
     * @param translationKey The translation key to use.
     */
    public TextDisplayOption(@NotNull @TranslationKey String translationKey) {
        super(Text.translatable(translationKey), Text.empty(), Text.empty());
        this.setColor(0xFFFFFFFF);
    }

    /**
     * Sets the color for the bar.
     *
     * @param color The color.
     */
    public void setColor(final int color) {
        this.color = color;
        this.secondColor = 0xFFFFFF & color;
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
    public ConfigOptionEditor<Text, TextDisplayOption> getEditor() {
        return new TextDisplayEditor(this);
    }

    @Override
    public boolean canBeSerialized() {
        return false;
    }

}
