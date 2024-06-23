package dev.morazzer.cookies.mod.repository.constants;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to load all constants.
 */
@SuppressWarnings("MissingJavadoc")
public class RepositoryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger("repository/constants");

    public static Hotm hotm;
    public static PlotPrice plotPrice;

    public static void load(Path path) {
        hotm = new Hotm(resolve(path.resolve("hotm.json"), JsonObject.class));
        plotPrice = new PlotPrice(resolve(path.resolve("plot_cost.json"), JsonObject.class));
    }

    @Nullable
    private static <T> T resolve(Path path, Class<T> clazz) {
        final String read;
        try {
            read = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to load Cookies Hotm Constants", e);
            return null;
        }

        return JsonUtils.CLEAN_GSON.fromJson(read, clazz);
    }

}
