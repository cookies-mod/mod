package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevInventoryUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import java.nio.file.Path;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for saving of screens/inventories.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Unique
    private static final Identifier ALLOW_SCREEN_SAVING = DevUtils.createIdentifier("save_handled_screens");

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings("MissingJavadoc")
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (DevUtils.isEnabled(ALLOW_SCREEN_SAVING)) {
            if (keyCode != 83) {
                return;
            }
            final Path path = DevInventoryUtils.saveInventory((HandledScreen<? extends ScreenHandler>) (Object) this);
            CookiesUtils.sendMessage(
                CookiesUtils.createPrefix().append("Saved inventory to file %s".formatted(path.getFileName()))
                    .styled(style ->style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, path.getFileName().toString().split("\\.")[0]))));
            cir.setReturnValue(true);
        }
    }
}
