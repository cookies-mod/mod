package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.FoldableOption;
import lombok.Getter;

import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.WidgetTooltipPositioner;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;

/**
 * Editor that describes a foldable.
 */
@Getter
public class FoldableEditor extends ConfigOptionEditor<Object, FoldableOption> {

	private final int foldableId;
	boolean active = true;

	@SuppressWarnings("MissingJavadoc")
	public FoldableEditor(FoldableOption option, int id) {
		super(option);
		this.foldableId = id;
	}

	@Override
	public void init() {
		this.active = false;
	}

	@Override
	public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
		drawContext.drawGuiTexture(
				RenderLayer::getGuiTextured, Identifier.ofVanilla("widget/button"),
				0,
				0,
				optionWidth,
				this.getHeight(optionWidth) - 2);
		MutableText prefix;
		if (this.isActive()) {
			prefix = Text.literal("▼ ");
		} else {
			prefix = Text.literal("▶ ");
		}
		Text renderedText = prefix.append(this.option.getName());

		drawContext.drawCenteredTextWithShadow(this.getTextRenderer(),
				renderedText,
				optionWidth / 2,
				(this.getHeight(optionWidth) - 2) / 2 - this.getTextRenderer().fontHeight / 2,
				0xFFFFFFFF);

	}

	@Override
	public void renderOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
		if (mouseX > 0 && mouseX < optionWidth
				&& mouseY > 0 && mouseY < getHeight()) {
			drawContext.drawTooltip(this.getTextRenderer(), this.option.getDescriptionOrdered(),
					new WidgetTooltipPositioner(
							ScreenRect.empty()), mouseX, mouseY);
		}
	}

	@Override
	public int getHeight() {
		return 20;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
		if (mouseX >= 0 && mouseX < optionWidth && mouseY >= 0 && mouseY < this.getHeight()) {
			this.active = !this.active;
		}
		return super.mouseClicked(mouseX, mouseY, button, optionWidth);
	}

}
