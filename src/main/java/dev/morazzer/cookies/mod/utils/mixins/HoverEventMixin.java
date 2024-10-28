package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.accessors.HoverEventAccessor;

import net.minecraft.text.HoverEvent;

import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(HoverEvent.class)
public class HoverEventMixin implements HoverEventAccessor {

	@Unique
	private List<Text> cookies$text;

	@Override
	public void cookies$setText(List<Text> text) {
		this.cookies$text = text;
	}

	@Override
	public List<Text> cookies$getText() {
		return this.cookies$text;
	}
}
