package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.CookiesUtils;
import dev.morazzer.cookies.mod.utils.accessors.SlotAccessor;
import dev.morazzer.cookies.mod.utils.dev.DevInventoryUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.SkyblockDataComponentTypes;
import java.nio.file.Path;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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
                    .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                        path.getFileName().toString().split("\\.")[0]))));
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void cancelMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir,
                                  boolean bl, Slot slot) {
        if (slot == null) {
            return;
        }
        if (SlotAccessor.getRunnable(slot) != null) {
            SlotAccessor.getRunnable(slot).run();
            cir.setReturnValue(true);
            return;
        }
        if (SlotAccessor.getItem(slot) != null) {
            cir.setReturnValue(true);
        }
    }

    @ModifyArgs(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void drawItem$drawItemInSlot(Args args) {
        final ItemStack itemStack = args.get(1);
        String text;
        if ((text = ItemUtils.getData(itemStack, SkyblockDataComponentTypes.CUSTOM_SLOT_TEXT)) != null) {
            args.set(4, text);
        }
    }
}
