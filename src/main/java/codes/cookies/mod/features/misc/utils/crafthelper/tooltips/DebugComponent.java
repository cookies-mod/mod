package codes.cookies.mod.features.misc.utils.crafthelper.tooltips;

import java.util.List;

import codes.cookies.mod.utils.dev.DevUtils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class DebugComponent extends CraftHelperComponent {

	private final CraftHelperText debug;

	public DebugComponent(String debug) {
		this.debug = CraftHelperText.of(debug);
	}

	@Override
	public boolean isHidden() {
		return !isDebugEnabled();
	}

	@Override
	public List<CraftHelperComponentPart> getTextParts() {
		return List.of();
	}

	@Override
	public boolean isHiddenOrCollapsed() {
		return !isDebugEnabled();
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		context.drawTextWithShadow(textRenderer, debug.text(), x, y, -1);
	}

	public boolean isDebugEnabled() {
		return DevUtils.isEnabled(DEBUG);
	}

	@Override
	public int getHeight() {
		return isDebugEnabled() ? 8 : 0;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return isDebugEnabled() ? textRenderer.getWidth(debug.text()) : 0;
	}
}
