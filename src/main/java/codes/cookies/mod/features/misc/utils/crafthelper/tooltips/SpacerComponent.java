package codes.cookies.mod.features.misc.utils.crafthelper.tooltips;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;

/**
 * A spacer tooltip component.
 * @param height The height.
 * @param width The width.
 */
public record SpacerComponent(int height, int width) implements TooltipComponent {
	@Override
	public int getHeight(TextRenderer textRenderer) {
		return height;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return width;
	}
}
