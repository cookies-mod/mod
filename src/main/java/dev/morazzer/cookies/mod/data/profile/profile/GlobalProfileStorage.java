package dev.morazzer.cookies.mod.data.profile.profile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.data.DataMigrations;
import dev.morazzer.cookies.mod.data.Migration;
import dev.morazzer.cookies.mod.data.player.PlayerStorage;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;

/**
 * The store for the {@link GlobalProfileData}
 */
public class GlobalProfileStorage {
	private static final String LOGGING_KEY = "globalProfileStorage";
	private static final Path PROFILE_DATA_FOLDER = Path.of("config/cookiesmod/profiles");

	private GlobalProfileStorage() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Load the current profile data instance from the file.
	 *
	 * @param uuid The uuid of the profile to load.
	 * @return The global profile data
	 */
	public static GlobalProfileData load(UUID uuid) {
		final Path profileFile = PROFILE_DATA_FOLDER.resolve(uuid + ".json");

		if (!Files.exists(profileFile)) {
			final GlobalProfileData globalProfileData = new GlobalProfileData(uuid);
			save(globalProfileData);
			return globalProfileData;
		}

		DevUtils.log(LOGGING_KEY, "Loading global profile data from %s", profileFile);
		final JsonObject jsonObject =
				JsonUtils.CLEAN_GSON.fromJson(ExceptionHandler.removeThrows(() -> Files.readString(
						profileFile,
						StandardCharsets.UTF_8)), JsonObject.class);
		if (!DataMigrations.migrate(jsonObject, Migration.Type.GLOBAL_PROFILE)) {
			CookiesUtils.sendFailedMessage(
					"Failed to apply a mandatory data migration, resetting local global profile data!");
			return new GlobalProfileData(uuid);
		}

		return JsonUtils.fromJson(new GlobalProfileData(uuid), jsonObject);
	}

	/**
	 * Save the current profile data instance to the file.
	 *
	 * @param globalProfileData The data to save.
	 */
	public static void save(GlobalProfileData globalProfileData) {
		if (globalProfileData == null) {
			return;
		}

		if (PlayerStorage.getCurrentPlayer().isEmpty() || SkyblockUtils.getLastProfileId().isEmpty()) {
			return;
		}

		final Path profileFile = PROFILE_DATA_FOLDER.resolve(globalProfileData.getUuid() + ".json");
		DevUtils.log(LOGGING_KEY, "Saving global profile data to %s", profileFile);

		ExceptionHandler.removeThrows(() -> Files.createDirectories(profileFile.getParent()));
		final JsonObject jsonObject = JsonUtils.toJsonObject(globalProfileData);
		DataMigrations.writeLatest(jsonObject, Migration.Type.GLOBAL_PROFILE);
		ExceptionHandler.removeThrows(() -> Files.writeString(
				profileFile,
				JsonUtils.CLEAN_GSON.toJson(jsonObject),
				StandardCharsets.UTF_8,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE));
	}
}
