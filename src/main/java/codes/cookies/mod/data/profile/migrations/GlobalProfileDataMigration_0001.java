package codes.cookies.mod.data.profile.migrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import codes.cookies.mod.data.Migration;

import net.minecraft.util.math.BlockPos;

/**
 * Drops all island storage data since the data structure was completely reworked with this data version in a way
 * where recovering is impossible.
 */
public class GlobalProfileDataMigration_0001 implements Migration<JsonObject> {

	@Override
	public int getNumber() {
		return 1;
	}

	@Override
	public void apply(JsonObject value) {
		final JsonObject islandStorage = value.getAsJsonObject("island_storage");
		if (islandStorage == null) {
			return;
		}
		final JsonArray newIslandStorage = new JsonArray();

		for (String key : islandStorage.keySet()) {
			this.modifySingle(key, islandStorage.getAsJsonArray(key), newIslandStorage);
		}
		value.add("island_storage", newIslandStorage);
	}

	private void modifySingle(String key, JsonArray items, JsonArray newIslandStorage) {
		long blockPosAsLong = Long.parseLong(key.split(";")[0]);
		final BlockPos secondBlockPos;
		if (key.contains(";")) {
			secondBlockPos = BlockPos.fromLong(Long.parseLong(key.split(";")[1]));
		} else {
			secondBlockPos = null;
		}
		final BlockPos blockPos = BlockPos.fromLong(blockPosAsLong);

		int index = 0;
		for (JsonElement item : items) {
			final JsonObject newItem = new JsonObject();
			newItem.addProperty("slot", index++);
			newItem.add("block_pos", this.toArray(blockPos));
			if (secondBlockPos != null) {
				newItem.add("second_chest", this.toArray(secondBlockPos));
			}
			newItem.add("item", item);
			newIslandStorage.add(newItem);
		}
	}

	private JsonArray toArray(BlockPos pos) {
		final JsonArray array = new JsonArray();
		array.add(pos.getX());
		array.add(pos.getY());
		array.add(pos.getZ());
		return array;
	}

	@Override
	public Type getType() {
		return Type.GLOBAL_PROFILE;
	}
}
