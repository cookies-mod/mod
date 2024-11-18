package codes.cookies.mod.data.mining.crystal;

import java.util.Optional;

import codes.cookies.mod.utils.skyblock.playerlist.widgets.crystal.CrystalOrigin;
import com.mojang.serialization.Codec;
import lombok.AllArgsConstructor;
import lombok.Getter;

import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

/**
 * All crystal types in the game plus their respective formatting.
 */
@Getter
@AllArgsConstructor
public enum CrystalType implements StringIdentifiable {

	JADE(CrystalOrigin.CRYSTAL_HOLLOWS, Formatting.GREEN),
	AMBER(CrystalOrigin.CRYSTAL_HOLLOWS, Formatting.GOLD),
	AMETHYST(CrystalOrigin.CRYSTAL_HOLLOWS, Formatting.DARK_PURPLE),
	SAPPHIRE(CrystalOrigin.CRYSTAL_HOLLOWS, Formatting.AQUA),
	TOPAZ(CrystalOrigin.CRYSTAL_HOLLOWS, Formatting.YELLOW),
	JASPER(CrystalOrigin.MISC, Formatting.LIGHT_PURPLE),
	RUBY(CrystalOrigin.MISC, Formatting.RED), // Misc crystals
	OPAL(CrystalOrigin.CRIMSON_ISLE, Formatting.WHITE), // Crimson isle
	AQUAMARINE(CrystalOrigin.GLACITE_TUNNELS, Formatting.BLUE),
	PERIDOT(CrystalOrigin.GLACITE_TUNNELS, Formatting.DARK_GREEN),
	CITRINE(CrystalOrigin.GLACITE_TUNNELS, Formatting.DARK_RED),
	ONYX(CrystalOrigin.GLACITE_TUNNELS, Formatting.DARK_GRAY); // Tunnels

	public static final Codec<CrystalType> CODEC = StringIdentifiable.createCodec(CrystalType::values);
	private final CrystalOrigin crystalOrigin;
	private final Formatting formatting;

	public static Optional<CrystalType> getCrystalTypeByDisplayName(String displayName) {
		if (displayName == null) {
			return Optional.empty();
		}

		for (CrystalType value : values()) {
			if (value.name().equalsIgnoreCase(displayName)) {
				return Optional.of(value);
			}
		}

		return Optional.empty();
	}

	@Override
	public String asString() {
		return name();
	}
}
