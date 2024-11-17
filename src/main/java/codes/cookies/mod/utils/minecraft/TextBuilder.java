package codes.cookies.mod.utils.minecraft;

import java.util.List;

import codes.cookies.mod.utils.accessors.ClickEventAccessor;

import codes.cookies.mod.utils.accessors.HoverEventAccessor;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Builder for (custom) text.
 */
public class TextBuilder {

	private final MutableText text;
	private Style style;

	/**
	 * Creates a new builder based on the string.
	 * @param text The string.
	 */
	public TextBuilder(String text) {
		this(Text.literal(text));
	}

	/**
	 * Creates a new builder base on the text.
	 * @param text The text.
	 */
	public TextBuilder(Text text) {
		this.text = text.copy();
		this.style = text.getStyle();
	}

	/**
	 * Sets the runnable as click event for the text.
	 * @param runnable The runnable to use.
	 * @return The builder.
	 */
	public TextBuilder setRunnable(Runnable runnable) {
		final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ":C");
		ClickEventAccessor.setRunnable(clickEvent, runnable);
		this.style = this.style.withClickEvent(clickEvent);
		return this;
	}

	/**
	 * Appends text to the builder.
	 * @param text The text to append.
	 * @return The builder.
	 */
	public TextBuilder append(Text text) {
		this.text.append(text);
		return this;
	}

	/**
	 * Appends a string to the builder.
	 * @param text The string to append.
	 * @return The builder.
	 */
	public TextBuilder append(String text) {
		this.text.append(text);
		return this;
	}

	/**
	 * Sets a list of text as hover event.
	 * @param event The list.
	 * @return The builder.
	 */
	public TextBuilder onHover(List<Text> event) {
		final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(":c"));
		HoverEventAccessor.setText(hoverEvent, event);
		this.style = this.style.withHoverEvent(hoverEvent);
		return this;
	}

	/**
	 * Adds a text hover event to the builder.
	 * @param event The text.
	 * @return The builder.
	 */
	public TextBuilder onHover(Text event) {
		this.style = this.style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, event));
		return this;
	}

	/**
	 * Formats the builder with the formatting.
	 * @param formatting The formatting to use.
	 * @return The builder.
	 */
	public TextBuilder formatted(Formatting... formatting) {
		this.style = this.style.withFormatting(formatting);
		return this;
	}

	/**
	 * Builds the builder to a mutable text.
	 * @return The text.
	 */
	public MutableText build() {
		this.text.setStyle(this.style);
		return text;
	}

}
