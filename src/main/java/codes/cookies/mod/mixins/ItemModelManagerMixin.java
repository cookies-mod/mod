package codes.cookies.mod.mixins;

import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.minecraft.client.item.ItemModelManager;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {

	@ModifyVariable(method = "update", at = @At("HEAD"), argsOnly = true)
	public ItemStack update(ItemStack value) {
		if (value.contains(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM)) {
			return value.get(CookiesDataComponentTypes.OVERRIDE_RENDER_ITEM);
		}

		return value;
	}

}
