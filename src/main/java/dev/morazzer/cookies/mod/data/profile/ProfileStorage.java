package dev.morazzer.cookies.mod.data.profile;

import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.data.DataMigrations;
import dev.morazzer.cookies.mod.data.Migration;
import dev.morazzer.cookies.mod.data.player.PlayerStorage;
import dev.morazzer.cookies.mod.events.profile.ProfileSwapEvent;
import dev.morazzer.cookies.mod.events.profile.ServerSwapEvent;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * Storage for the {@linkplain dev.morazzer.cookies.mod.data.profile.ProfileData} to always get the correct instance.
 */
public class ProfileStorage {
    private static final String LOGGING_KEY = "profileStorage";

    private static final Path PROFILE_DATA_FOLDER = Path.of("config/cookiesmod/profiles");
    private static ProfileData profileData;

    /**
     * Registers the listeners for automatic profile swapping.
     */
    public static void register() {
        ProfileSwapEvent.AFTER_SET_NO_UUID.register(() -> {
            saveCurrentProfile();
            loadCurrentProfile();
        });
        ServerSwapEvent.SERVER_SWAP.register(ProfileStorage::saveCurrentProfile);

        CookiesMod.getExecutorService().scheduleAtFixedRate(
            ProfileStorage::saveCurrentProfile,
            5,
            5,
            TimeUnit.MINUTES
        );
    }

    /**
     * Save the current profile data instance to the file.
     */
    public static void saveCurrentProfile() {
        if (profileData == null) {
            return;
        }

        if (PlayerStorage.getCurrentPlayer().isEmpty() || SkyblockUtils.getLastProfileId().isEmpty()) {
            return;
        }

        final Path playerDirectory = PROFILE_DATA_FOLDER.resolve(profileData.getPlayerUuid().toString());
        final Path profileFile = playerDirectory.resolve(profileData.getProfileUuid() + ".json");
        DevUtils.log(LOGGING_KEY, "Saving profile data to %s", profileFile);

        ExceptionHandler.removeThrows(() -> Files.createDirectories(profileFile.getParent()));
        final JsonObject jsonObject = JsonUtils.toJsonObject(profileData);
        DataMigrations.writeLatest(jsonObject);
        profileData.save();
        ExceptionHandler.removeThrows(() -> Files.writeString(
            profileFile,
            JsonUtils.CLEAN_GSON.toJson(jsonObject),
            StandardCharsets.UTF_8,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
        ));
    }

    /**
     * Load the current profile data instance from the file.
     */
    private static void loadCurrentProfile() {
        if (PlayerStorage.getCurrentPlayer().isEmpty() || SkyblockUtils.getLastProfileId().isEmpty()) {
            return;
        }

        final Path playerDirectory = PROFILE_DATA_FOLDER.resolve(PlayerStorage.getCurrentPlayer().get().toString());
        final Path profileFile = playerDirectory.resolve(SkyblockUtils.getLastProfileId().get() + ".json");

        if (!Files.exists(profileFile)) {
            profileData = new ProfileData(
                PlayerStorage.getCurrentPlayer().get(),
                SkyblockUtils.getLastProfileId().get()
            );
            profileData.load();
            saveCurrentProfile();
            return;
        }

        DevUtils.log(LOGGING_KEY, "Loading profile data from %s", profileFile);
        final JsonObject jsonObject =
            JsonUtils.CLEAN_GSON.fromJson(ExceptionHandler.removeThrows(() -> Files.readString(
                profileFile,
                StandardCharsets.UTF_8
            )), JsonObject.class);
        DataMigrations.migrate(jsonObject, Migration.Type.PROFILE);

        profileData = JsonUtils.fromJson(new ProfileData(
            PlayerStorage.getCurrentPlayer().get(),
            SkyblockUtils.getLastProfileId().get()
        ), jsonObject);
        profileData.load();
    }

    /**
     * Get the current profile data.
     *
     * @return The current profile data.
     */
    @NotNull
    public static Optional<ProfileData> getCurrentProfile() {
        if (!SkyblockUtils.isCurrentlyInSkyblock() || PlayerStorage.getCurrentPlayer().isEmpty()) {
            return Optional.empty();
        }

        // Currently loaded profile is still the active one
        if (Optional.ofNullable(profileData).map(ProfileData::isActive).orElse(false)) {
            return Optional.of(profileData);
        }

        saveCurrentProfile();
        loadCurrentProfile();

        return Optional.ofNullable(profileData);
    }

}