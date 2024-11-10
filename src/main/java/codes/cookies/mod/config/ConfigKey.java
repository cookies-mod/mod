package codes.cookies.mod.config;

import codes.cookies.mod.config.system.Option;
import java.util.function.Function;

/**
 * Quick access to config values, without ruining the system.
 *
 * @param <T> The
 */
public class ConfigKey<T> {
    private final Function<CookiesConfig, Option<T, ?>> optionFunction;

    /**
     * Creates a new config key.
     *
     * @param optionFunction The location of the config key.
     */
    public ConfigKey(Function<CookiesConfig, Option<T, ?>> optionFunction) {
        this.optionFunction = optionFunction;
    }


    /**
     * @return The config value.
     */
    public T get() {
        return this.optionFunction.apply(ConfigManager.getConfig()).getValue();
    }

    /**
     * @param value The new value.
     */
    public void set(T value) {
        this.optionFunction.apply(ConfigManager.getConfig()).setValue(value);
    }


}
