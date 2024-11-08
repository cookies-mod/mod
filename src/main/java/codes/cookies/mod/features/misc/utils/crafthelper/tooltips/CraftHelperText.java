package codes.cookies.mod.features.misc.utils.crafthelper.tooltips;

import java.util.Objects;

import lombok.Setter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Setter
public final class CraftHelperText implements CraftHelperComponentPart {
	private final Text text;
	private int x;
	private int y;

	public CraftHelperText(int x, int y, Text text) {
		this.x = x;
		this.y = y;
		this.text = text;
	}

	public static CraftHelperText of(String debug) {
		return new CraftHelperText(0, 0, Text.literal(debug));
	}

	public static CraftHelperText of(Text append) {
		return new CraftHelperText(0, 0, append);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	@Override
	public int width() {
		return MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	@Override
	public int height() {
		return MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	public Text text() {
		return text;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (CraftHelperText) obj;
		return this.x == that.x &&
				this.y == that.y &&
				Objects.equals(this.text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, text);
	}

	@Override
	public String toString() {
		return "CraftHelperText[" +
				"x=" + x + ", " +
				"y=" + y + ", " +
				"text=" + text + ']';
	}

}
