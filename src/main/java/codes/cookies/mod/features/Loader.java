package codes.cookies.mod.features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to help all loader classes.
 */
public interface Loader {
    /**
     * Logger for debugging failed loading.
     */
    Logger LOGGER = LoggerFactory.getLogger(Loader.class);

    /**
     * Runs the runnable and prints an error if any exception occur.
     *
     * @param name     The name for context.
     * @param runnable The runnable to run.
     */
    static void load(String name, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception exception) {
            LOGGER.error("Unable to load feature %s: %s".formatted(name, exception.getClass().getName()), exception);
        }
    }

}
