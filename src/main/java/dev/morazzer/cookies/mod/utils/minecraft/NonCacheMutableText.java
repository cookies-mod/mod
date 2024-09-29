package dev.morazzer.cookies.mod.utils.minecraft;

import java.util.Collections;
import java.util.List;

import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Language;

/**
 * Disables the default caching behaviour of the mutable text, this should be used with {@link SupplierTextContent}.
 */
public class NonCacheMutableText extends MutableText {
	public NonCacheMutableText(TextContent textContent, List<Text> siblings, Style style) {
		super(textContent, siblings, style);
	}

	public NonCacheMutableText(MutableText mutableText) {
		this(mutableText.getContent(), mutableText.getSiblings(), mutableText.getStyle());
	}

	public static NonCacheMutableText of(TextContent textContent) {
		return new NonCacheMutableText(textContent, Collections.emptyList(), Style.EMPTY);
	}

	@Override
	public OrderedText asOrderedText() {
		return Language.getInstance().reorder(this);
	}
}
