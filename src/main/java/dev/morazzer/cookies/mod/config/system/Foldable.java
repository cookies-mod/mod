package dev.morazzer.cookies.mod.config.system;

import com.google.gson.JsonElement;
import net.minecraft.text.Text;

/**
 * A foldable that will be displayed as such in the config.
 */
public abstract class Foldable implements SaveLoadHelper {

    /**
     * Gets the display name of the foldable.
     *
     * @return The name.
     */
    public abstract Text getName();

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
