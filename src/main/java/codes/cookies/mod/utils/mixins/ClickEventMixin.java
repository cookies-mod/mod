package codes.cookies.mod.utils.mixins;

import codes.cookies.mod.utils.accessors.ClickEventAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.text.ClickEvent;

@Mixin(ClickEvent.class)
public class ClickEventMixin implements ClickEventAccessor {

	@Unique
	private Runnable cookies$runnable;

	@Override
	public void cookies$setRunnable(Runnable runnable) {
		this.cookies$runnable = runnable;
	}

	@Override
	public Runnable cookies$getRunnable() {
		return this.cookies$runnable;
	}

}
