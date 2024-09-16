package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import dev.morazzer.cookies.mod.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to display text.
 */
public class TextDisplayEditor extends ConfigOptionEditor<Text, TextDisplayOption> {

    @SuppressWarnings("MissingJavadoc")
    public TextDisplayEditor(final TextDisplayOption option) {
        super(option);
    }

    @Override
    public void render(final @NotNull DrawContext drawContext,
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
            0xFFFFFFFF
        );
    }

	/**
	 * @implNote Don't call super, we don't want descriptions on text elements.
 	 */
	@Override
	public void renderOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
	}

	@Override
    public int getHeight() {
        return 18;
    }
}
