package dev.morazzer.cookies.mod.features.dungeons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.morazzer.cookies.mod.repository.constants.RepositoryConstants;

import dev.morazzer.cookies.mod.utils.json.JsonUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

import java.util.Optional;

import lombok.Setter;

/**
 * Static data that informs about rooms within the dungeon.
 */
@Setter
public final class DungeonRoomData {
	private static Path path;
	public static List<DungeonRoomData> DUNGEON_ROOMS = new ArrayList<>();

	public static void load(Path filePath) {
		path = filePath;
		final JsonArray resolve = RepositoryConstants.resolve(filePath, JsonArray.class);
		final DataResult<List<DungeonRoomData>> parse = LIST_CODEC.parse(JsonOps.INSTANCE, resolve);
		if (parse.isSuccess()) {
			DUNGEON_ROOMS.addAll(parse.getOrThrow());
		}
	}

	public static Codec<DungeonRoomData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("id").forGetter(DungeonRoomData::id),
			Codec.STRING.fieldOf("name").forGetter(DungeonRoomData::name),
			Codec.INT.optionalFieldOf("secrets")
					.xmap(
							integer -> integer.orElse(0),
							integer -> integer == 0 ? Optional.empty() : Optional.of(integer))
					.forGetter(DungeonRoomData::secrets)).apply(instance, DungeonRoomData::new));

	public static Codec<List<DungeonRoomData>> LIST_CODEC = CODEC.listOf();
	private final List<String> id;
	private String name;
	private int secrets;
	public boolean wasUpdated;

	public DungeonRoomData(
			List<String> id, String name, int secrets) {
		this.id = id;
		this.name = name;
		this.secrets = secrets;
	}

	public static void save() throws IOException {
		final DataResult<JsonElement> jsonElementDataResult = LIST_CODEC.encodeStart(JsonOps.INSTANCE, DUNGEON_ROOMS);
		if (jsonElementDataResult.isSuccess()) {
			final JsonElement orThrow = jsonElementDataResult.getOrThrow();
			Files.writeString(
					path,
					JsonUtils.CLEAN_GSON.toJson(orThrow),
					StandardCharsets.UTF_8,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
	}

	public boolean matches(String id) {
		return this.id.contains(id);
	}

	public List<String> id() {return this.id;}

	public String name() {return this.name;}

	public int secrets() {return this.secrets;}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (DungeonRoomData) obj;
		return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && this.secrets == that.secrets;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.secrets);
	}

	@Override
	public String toString() {
		return "DungeonRoomData[" + "id=" + this.id + ", " + "name=" + this.name + ", " + "secrets=" + this.secrets +
			   ']';
	}

}
