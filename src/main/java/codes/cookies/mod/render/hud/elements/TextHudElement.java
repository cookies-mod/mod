package codes.cookies.mod.render.hud.elements;

import codes.cookies.mod.render.hud.internal.HudEditAction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class TextHudElement extends HudElement {

	public TextHudElement(Identifier identifier) {
		super(identifier);
	}

	protected abstract Text getText();
	protected abstract Text getEditText();

	@Override
	public void render(DrawContext drawContext, TextRenderer textRenderer, float ticks) {
		drawContext.drawText(textRenderer, this.getDisplayText(), 1,1, -1, false);
	}

	protected Text getDisplayText() {
		if (this.hudEditAction != HudEditAction.NONE) {
			return this.getEditText();
		}

		return this.getText();
	}


	@Override
	public int getWidth() {
		return MinecraftClient.getInstance().textRenderer.getWidth(this.getDisplayText()) + 2;
	}

	@Override
	public int getHeight() {
		return MinecraftClient.getInstance().textRenderer.fontHeight + 2;
	}
}
