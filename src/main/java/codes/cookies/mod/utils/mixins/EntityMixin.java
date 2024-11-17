package codes.cookies.mod.utils.mixins;

import codes.cookies.mod.utils.accessors.GlowingEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Implements custom glowing.
 */
@Mixin(Entity.class)
public class EntityMixin implements GlowingEntityAccessor {

	@Unique
	private boolean cookies$glowing;
	@Unique
	private Integer cookies$glowingColor;

	@Override
	public void cookies$setGlowing(boolean glowing) {
		this.cookies$glowing = glowing;
	}

	@Override
	public void cookies$setGlowColor(Integer color) {
		this.cookies$glowingColor = color;
	}

	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
		if (this.cookies$glowing) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
	public void getTeamColor(CallbackInfoReturnable<Integer> cir) {
		if (this.cookies$glowingColor != null) {
			cir.setReturnValue(this.cookies$glowingColor);
		}
	}
}
