package codes.cookies.mod.config.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import codes.cookies.mod.utils.json.JsonSerializable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Used to store a single integer in the config, without having a config setting associated to it.
 */
@Setter
@Getter
public class IntegerData implements JsonSerializable {

    private int value;

    /**
     * Creates a new integer data instance.
     *
     * @param value The initial value.
     */
    public IntegerData(int value) {
        this.value = value;
    }


    @Override
    public void read(@NotNull JsonElement jsonElement) {
        this.value = jsonElement.getAsInt();
    }

    @Override
    public @NotNull JsonElement write() {
        return new JsonPrimitive(this.value);
    }
}
