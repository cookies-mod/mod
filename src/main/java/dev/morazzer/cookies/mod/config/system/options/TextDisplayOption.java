package dev.morazzer.cookies.mod.config.system.options;

import java.util.Optional;

import com.google.gson.JsonElement;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.editor.TextDisplayEditor;
import dev.morazzer.cookies.mod.translations.TranslationKey;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import net.minecraft.text.Text;

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
		this(translationKey, null);
	}

	public TextDisplayOption(@NotNull @TranslationKey String name, String description) {
		super(
				Text.translatable(name),
				Optional.ofNullable(description).map(Text::translatable).orElse(null),
				Text.empty());
		this.setColor(0xFFFFFFFF);
	}

	public static TextDisplayOption description(@NotNull @TranslationKey String translationKey) {
		return new TextDisplayOption(
				translationKey + TranslationKeys.NAME_SUFFIX,
				translationKey + TranslationKeys.TOOLTIP_SUFFIX);
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
