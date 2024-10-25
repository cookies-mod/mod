package dev.morazzer.cookies.mod.utils.compatibility.legendarytooltips;

import dev.morazzer.cookies.mod.utils.items.AbsoluteTooltipPositioner;

import java.util.Collections;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class LegendaryTooltipsImpl implements LegendaryTooltips {

	Slot focused = null;

	@Override
	public void beforeTooltipRender(Screen screen, DrawContext drawContext) {
		if (screen instanceof HandledScreen<?> handledScreen) {
			focused = handledScreen.focusedSlot;
			if (focused != null && focused.getStack() != null) {
				if (focused.getStack().getTooltip(Item.TooltipContext.DEFAULT, MinecraftClient.getInstance().player, TooltipType.BASIC).isEmpty()) {
					drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer, Collections.singletonList(Text.empty().asOrderedText()), AbsoluteTooltipPositioner.INSTANCE, -1, -10);
				}
			}
			handledScreen.focusedSlot = null;
		} else {
			focused = null;
		}
	}

	@Override
	public void afterTooltipRender(Screen screen) {
		if (screen instanceof HandledScreen<?> handledScreen) {
			handledScreen.focusedSlot = focused;
		}
	}
}
