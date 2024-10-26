package dev.morazzer.cookies.mod.utils.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    /**
	 * Allows for modification of the slot text, just by attaching an item component.
	 *
	 */
    @ModifyVariable(
			method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "HEAD"),
			argsOnly = true
	)
    public String modifyStuff(String value, @Local(argsOnly = true)ItemStack itemStack) {
        final String data = ItemUtils.getData(itemStack, CookiesDataComponentTypes.CUSTOM_SLOT_TEXT);
        if (data != null) {
            return data;
        }
		return value;
	}
}
