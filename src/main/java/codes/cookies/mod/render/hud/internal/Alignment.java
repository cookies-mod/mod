package codes.cookies.mod.render.hud.internal;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;

public enum Alignment implements StringIdentifiable {
	LEFT, MIDDLE, RIGHT;

	public static Codec<Alignment> CODEC = StringIdentifiable.createBasicCodec(Alignment::values);

	public int getX(int x, int width) {
		return switch (this) {
			case LEFT -> x;
			case MIDDLE -> x - width / 2;
			case RIGHT -> x - width;
		};
	}

	@Override
	public String asString() {
		return name().toLowerCase();
	}
}
