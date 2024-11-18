package codes.cookies.mod.data.mining.crystal;

import com.mojang.serialization.Codec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

/**
 * All types of crystal status, and their display name.
 */
@RequiredArgsConstructor
@Getter
public enum CrystalStatus implements StringIdentifiable {

	NOT_FOUND(Text.literal("✖ Not Placed").formatted(Formatting.RED)),
	FOUND(Text.literal("✔ Found").formatted(Formatting.GREEN)),
	PLACED(Text.literal("✔ Placed").formatted(Formatting.YELLOW));

	public static final Codec<CrystalStatus> CODEC = StringIdentifiable.createCodec(CrystalStatus::values);
	private final Text text;

	public static CrystalStatus getCrystalStatusFromText(String text) {
		return switch (text) {
			case "✖ Not Placed", "✔ Found" -> FOUND;
			case "✔ Placed" -> PLACED;
			default -> NOT_FOUND;
		};
	}

	@Override
	public String asString() {
		return name();
	}
}
