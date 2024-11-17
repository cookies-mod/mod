package codes.cookies.mod.data;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.profile.GlobalProfileData;
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
	 * @return Whether this migration may fail without causing a data reset.
	 */
	default boolean mayFail() {
		return false;
	}

    /**
     * The supported migration types.
     */
    @AllArgsConstructor
    @Getter
    enum Type {
        /**
         * {@link ProfileData}
         */
        PROFILE(-1),
        /**
         * {@link GlobalProfileData}
         */
        GLOBAL_PROFILE(-1);

        @Setter
        int latest;
    }
}
