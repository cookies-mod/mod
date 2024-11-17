package codes.cookies.mod.utils.injections;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * Custom methods for the draw context, mainly just for convenience.
 */
public interface DrawContextInjections {

	/**
	 * Pushes and pops the matrix just before and right after consumer execution.
	 * @param consumer The consumer to execute.
	 */
	default void cm$withMatrix(Consumer<MatrixStack> consumer) {
		// implemented in mixin
	}

	/**
	 * Renders a centered text at the provided location.
	 * @param text The text to render.
	 * @param centerX The center position of the text.
	 * @param y The y position.
	 * @param color The color to render the text in.
	 * @param shadow Whether the text has a shadow or not.
	 */
	default void cm$drawCenteredText(Text text, int centerX, int y, int color, boolean shadow) {
		// implemented in mixin
	}
}
