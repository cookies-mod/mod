package dev.morazzer.cookies.mod.utils.accessors;

import java.util.List;
import java.util.Optional;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public interface HoverEventAccessor {

	static HoverEventAccessor get(HoverEvent clickEvent) {
		return (HoverEventAccessor) clickEvent;
	}

	static void setText(HoverEvent hoverEvent, List<Text> text) {
		get(hoverEvent).cookies$setText(text);
	}

	static Optional<List<Text>> getText(HoverEvent hoverEvent) {
		return Optional.ofNullable(get(hoverEvent).cookies$getText());
	}

	void cookies$setText(List<Text> text);

	List<Text> cookies$getText();


}
