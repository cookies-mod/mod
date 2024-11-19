package codes.cookies.mod.render.hud.elements;

import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class MultiLineTextHudElement extends HudElement {
	protected int lastWidth;
	public MultiLineTextHudElement(Identifier identifier) {
		super(identifier);
	}

	@Override
	public void render(DrawContext drawContext, TextRenderer textRenderer, float ticks) {
		this.renderBackground(drawContext);
		int yOffset = 0;
		lastWidth = 0;
		for (Text text : getText()) {
			renderSingleText(drawContext, textRenderer, text, yOffset);
			yOffset += 10;
		}
	}

	protected void renderSingleText(DrawContext drawContext, TextRenderer textRenderer, Text text, int y) {
		drawContext.drawText(textRenderer, text, 0, y, -1, true);
		lastWidth = Math.max(lastWidth, textRenderer.getWidth(text));
	}

	@Override
	public int getHeight() {
		return getMaxRows() * 10;
	}

	protected abstract List<Text> getText();

	abstract public int getMaxRows();

}
