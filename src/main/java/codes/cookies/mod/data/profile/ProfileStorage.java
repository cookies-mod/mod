package codes.cookies.mod.data.profile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import codes.cookies.mod.api.ApiManager;
import com.google.gson.JsonObject;
import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.data.DataMigrations;
import codes.cookies.mod.data.Migration;
import codes.cookies.mod.data.player.PlayerStorage;
import codes.cookies.mod.events.profile.ProfileSwapEvent;
import codes.cookies.mod.events.profile.ServerSwapEvent;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import codes.cookies.mod.utils.json.JsonUtils;

import com.mojang.logging.LogUtils;

import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.data.region.Environment;

import net.minecraft.client.MinecraftClient;

import org.jetbrains.annotations.NotNull;

/**
 * Storage for the {@linkplain ProfileData} to always get the correct instance.
 */
public class ProfileStorage {
	private static final String LOGGING_KEY = "profileStorage";

	private static final Path PROFILE_DATA_FOLDER = Path.of("config/cookiesmod/profiles");
	private static  ProfileData profileData;

	private static boolean isOnHypixelAlpha = false;

	/**
	 * Registers the listeners for automatic profile swapping.
	 */
	public static void register() {
		ProfileSwapEvent.AFTER_SET_NO_UUID.register(() -> CookiesMod.getExecutorService().execute(() -> {
			saveCurrentProfile();
			loadCurrentProfile();
		}));
		ServerSwapEvent.SERVER_SWAP.register(() -> CookiesMod.getExecutorService()
				.execute(ProfileStorage::saveCurrentProfile));

		CookiesMod.getExecutorService().scheduleAtFixedRate(ProfileStorage::saveCurrentProfile, 5, 5,
				TimeUnit.MINUTES);
		HypixelModAPI.getInstance().createHandler(ClientboundHelloPacket.class, ProfileStorage::onJoinHypixel);
	}

	private static void onJoinHypixel(ClientboundHelloPacket clientboundHelloPacket) {
		if(clientboundHelloPacket.getEnvironment() ==  Environment.BETA) {
			isOnHypixelAlpha = true;
			CookiesUtils.sendFailedMessage("You are on Hypixel Alpha, profile data will not be saved!");
		} else {
			isOnHypixelAlpha = false;
		}
	}


	/**
	 * Save the current profile data instance to the file.
	 */
	public static synchronized void saveCurrentProfile() {
		if (profileData == null) {
			return;
		}

		if (PlayerStorage.getCurrentPlayer().isEmpty() || SkyblockUtils.getLastProfileId().isEmpty() || isOnHypixelAlpha) {
			return;
		}

		long started = System.currentTimeMillis();
		final Path playerDirectory = PROFILE_DATA_FOLDER.resolve(profileData.getPlayerUuid().toString());
		final Path profileFile = playerDirectory.resolve(profileData.getProfileUuid() + ".json");
		DevUtils.log(LOGGING_KEY, "Saving profile data to %s", profileFile);

		ExceptionHandler.removeThrows(() -> Files.createDirectories(profileFile.getParent()));
		final JsonObject jsonObject = JsonUtils.toJsonObject(profileData);
		DataMigrations.writeLatest(jsonObject, Migration.Type.PROFILE);
		profileData.save();
		ExceptionHandler.removeThrows(() -> Files.writeString(
				profileFile,
				JsonUtils.CLEAN_GSON.toJson(jsonObject),
				StandardCharsets.UTF_8,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE));
		DevUtils.log(LOGGING_KEY, "Saved profile in %sms", System.currentTimeMillis() - started);
	}

	private static void loadCurrentProfile() {
		CookiesMod.getExecutorService().execute(() -> loadCurrentProfile(false));
	}

	/**
	 * Load the current profile data instance from the file.
	 */
	public static synchronized void loadCurrentProfile(boolean skipCheck) {
		if (PlayerStorage.getCurrentPlayer().isEmpty() || (SkyblockUtils.getLastProfileId().isEmpty() || skipCheck)) {
			return;
		}

		long started = System.currentTimeMillis();
		final Path playerDirectory = PROFILE_DATA_FOLDER.resolve(PlayerStorage.getCurrentPlayer().get().toString());
		final Path profileFile = playerDirectory.resolve(SkyblockUtils.getLastProfileId().get() + ".json");

		if (!Files.exists(profileFile)) {
			profileData =
					new ProfileData(PlayerStorage.getCurrentPlayer().get(), SkyblockUtils.getLastProfileId().get());
			profileData.load();
			saveCurrentProfile();
			return;
		}

		DevUtils.log(LOGGING_KEY, "Loading profile data from %s", profileFile);
		final JsonObject jsonObject =
				JsonUtils.CLEAN_GSON.fromJson(ExceptionHandler.removeThrows(() -> Files.readString(
						profileFile,
						StandardCharsets.UTF_8), "{}"), JsonObject.class);
		if (!DataMigrations.migrate(jsonObject, Migration.Type.PROFILE)) {
			CookiesUtils.sendFailedMessage("Failed to apply a mandatory data migration, resetting local profile data!");
			profileData = new ProfileData(
					PlayerStorage.getCurrentPlayer().get(),
					SkyblockUtils.getLastProfileId().get());
			profileData.load();
		} else {
			profileData = JsonUtils.fromJson(new ProfileData(
					PlayerStorage.getCurrentPlayer().get(),
					SkyblockUtils.getLastProfileId().get()), jsonObject);
			profileData.load();
		}

		DevUtils.log(LOGGING_KEY, "Loaded profile in %sms", System.currentTimeMillis() - started);
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
		CookiesMod.getExecutorService().execute(() -> {
			saveCurrentProfile();
			loadCurrentProfile();
		});

		return Optional.ofNullable(profileData);
	}

}
