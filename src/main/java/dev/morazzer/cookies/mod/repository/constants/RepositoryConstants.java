package dev.morazzer.cookies.mod.repository.constants;

import java.nio.file.Path;

/**
 * Utility class to load all constants.
 */
public class RepositoryConstants {
    public static Hotm hotm;

    @SuppressWarnings("MissingJavadoc")
    public static void load(Path path) {
        hotm = Hotm.load(path.resolve("hotm.json"));
    }

}
