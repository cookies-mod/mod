package codes.cookies.mod.utils;

import org.jetbrains.annotations.NotNull;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

/**
 * Various constants and methods related to colors.
 */
public class ColorUtils {

	/**
	 * Gets the color between the specified colors.
	 *
	 * @param first  The first color
	 * @param second The second color
	 * @return The middle.
	 */
	public static int calculateBetween(int first, int second) {
		return calculateBetween(first, second, 0.5);
	}

	/**
	 * Gets the color between the specified colors.
	 *
	 * @param first      The first color.
	 * @param second     The second color.
	 * @param percentage The percentage.
	 * @return The color corresponding to the percentage.
	 */
	public static int calculateBetween(int first, int second, double percentage) {
		if (first == second) {
			return first;
		}

		int alphaFirst = (first >> 24) & 0xFF;
		int redFirst = (first >> 16) & 0xFF;
		int greenFirst = (first >> 8) & 0xFF;
		int blueFirst = (first) & 0xFF;

		int alphaDifference = ((second >> 24) & 0xFF) - alphaFirst;
		int redDifference = ((second >> 16) & 0xFF) - redFirst;
		int greenDifference = ((second >> 8) & 0xFF) - greenFirst;
		int blueDifference = ((second) & 0xFF) - blueFirst;

		double alpha = alphaDifference * percentage;
		double red = redDifference * percentage;
		double green = greenDifference * percentage;
		double blue = blueDifference * percentage;

		int color = 0;
		color |= ((alphaFirst + (int) alpha) & 0xFF) << 24;
		color |= ((redFirst + (int) red) & 0xFF) << 16;
		color |= ((greenFirst + (int) green) & 0xFF) << 8;
		color |= ((blueFirst + (int) blue) & 0xFF);

		return color;
	}

	/**
	 * Gets a text with an applied gradient.
	 *
	 * @param text       The text.
	 * @param startColor The start color of the gradient.
	 * @param endColor   The end color of the gradient.
	 * @return The text with an applied gradient.
	 */
	public static MutableText literalWithGradient(@NotNull String text, int startColor, int endColor) {
		if (startColor == endColor) {
			return Text.literal(text).setStyle(Style.EMPTY.withColor(startColor));
		}

		int redStart = (startColor >> 16) & 0xFF;
		int greenStart = (startColor >> 8) & 0xFF;
		int blueStart = (startColor) & 0xFF;

		int redDifference = ((endColor >> 16) & 0xFF) - redStart;
		int greenDifference = ((endColor >> 8) & 0xFF) - greenStart;
		int blueDifference = ((endColor) & 0xFF) - blueStart;

		int length = text.length();

		int red = redDifference / length;
		int green = greenDifference / length;
		int blue = blueDifference / length;

		MutableText gradient = Text.empty().setStyle(Style.EMPTY.withColor(endColor));

		for (int i = 0; i < text.length(); i++) {
			int color = 0;
			color |= ((redStart + red * i) & 0xFF) << 16;
			color |= ((greenStart + green * i) & 0xFF) << 8;
			color |= ((blueStart + blue * i) & 0xFF);

			MutableText mutableText = Text
					.literal(String.valueOf(text.charAt(i)))
					.setStyle(Style.EMPTY.withColor(color));

			gradient.append(mutableText);
		}

		return gradient;
	}

	/**
	 *
	 * Returns a text with a gradient that is going through all the provided colors.
	 */
	public static MutableText literalWithGradient(String prefix, int... values) {
		int amountPerValue = (int) Math.ceil(prefix.length() / ((float)values.length));

		MutableText literal = Text.empty();
		for (int i = 0; i < values.length - 1; i++) {
			literal.append(literalWithGradient(prefix.substring(
					amountPerValue * i,
					Math.min(amountPerValue * (i + 1), prefix.length())), values[i], values[i + 1]));
		}
		int length = (values.length - 1);
		literal.append(literalWithGradient(prefix.substring(
				Math.min(amountPerValue * length, prefix.length()),
				Math.min(amountPerValue * (length + 1), prefix.length())), values[length - 1], values[length]));
		literal.withColor(values[length]);
		return literal;
	}
}
