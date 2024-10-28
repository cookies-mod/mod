package dev.morazzer.cookies.mod.utils.compatibility.legendarytooltips;

import dev.morazzer.cookies.mod.utils.compatibility.CompatibilityService;
import dev.morazzer.cookies.mod.utils.compatibility.system.CompatabilityMethod;
import dev.morazzer.cookies.mod.utils.compatibility.system.Requires;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

@Requires("legendarytooltips")
public interface LegendaryTooltips {

	static LegendaryTooltips getInstance() {
		return CompatibilityService.get(LegendaryTooltips.class);
	}

	@CompatabilityMethod
	void beforeTooltipRender(Screen handledScreen, DrawContext drawContext);
	@CompatabilityMethod
	void afterTooltipRender(Screen handledScreen);

}
