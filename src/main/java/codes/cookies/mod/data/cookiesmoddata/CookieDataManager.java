package codes.cookies.mod.data.cookiesmoddata;

import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CookieDataManager {
	public static final Path MOD_DATA_FOLDER = Path.of("config/cookiesmod/data");

	public static void load() {
		if(!Files.exists(MOD_DATA_FOLDER)) {
			ExceptionHandler.tryCatch(() -> Files.createDirectories(MOD_DATA_FOLDER));
		}

		for (Field declaredField : CookieDataInstances.class.getDeclaredFields()) {
			ExceptionHandler.tryCatch(() -> load(declaredField));
		}
	}

	public static void save(CookiesModData modData) throws IOException {
		final Path dataLocation = MOD_DATA_FOLDER.resolve(modData.getFileLocation());
		final JsonElement jsonElement = modData.write();
		final byte[] content = jsonElement.toString().getBytes(StandardCharsets.UTF_8);

		ExceptionHandler.tryCatch(() -> {
			saveFile(dataLocation, content);
		});
	}

	private static void load(Field declaredField) {
		declaredField.setAccessible(true);

		final Object instance;
		try {
			instance = declaredField.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		if (!(instance instanceof CookiesModData data)) {
			return;
		}

		final Path dataLocation = MOD_DATA_FOLDER.resolve(data.getFileLocation());
		if (!Files.exists(dataLocation)) {
			return;
		}

		final byte[] bytes;
		try {
			bytes = readFile(dataLocation);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final String fileContent = new String(bytes, StandardCharsets.UTF_8);
		final JsonElement fileElement = JsonParser.parseString(fileContent);
		if(fileElement.isJsonObject())
			data.read(fileElement.getAsJsonObject());
	}

	private static byte[] readFile(Path filePath) throws IOException {
		return Files.readAllBytes(filePath);
	}

	@SneakyThrows
	private static void saveFile(Path filePath, byte[] content) {
		Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
