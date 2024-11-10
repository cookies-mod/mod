package codes.cookies.mod.events.mixins;

import codes.cookies.mod.events.api.ScreenKeyEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Invokes the {@link ScreenKeyEvents} in sign screens.
 */
@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen {

    protected AbstractSignEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (ScreenKeyEvents.handle(this, chr, modifiers)) {
            cir.setReturnValue(true);
        }
    }

}
