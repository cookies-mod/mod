package codes.cookies.mod.utils.items;

import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper interface to make accessing data easier, without having duplicated code.
 *
 * @param <T> The type of the data.
 */
public interface Value<T> {
    /**
     * Gets the data as an optional.
     *
     * @return The data.
     */
    default Optional<T> getAsOptional() {
        return Optional.ofNullable(get());
    }

    /**
     * Gets the data without any modification.
     *
     * @return The data.
     */
    T get();

    /**
     * Gets the data or the default value if it is null.
     *
     * @param defaultValue The default value.
     * @return The data.
     */
    @Nullable
    default T getOrDefault(@Nullable T defaultValue) {
        final T t = get();
        return t == null ? defaultValue : t;
    }

    /**
     * Gets the data or the default value from the supplier if it is null.
     *
     * @param defaultValueSupplier The default value supplier.
     * @return The data.
     */
    @Nullable
    default T getOrDefault(@NotNull Supplier<T> defaultValueSupplier) {
        final T t = get();
        return t == null ? defaultValueSupplier.get() : t;
    }
}
