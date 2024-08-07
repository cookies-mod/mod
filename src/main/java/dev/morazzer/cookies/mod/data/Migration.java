package dev.morazzer.cookies.mod.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic migration for every type of data.
 *
 * @param <T> The type of the data.
 */
public interface Migration<T> {

    /**
     * Gets the unique number of the migration. This number incrementally counts up with each migration and is used to
     * track whether a migration is already applied or not.
     *
     * @return The number.
     */
    int getNumber();

    /**
     * Applies the migration to the data.
     *
     * @param value The data to apply it to.
     */
    void apply(T value);

    /**
     * The type of data that is handled by this migration.
     *
     * @return The type.
     */
    Type getType();

    /**
     * The supported migration types.
     */
    @AllArgsConstructor
    @Getter
    enum Type {
        /**
         * {@link dev.morazzer.cookies.mod.data.profile.ProfileData}
         */
        PROFILE(-1),
        /**
         * {@link dev.morazzer.cookies.mod.data.profile.profile.GlobalProfileData}
         */
        GLOBAL_PROFILE(-1);

        @Setter
        int latest;
    }
}
