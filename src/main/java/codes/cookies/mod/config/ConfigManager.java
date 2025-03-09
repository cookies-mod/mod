package codes.cookies.mod.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import com.teamresourceful.resourcefulconfig.api.patching.ConfigPatchEvent;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.common.loader.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager to do various config related actions.
 */
public class ConfigManager {
	public static final Configurator CONFIGURATOR = new Configurator("cookies-mod");
	private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

	private static final Path configFolder = Path.of("config/cookiesmod");
	private static final Path configFile = configFolder.resolve("config.json");

	private static boolean hasBeenLoaded = false;

	/**
	 * @return Whether the config is loader or not.
	 */
	public static boolean isLoaded() {
		return hasBeenLoaded;
	}

	/**
	 * Processes the config.
	 */
	public static void load() {
		CookiesOptions.register();
		CONFIGURATOR.register(CookiesConfig.class, ConfigManager::registerMigrations);
		migrateToNewConfigSystemIfRequired();
		hasBeenLoaded = true;
	}

	public static void saveConfig(String reason) {
		log.info("Saving config: {}", reason);
		save();
	}

	public static void save() {
		CONFIGURATOR.saveConfig(CookiesConfig.class);
	}

	private static void migrateToNewConfigSystemIfRequired() {
		if (!Files.exists(configFile)) {
			return;
		}
		log.info("Detected old config file, migrating to new system!");

		final JsonObject configObject;
		try (InputStream stream = Files.newInputStream(configFile)) {
			final byte[] bytes = stream.readAllBytes();
			configObject = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final JsonObject jsonObject = ConfigMigrator.migrateToNewConfig(configObject);
		final ResourcefulConfig config = CONFIGURATOR.getConfig(CookiesConfig.class);
		Loader.loadConfig(config, jsonObject);
		config.save();

		try {
			Files.delete(configFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void registerMigrations(ConfigPatchEvent configPatchEvent) {
	}
}

