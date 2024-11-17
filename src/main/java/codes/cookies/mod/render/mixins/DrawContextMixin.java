package codes.cookies.mod.render.mixins;

import codes.cookies.mod.utils.injections.DrawContextInjections;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

/**
 * Some utility for the draw context.
 */
@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements DrawContextInjections {
	@Shadow
	public abstract MatrixStack getMatrices();

	@Shadow
	public abstract int drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow);

	@Override
	@Unique
	public void cm$withMatrix(Consumer<MatrixStack> consumer) {
		this.getMatrices().push();
		consumer.accept(this.getMatrices());
		this.getMatrices().pop();
	}

	@Override
	@Unique
	public void cm$drawCenteredText(Text text, int centerX, int y, int color, boolean shadow) {
		final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		this.drawText(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color, shadow);
	}
}
