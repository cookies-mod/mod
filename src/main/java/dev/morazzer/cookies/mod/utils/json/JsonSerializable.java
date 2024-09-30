package dev.morazzer.cookies.mod.utils.json;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Notes an object as serializable.
 */
public interface JsonSerializable {

    /**
     * Logger for json related problems/debugs/infos.
     */
    Logger logger = LoggerFactory.getLogger("cookies-mod(json)");

    /**
     * Loads the object from a {@linkplain JsonElement}.
     *
     * @param jsonElement The json element.
     */
    void read(@NotNull JsonElement jsonElement);

    /**
     * Saves the object to a {@linkplain JsonElement}.
     *
     * @return The json element.
     */
    JsonElement write();

}
