package codes.cookies.mod.config;

import codes.cookies.mod.config.system.yacl.YaclConfigReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import codes.cookies.mod.config.system.parsed.ConfigProcessor;
import codes.cookies.mod.config.system.parsed.ConfigReader;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager to do various config related actions.
 */
public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    @Getter
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .excludeFieldsWithoutExposeAnnotation()
        .serializeSpecialFloatingPointValues()
        .enableComplexMapKeySerialization()
        .create();

    @Getter
    private static final Path configFolder = Path.of("config/cookiesmod");
    private static final Path configFile = configFolder.resolve("config.json");
    private static CookiesConfig config;
    @Getter
    private static ConfigReader configReader;

    /**
     * Gets the config instance or process it if it's not available.
     *
     * @return The config.
     */
    public static CookiesConfig getConfig() {
        if (config == null) {
            processConfig();
        }

        return config;
    }

    /**
     * @return Whether the config is loader or not.
     */
    public static boolean isLoaded() {
        return config != null;
    }

    /**
     * Processes the config.
     */
    public static void processConfig() {
        config = new CookiesConfig();
        reload();
        configReader = new YaclConfigReader();
        ConfigProcessor.processConfig(config, configReader);
    }

    /**
     * Reloads the config from the file.
     */
    public static void reload() {
        config.load(loadConfig());
    }

    /**
     * Loads or creates the config file if it's not available.
     *
     * @return The config as {@linkplain JsonObject}.
     */
    private static JsonObject loadConfig() {
        if (Files.exists(configFile)) {
            return gson.fromJson(
                ExceptionHandler.removeThrows(() -> Files.readString(configFile), "{}"),
                JsonObject.class
            );
        } else {
            saveConfig(false, "first-save");
        }

        return new JsonObject();
    }

    /**
     * Saves the current state of the config.
     *
     * @param createBackup Whether there should be a backup of the old config.
     * @param reason       The reason why the save was called.
     */
    public static void saveConfig(boolean createBackup, String reason) {
        if (!Files.exists(configFolder)) {
            try {
                Files.createDirectories(configFolder);
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }

        if (createBackup) {
            try {
                Files.copy(
                    configFile,
                    configFile.resolveSibling("config.backup.json"),
                    StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException exception) {
				// we just log a failed config backup and move on!
				log.error("Failed to save config", exception);
            }
        }

        try {
            Files.writeString(
                configFile,
                gson.toJson(config.save()),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        log.info("Saving config with with reason: {}", reason);
    }

}

