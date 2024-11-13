package codes.cookies.mod.repository.constants.mining;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.extern.slf4j.Slf4j;

import net.minecraft.util.math.BlockPos;

@Slf4j
public class ShaftCorpseLocations {

	private static final List<ShaftLocations> locations = new ArrayList<>();

	public static void load(JsonArray jsonObject) {
		final DataResult<List<ShaftLocations>> parse = CachedShaftLocations.LIST_CODEC.parse(
				JsonOps.INSTANCE,
				jsonObject);
		if (parse.isSuccess()) {
			locations.addAll(parse.getOrThrow());
			return;
		}
		final String message = parse.error().orElseThrow().message();
		log.error("Can't load museum data: {}", message);
	}

	public static Optional<ShaftLocations> getById(String id) {
		return locations.stream().filter(locations -> locations.id().equals(id.trim())).findFirst();
	}

	public static List<ShaftLocations> getCached() {
		return locations;
	}

	public static ShaftLocations getCachedOrCreate(String id) {
		return getById(id).orElseGet(() -> register(new MutableShaftLocations(id)));
	}

	private static ShaftLocations register(MutableShaftLocations mutableShaftLocations) {
		locations.add(mutableShaftLocations);
		return mutableShaftLocations;
	}

	public interface ShaftLocations {
		String id();
		List<BlockPos> corpseLocations();
		boolean cached();
	}

	public record CachedShaftLocations(String id, List<BlockPos> corpseLocations) implements ShaftLocations {
		public static final Codec<ShaftLocations> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(ShaftLocations::id),
				BlockPos.CODEC.listOf().fieldOf("locations").forGetter(ShaftLocations::corpseLocations)
		).apply(instance, CachedShaftLocations::create));

		private static ShaftLocations create(String s, List<BlockPos> blockPos) {
			return new CachedShaftLocations(s, new ArrayList<>(blockPos));
		}

		public static final Codec<List<ShaftLocations>> LIST_CODEC = CODEC.listOf();

		@Override
		public boolean cached() {
			return true;
		}
	}

	public static class MutableShaftLocations implements ShaftLocations {
		private final String id;
		private final List<BlockPos> corpseLocations;

		public MutableShaftLocations(String id) {
			this.id = id;
			this.corpseLocations = new ArrayList<>();
		}

		@Override
		public String id() {
			return this.id;
		}

		@Override
		public List<BlockPos> corpseLocations() {
			return corpseLocations;
		}

		@Override
		public boolean cached() {
			return false;
		}
	}

}
