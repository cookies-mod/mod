package dev.morazzer.cookies.mod.features.cleanup;

import dev.morazzer.cookies.mod.features.Loader;

/**
 * Utility class to load all cleanup features.
 */
@SuppressWarnings("MissingJavadoc")
public class CleanupFeatures {

    public static void load() {
        Loader.load("CoopCleanup", CoopCleanupFeature::new);
    }

}
