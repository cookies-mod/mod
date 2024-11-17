package codes.cookies.mod.data.farming.squeakymousemat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entry for the squeaky mousemat overlay.
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SqueakyMousematEntry {

	public static final Codec<SqueakyMousematEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.INT.fieldOf("yaw").forGetter(SqueakyMousematEntry::getYaw),
					Codec.INT.fieldOf("pitch").forGetter(SqueakyMousematEntry::getPitch))
			.apply(instance, SqueakyMousematEntry::new));
	public static final SqueakyMousematEntry EMPTY = new SqueakyMousematEntry(0, 0);

	private int yaw;
	private int pitch;

	public String get() {
		return this.yaw + " / " + this.pitch;
	}

	public void loadFrom(SqueakyMousematEntry other) {
		this.yaw = other.yaw;
		this.pitch = other.pitch;
	}
}
