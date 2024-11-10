package codes.cookies.mod.features.misc.utils.crafthelper.tooltips;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public record SpacerComponent(int height, int width) implements TooltipComponent {

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return width;
	}
}
