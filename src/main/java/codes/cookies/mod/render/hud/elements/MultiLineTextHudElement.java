package codes.cookies.mod.render.hud.elements;

import java.util.List;

import codes.cookies.mod.render.hud.internal.HudEditAction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class MultiLineTextHudElement extends HudElement {
	protected int lastWidth;
	protected int lastHeight;
	public MultiLineTextHudElement(Identifier identifier) {
		super(identifier);
	}

	@Override
	public void render(DrawContext drawContext, TextRenderer textRenderer, float ticks) {
		this.renderBackground(drawContext);
		int yOffset = 0;
		lastWidth = 0;
		lastHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
		final List<Text> lines = getText();
		if (lines.isEmpty()) {
			if (hudEditAction != HudEditAction.NONE) {
				renderSingleText(drawContext, textRenderer, getName(), yOffset);
				lastWidth += 10;
			}

			return;
		}
		for (Text text : lines) {
			renderSingleText(drawContext, textRenderer, text, yOffset);
			yOffset += 10;
			lastWidth += 10;
		}
	}

	protected void renderSingleText(DrawContext drawContext, TextRenderer textRenderer, Text text, int y) {
		drawContext.drawText(textRenderer, text, 0, y, -1, true);
		lastWidth = Math.max(lastWidth, textRenderer.getWidth(text));
	}

	@Override
	public int getHeight() {
		if (hudEditAction == HudEditAction.NONE) {
			return lastHeight;
		}

		return getMaxRows() * 10;
	}

	protected abstract List<Text> getText();

	abstract public int getMaxRows();

}
