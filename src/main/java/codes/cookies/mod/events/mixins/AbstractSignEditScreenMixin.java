package codes.cookies.mod.events.mixins;

import codes.cookies.mod.config.categories.MiscCategory;
import codes.cookies.mod.events.api.ScreenKeyEvents;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Invokes the {@link ScreenKeyEvents} in sign screens.
 */
@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen {

	@Shadow
	protected abstract void finishEditing();

	protected AbstractSignEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (ScreenKeyEvents.handle(this, chr, modifiers)) {
            cir.setReturnValue(true);
        }
    }

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (MiscCategory.signEditEnterSubmits && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && !Screen.hasShiftDown()) {
			this.finishEditing();
			cir.setReturnValue(true);
		}
	}
}
