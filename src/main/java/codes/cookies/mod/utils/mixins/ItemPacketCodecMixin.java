package codes.cookies.mod.utils.mixins;

import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.item.ItemStack;

/**
 * Prevents replacement items to slip through to hypixel.
 */
@Mixin(targets = "net.minecraft.item.ItemStack$1")
public class ItemPacketCodecMixin {

	@ModifyVariable(
			at = @At("HEAD"),
			method = "encode(Lnet/minecraft/network/RegistryByteBuf;Lnet/minecraft/item/ItemStack;)V",
			argsOnly = true
	)
	public net.minecraft.item.ItemStack encode(ItemStack stack) {
		if (stack.contains(CookiesDataComponentTypes.ORIGINAL_ITEM)) {
			if (stack.get(CookiesDataComponentTypes.ORIGINAL_ITEM) != null) {
				return stack.get(CookiesDataComponentTypes.ORIGINAL_ITEM);
			}
		}
		return stack;
	}

}
