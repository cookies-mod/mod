package dev.morazzer.cookies.mod.repository;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.repository.constants.RepositoryConstants;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
import dev.morazzer.mods.cookies.generated.BuildInfo;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository related methods.
 */
public class Repository {

    private static final String indexLocation = "https://raw.githubusercontent.com/cookies-mod/data/main";
    private static final Path ROOT = Path.of("cookies");
    private static final Logger LOGGER = LoggerFactory.getLogger("repository");

    @SuppressWarnings("MissingJavadoc")
    public static void loadRepository() {
        if (!Files.exists(ROOT)) {
            try {
                Files.createDirectories(ROOT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JsonObject index;
        try {
            index = JsonUtils.CLEAN_GSON.fromJson(download("index"), JsonObject.class);

            final JsonObject requires = index.getAsJsonObject("requires");
            final String modVersion = requires.get("mod_version").getAsString();
            final VersionPredicate versionPredicate =
                ExceptionHandler.removeThrows(() -> VersionPredicate.parse(modVersion));
            if (versionPredicate.test(BuildInfo.version)) {
                index.getAsJsonObject("files").entrySet().forEach(entry -> isUpToDate("", entry));
            }
        } catch (IOException e) {
            LOGGER.error("Error while loading cookies repository, continuing with old data.", e);
        }

        RepositoryItem.load(ROOT.resolve("items.json"));
        Recipe.load(ROOT.resolve("recipes.json"));
        RepositoryConstants.load(ROOT.resolve("constants"));
    }

    private static String download(String file) throws IOException {
        final URLConnection urlConnection =
            URI.create("%s/%s.json".formatted(indexLocation, file)).toURL().openConnection();
        urlConnection.connect();
        final byte[] bytes = urlConnection.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void isUpToDate(String path, Map.Entry<String, JsonElement> entry) {
        final String key = entry.getKey();
        if (entry.getValue().isJsonObject()) {
            entry.getValue().getAsJsonObject().entrySet().forEach(otherEntry -> {
                isUpToDate(path + entry.getKey() + "/", otherEntry);
            });
            return;
        }

        Path folder = ROOT.resolve(path.isBlank() ? "." : path);
        Path file = folder.resolve(entry.getKey() + ".json");
        final String hash = entry.getValue().getAsString();
        final String fileHash = getFileHash(file);
        if (hash.equals(fileHash)) {
            return;
        }

        LOGGER.info("Downloading {}", path + key);
        try {
            final String download = download(path + key);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }
            Files.writeString(file, download, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable String getFileHash(Path resolve) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            if (!Files.exists(resolve)) {
                return null;
            }
            final byte[] digest = messageDigest.digest(Files.readAllBytes(resolve));
            return byteToHexString(digest);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String byteToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte singleByte : bytes) {
            result.append(String.format("%02x", singleByte));
        }
        return result.toString().toUpperCase(Locale.ROOT);
    }

}
