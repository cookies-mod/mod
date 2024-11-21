package codes.cookies.mod.render.hud.internal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.MinecraftClient;

import java.awt.*;

public class HudElementSettings {
	public static Codec<HudElementSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("x").forGetter(HudElementSettings::getRelativeX),
			Codec.FLOAT.fieldOf("y").forGetter(HudElementSettings::getRelativeY),
			Codec.FLOAT.fieldOf("scale").forGetter(HudElementSettings::getScale),
			Alignment.CODEC.fieldOf("alignment").forGetter(HudElementSettings::getAlignment),
			Codec.BOOL.optionalFieldOf("background", false).forGetter(HudElementSettings::isBackground),
			Codec.INT.optionalFieldOf("background_color", 0xFFFFFFFF).forGetter(HudElementSettings::getBackgroundColor)
	).apply(instance, HudElementSettings::new));

	@Setter
	private float x;
	@Setter
	private float y;
	@Getter
	private float scale = 1.0f;
	@Getter
	@Setter
	private Alignment alignment = Alignment.LEFT;
	@Getter
	@Setter
	private boolean background = false;
	@Getter
	@Setter
	private int backgroundColor = 0xFFFFFFFF;

	public HudElementSettings() {}

	public HudElementSettings(float x, float y, float scale, Alignment alignment, boolean background, int backgroundColor) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.alignment = alignment;
		this.background = background;
		this.backgroundColor = backgroundColor;
	}

	public float getRelativeX() {
		return x;
	}

	public int getX(int width) {
		return alignment.getX((int) (x * getScreenWidth()), width);
	}

	public int clampX(int width) {
		return (int) Math.clamp(getX(width), 0, Math.max(getScreenWidth() - (width * scale), 0));
	}

	public float getRelativeY() {
		return y;
	}

	public int getY() {
		return (int) (y * getScreenHeight());
	}

	public int clampY(int height) {
		return (int) Math.clamp(getY(), 0, Math.max(0, getScreenHeight() - (height * scale)));
	}

	public void applyDelta(float x, float y) {
		this.x = Math.clamp(this.x + x, 0, 1);
		this.y = Math.clamp(this.y + y, 0, 1);
	}

	public void setScale(float scale) {
		this.scale = Math.clamp(scale, 0.1f, 10);
	}

	public void applyScale(float scale) {
		this.setScale(this.scale + scale);
	}

	private int getScreenWidth() {
		return MinecraftClient.getInstance().getWindow().getScaledWidth();
	}

	private int getScreenHeight() {
		return MinecraftClient.getInstance().getWindow().getScaledHeight();
	}

	public Color getColorValue() {
		return new Color(getBackgroundColor(), true);
	}

	public void setColorValue(Color color) {
		this.backgroundColor = color.getRGB();
	}
}
