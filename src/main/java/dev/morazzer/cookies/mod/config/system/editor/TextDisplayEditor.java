package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import dev.morazzer.cookies.mod.utils.RenderUtils;

import net.minecraft.client.MinecraftClient;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.WidgetTooltipPositioner;
import net.minecraft.text.Text;

/**
 * Editor to display text.
 */
public class TextDisplayEditor extends ConfigOptionEditor<Text, TextDisplayOption> {

	@SuppressWarnings("MissingJavadoc")
	public TextDisplayEditor(final TextDisplayOption option) {
		super(option);
	}

	@Override
	public void render(
			final @NotNull DrawContext drawContext,
			final int mouseX,
			final int mouseY,
			final float tickDelta,
			final int optionWidth) {
		RenderUtils.renderFilledBox(drawContext, 0, 0, optionWidth, this.getHeight(optionWidth));
		final int centerX = optionWidth / 2;
		final int centerY = this.getHeight() / 2;

		drawContext.drawCenteredTextWithShadow(
				this.getTextRenderer(),
				this.option.getName(),
				centerX,
				centerY - this.getTextRenderer().fontHeight / 2,
				0xFFFFFFFF);
	}

	@Override
	public void renderOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
		if (this.option.getDescription() == null) {
			return;
		}
		if (mouseX > 2 && mouseX < optionWidth + 2 && mouseY > 0 && mouseY < 16) {
			drawContext.drawTooltip(
					this.getTextRenderer(),
					MinecraftClient.getInstance().textRenderer.wrapLines(this.option.getDescription(), optionWidth * 2),
					new WidgetTooltipPositioner(ScreenRect.empty()),
					mouseX,
					mouseY);
		}
	}

	@Override
	public int getHeight() {
		return 18;
	}
}
