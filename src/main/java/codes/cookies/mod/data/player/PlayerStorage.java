package codes.cookies.mod.data.player;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Storage for the {@linkplain PlayerData} to always get the correct instance.
 */
public class PlayerStorage {
    static final Path playerDataFolder = Path.of("config/cookiesmod/players");
    private static final Gson GSON = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setPrettyPrinting()
        .create();
    private static PlayerData playerData;

    /**
     * Get the current player data or load it.
     *
     * @return The player data.
     */
    @NotNull
    public static Optional<PlayerData> getPlayerData() {
        if (!getCurrentPlayer().flatMap(
            player -> Optional.ofNullable(playerData).map(data -> data.getPlayerUuid().equals(player))).orElse(false)) {
            savePlayerData();
            loadPlayerData();
        }

        return Optional.ofNullable(playerData);
    }

    /**
     * Return the currently active players uuid.
     *
     * @return The uuid.
     */
    public static Optional<UUID> getCurrentPlayer() {
        return Optional.of(MinecraftClient.getInstance()).map(minecraftClient -> minecraftClient.player)
            .map(Entity::getUuid);
    }

    /**
     * Save the current player data to the file.
     */
    public static void savePlayerData() {
        if (playerData == null) {
            return;
        }

        if (getCurrentPlayer().isEmpty()) {
            return;
        }

        final UUID player = getCurrentPlayer().get();
        final Path playerDataFile = playerDataFolder.resolve(player + ".json");

        try {
            Files.writeString(playerDataFile, GSON.toJson(playerData), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    /**
     * Load the current player data from the file.
     */
    private static void loadPlayerData() {
        if (getCurrentPlayer().isEmpty()) {
            return;
        }
        final UUID player = getCurrentPlayer().get();
        final Path playerDataFile = playerDataFolder.resolve(player + ".json");

        if (!Files.exists(playerDataFolder)) {
            try {
                Files.createDirectories(playerDataFolder);
            } catch (final IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        if (!Files.exists(playerDataFile)) {
            playerData = new PlayerData(player);
            savePlayerData();
        }
        final String content;
        try {
            content = Files.readString(playerDataFile);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        playerData = GSON.fromJson(content, PlayerData.class);
    }

}

