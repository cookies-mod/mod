package dev.morazzer.cookies.mod.utils.minecraft;

import java.util.Optional;

import java.util.function.Supplier;

import net.minecraft.text.PlainTextContent;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

/**
 * Text content that works with a string supplier for text that updates.
 * @param contentSupplier The supplier.
 */
public record SupplierTextContent(Supplier<String> contentSupplier) implements PlainTextContent {

	@Override
	public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		return visitor.accept(this.contentSupplier.get());
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return visitor.accept(style, this.contentSupplier.get());
	}

	@Override
	public String toString() {
		return this.string();
	}

	@Override
	public String string() {
		return "supplier{content='%s'}".formatted(this.contentSupplier.get());
	}
}
