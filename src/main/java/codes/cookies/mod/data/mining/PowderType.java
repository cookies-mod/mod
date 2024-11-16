package codes.cookies.mod.data.mining;

import com.mojang.serialization.Codec;
import lombok.Getter;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import java.util.Optional;

@Getter
@SuppressWarnings("MissingJavadoc")
public enum PowderType implements StringIdentifiable {
	MITHRIL("Mithril Powder", Formatting.DARK_GREEN, 10000),
	GEMSTONE("Gemstone Powder", Formatting.LIGHT_PURPLE, 10000),
	GLACITE("Glacite Powder", Formatting.AQUA, 1000 * 60 * 5);

	private final String name;
	private final Formatting formatting;
	private final Text text;
	private final String displayName;
	private final int autoPauseTime;

	public static Codec<PowderType> CODEC = StringIdentifiable.createCodec(PowderType::values);

	PowderType(String name, Formatting formatting, int autoPauseTime) {
		this.name = name;
		this.displayName = name.replace("Powder", "").trim();
		this.formatting = formatting;
		this.text = Text.literal(name).formatted(formatting);
		this.autoPauseTime = autoPauseTime;
	}

	public static Optional<PowderType> getByDisplayName(String powder) {
		for (PowderType value : values()) {
			if (value.getDisplayName().equalsIgnoreCase(powder)) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	@Override
	public String asString() {
		return this.name();
	}
}
