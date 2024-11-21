package codes.cookies.mod.mixins;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.categories.FarmingConfig;
import codes.cookies.mod.data.cookiesdata.CookieDataInstances;
import codes.cookies.mod.data.cookiesdata.CookieDataManager;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.option.KeyBinding;

import net.minecraft.client.util.InputUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyBinding.class, priority = 99999)
public abstract class KeybindingMixin {
	@ModifyVariable(method = "onKeyPressed", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), argsOnly = true)
	private InputUtil.Key onKeyPressedFixed(InputUtil.Key value) {
		if(!LocationUtils.Island.GARDEN.isActive())
		{
			return value;
		}
		var overridingKey = CookieDataInstances.gardenKeybindsData.get.get(value);

		return value;
	}

	@Inject(method = "setKeyPressed", at = @At(value = "TAIL"))
	private static void setKeyPressedFixed(InputUtil.Key key, boolean pressed, CallbackInfo ci, @Local KeyBinding original) {

	}

}
