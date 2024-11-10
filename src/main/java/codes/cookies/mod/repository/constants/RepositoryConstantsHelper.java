package codes.cookies.mod.repository.constants;

import codes.cookies.mod.utils.json.JsonUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface RepositoryConstantsHelper {
	Logger LOGGER = LoggerFactory.getLogger("repository/constants");

	@Nullable
	static <T> T resolve(Path path, Class<T> clazz) {
		final String read;
		try {
			read = Files.readString(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.error("Failed to load Cookies Constant", e);
			return null;
		}

		return JsonUtils.CLEAN_GSON.fromJson(read, clazz);
	}


}
