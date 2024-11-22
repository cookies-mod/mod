package codes.cookies.mod.mixins;

import codes.cookies.mod.data.cookiesdata.CookieDataInstances;
import codes.cookies.mod.data.farming.GardenKeybindsData;
import codes.cookies.mod.utils.accessors.KeyBindingAccessor;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.client.option.KeyBinding;

import net.minecraft.client.util.InputUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = KeyBinding.class, remap = false)
public abstract class KeybindingMixin implements KeyBindingAccessor {
	@Unique
	private GardenKeybindsData.GardenKeyBindOverride cookies$gardenKey;

	@Unique
	private static final Map<String, KeyBindingAccessor> GARDEN_KEYS_BY_ID = new HashMap<>();

	@Unique
	private static final Map<InputUtil.Key, KeyBindingAccessor> GARDEN_KEY_TO_BINDINGS = new HashMap<>();

	@Override
	public GardenKeybindsData.GardenKeyBindOverride cookies$getGardenKey() {
		return cookies$gardenKey;
	}

	@Override
	public void cookies$setGardenKey(GardenKeybindsData.GardenKeyBindOverride key) {
		cookies$gardenKey = key;
		cookies$updateGardenKeysByCode(null);
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void cookies$initGardenKeys(CallbackInfo ci) {
		for (KeyBinding keyBinding : KeyBinding.KEYS_BY_ID.values()) {
			GARDEN_KEYS_BY_ID.put(keyBinding.getTranslationKey(), (KeyBindingAccessor) keyBinding);
		}
	}

	@Inject(method = "updateKeysByCode", at = @At("TAIL"))
	private static void cookies$updateGardenKeysByCode(CallbackInfo ci) {
		GARDEN_KEY_TO_BINDINGS.clear();
		for (KeyBinding keyBinding : KeyBinding.KEYS_BY_ID.values()) {
			GARDEN_KEY_TO_BINDINGS.put(keyBinding.boundKey, KeyBindingAccessor.toAccessor(keyBinding));
		}
	}

	@Inject(method = "setKeyPressed", at = @At(value = "HEAD"), cancellable = true)
	private static void cookies$setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		if(LocationUtils.Island.GARDEN.isActive()) {
			KeyBinding keyBinding = (KeyBinding)GARDEN_KEY_TO_BINDINGS.get(key);
			if (keyBinding != null) {
				keyBinding.setPressed(pressed);
				ci.cancel();
			}
		}
	}

	@Inject(method = "onKeyPressed", at = @At(value = "HEAD"), cancellable = true)
	private static void cookies$onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
		if(LocationUtils.Island.GARDEN.isActive()) {
			KeyBinding keyBinding = (KeyBinding)GARDEN_KEY_TO_BINDINGS.get(key);
			if (keyBinding != null) {
				keyBinding.timesPressed++;
				ci.cancel();
			}
		}
	}
}
