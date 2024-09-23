package dev.morazzer.cookies.mod.config.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;

import net.minecraft.client.MinecraftClient;

import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.NotNull;

/**
 * A hud element and the properties associated with it.
 */
public class HudElementPosition implements JsonSerializable {

	public float x;
	public float y;
	public float scale;

	public HudElementPosition(float x, float y, float scale) {
		this.x = x;
		this.y = y;
		this.scale = scale;
	}

	public float clampX(int width) {
		return MathHelper.clamp(x, 0,
            1 - ((float) width / MinecraftClient.getInstance().getWindow().getScaledWidth()));
	}

	public float clampY(int height) {
		return MathHelper.clamp(
				y,
				0,
				1 - ((float) height / MinecraftClient.getInstance().getWindow().getScaledHeight()));
	}

	@Override
	public void read(@NotNull JsonElement jsonElement) {
		final JsonObject asJsonObject = jsonElement.getAsJsonObject();
		this.x = asJsonObject.get("x").getAsFloat();
		this.y = asJsonObject.get("y").getAsFloat();
		this.scale = asJsonObject.get("scale").getAsFloat();
	}

	@Override
	public @NotNull JsonElement write() {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("x", this.x);
		jsonObject.addProperty("y", this.y);
		jsonObject.addProperty("scale", this.scale);
		return jsonObject;
	}
}
