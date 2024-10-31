package dev.morazzer.cookies.mod.data.profile.migrations;

import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.data.Migration;

/**
 * Drops current selected craft helper item, this is due to a bug that I can't replicate that is fixed by doing this.
 */
public class ProfileDataMigration_0003 implements Migration<JsonObject> {
	@Override
	public int getNumber() {
		return 3;
	}

	@Override
	public void apply(JsonObject value) {
		value.remove("craft_helper_data");
	}

	@Override
	public Type getType() {
		return Type.PROFILE;
	}

	@Override
	public boolean mayFail() {
		return true;
	}
}
