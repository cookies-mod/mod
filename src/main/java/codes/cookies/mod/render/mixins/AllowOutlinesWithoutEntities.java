package codes.cookies.mod.render.mixins;

import codes.cookies.mod.render.WorldRender;

import net.minecraft.client.render.WorldRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for outline rendering without any active entity.
 */
@Mixin(WorldRenderer.class)
public abstract class AllowOutlinesWithoutEntities {
	@Inject(at = @At(value = "RETURN"), method = "getEntitiesToRender", cancellable = true)
	public void render(CallbackInfoReturnable<Boolean> cir) {
		if (WorldRender.isHasOutlines()) {
			cir.setReturnValue(true);
		}
	}
}
