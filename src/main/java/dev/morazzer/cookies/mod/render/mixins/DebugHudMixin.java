package dev.morazzer.cookies.mod.render.mixins;

import dev.morazzer.cookies.mod.render.WorldRender;
import java.util.List;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds debug information to the debug screen (f3).
 */
@Mixin(DebugHud.class)
public class DebugHudMixin {

    /**
     * Adds text to the left side of the debug screen.
     *
     * @param cir The callback info.
     */
    @Inject(method = "getLeftText", at = @At("RETURN"))
    public void addLeftText(CallbackInfoReturnable<List<String>> cir) {
        final List<String> returnValue = cir.getReturnValue();
        returnValue.add(
            "Cookies Drawables: %s (normal: %sns, outlines: %sns)".formatted(WorldRender.getAmountOfDrawables(),
                WorldRender.getAmountOfDrawables(), WorldRender.getOutlinesNs()));
    }

}
