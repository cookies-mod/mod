package dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips;

import lombok.Getter;
import lombok.Setter;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
public final class TooltipFieldPart implements CraftHelperComponentPart {
	private int x;
	private int y;
	private int width;
	private int height;
	@Getter
	private List<Text> hoverText = new ArrayList<>();

	public TooltipFieldPart(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (TooltipFieldPart) obj;
		return this.x == that.x &&
				this.y == that.y &&
				this.width == that.width &&
				this.height == that.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, width, height);
	}

	@Override
	public String toString() {
		return "TooltipFieldPart[" +
				"x=" + x + ", " +
				"y=" + y + ", " +
				"width=" + width + ", " +
				"height=" + height + ']';
	}

}
