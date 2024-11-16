package codes.cookies.mod.config.system;

import codes.cookies.mod.translations.TranslationKeys;
import com.google.gson.JsonElement;

/**
 * A foldable that will be displayed as such in the config.
 */
public abstract class Foldable implements SaveLoadHelper, TranslationKeys {

    /**
     * Gets the display name of the foldable.
     *
     * @return The name.
     */
    public abstract String getName();

    /**
     * @see SaveLoadHelper#load_(JsonElement)
     */
    @SuppressWarnings("MissingJavadoc")
    public final void load(JsonElement jsonObject) {
        load_(jsonObject);
    }

    /**
     * @see SaveLoadHelper#save_()
     */
    @SuppressWarnings("MissingJavadoc")
    public final JsonElement save() {
        return save_();
    }

}
