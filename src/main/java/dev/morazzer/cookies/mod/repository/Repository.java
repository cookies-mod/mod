package dev.morazzer.cookies.mod.repository;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
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
import org.jetbrains.annotations.Nullable;

/**
 * Repository related methods.
 */
public class Repository {

    private static final String indexLocation = "https://raw.githubusercontent.com/cookies-mod/data/main";
    private static final Path ROOT = Path.of("cookies");

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        index.entrySet().forEach(Repository::isUpToDate);

        RepositoryItem.load(ROOT.resolve("items.json"));
        Recipe.load(ROOT.resolve("recipes.json"));
    }

    private static String download(String file) throws IOException {
        final URLConnection urlConnection =
            URI.create("%s/%s.json".formatted(indexLocation, file)).toURL().openConnection();
        urlConnection.connect();
        final byte[] bytes = urlConnection.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void isUpToDate(Map.Entry<String, JsonElement> entry) {
        final String key = entry.getKey();
        final String hash = entry.getValue().getAsString();
        final String fileHash = getFileHash(key);
        if (hash.equals(fileHash)) {
            return;
        }

        System.err.println("Downloading " + key);
        try {
            final String download = download(entry.getKey());
            Files.writeString(ROOT.resolve("%s.json".formatted(entry.getKey())), download, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable String getFileHash(String fileName) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            final Path resolve = ROOT.resolve("%s.json".formatted(fileName));
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
