package codes.cookies.mod.utils.injections;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public interface DrawContextInjections {

	default void cm$withMatrix(Consumer<MatrixStack> consumer) {
		// implemented in mixin
	}

	default void cm$drawCenteredText(Text text, int centerX, int y, int color, boolean shadow) {
		// implemented in mixin
	}
}
