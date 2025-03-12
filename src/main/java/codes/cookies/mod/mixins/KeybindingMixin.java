package codes.cookies.mod.mixins;

import java.util.HashMap;
import java.util.Map;

import codes.cookies.mod.config.categories.FarmingCategory;
import codes.cookies.mod.data.farming.GardenKeybindsData;
import codes.cookies.mod.utils.accessors.KeyBindingAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Mixin(value = KeyBinding.class)
public abstract class KeybindingMixin implements KeyBindingAccessor {
	@Unique
	private static final Map<InputUtil.Key, KeyBindingAccessor> GARDEN_KEY_TO_BINDINGS = new HashMap<>();
	@Final
	@Shadow
	private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;
	@Unique
	private GardenKeybindsData.GardenKeyBindOverride cookies$gardenKey;

	@Inject(method = "updateKeysByCode", at = @At("TAIL"))
	private static void cookies$updateGardenKeysByCode(CallbackInfo ci) {
		GARDEN_KEY_TO_BINDINGS.clear();
		for (KeyBinding keyBinding : KeyBinding.KEYS_BY_ID.values()) {
			var gardenKey = KeyBindingAccessor.toAccessor(keyBinding).cookies$getGardenKey();
			if (gardenKey != null) {
				GARDEN_KEY_TO_BINDINGS.put(gardenKey.key(), KeyBindingAccessor.toAccessor(keyBinding));
			}
		}
	}

	@Inject(method = "updatePressedStates", at = @At(value = "HEAD"), cancellable = true)
	private static void cookies$updatePressedStates(CallbackInfo ci) {
		if (FarmingCategory.keybindPredicate.getShouldBeEnabled().get()) {
			for (var keyBinding : GARDEN_KEY_TO_BINDINGS.values()) {
				if (keyBinding.cookies$getGardenKey()
						.key()
						.getCategory() == InputUtil.Type.KEYSYM && keyBinding.cookies$getGardenKey()
						.key()
						.getCode() != InputUtil.UNKNOWN_KEY.getCode()) {
					((KeyBinding) keyBinding).setPressed(InputUtil.isKeyPressed(
							MinecraftClient.getInstance()
									.getWindow()
									.getHandle(), keyBinding.cookies$getGardenKey().key().getCode()));
				}
			}
			ci.cancel();
		}
	}

	@Inject(method = "setKeyPressed", at = @At(value = "HEAD"), cancellable = true)
	private static void cookies$setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		if (FarmingCategory.keybindPredicate.getShouldBeEnabled().get()) {
			if (GARDEN_KEY_TO_BINDINGS.get(key) instanceof KeyBindingAccessor accessor) {
				if (accessor.cookies$getGardenKey() != null) {
					((KeyBinding) accessor).setPressed(pressed);
				}
				ci.cancel();
			}
			if (KEY_TO_BINDINGS.get(key) instanceof KeyBindingAccessor accessor && accessor.cookies$getGardenKey() != null) {
				ci.cancel();
			}
		}
	}

	@Inject(method = "onKeyPressed", at = @At(value = "HEAD"), cancellable = true)
	private static void cookies$onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
		if (FarmingCategory.keybindPredicate.getShouldBeEnabled().get()) {
			if (GARDEN_KEY_TO_BINDINGS.get(key) instanceof KeyBindingAccessor accessor) {
				if (accessor.cookies$getGardenKey() != null) {
					((KeyBinding) accessor).timesPressed++;
				}

				ci.cancel();
			}
			if (KEY_TO_BINDINGS.get(key) instanceof KeyBindingAccessor accessor && accessor.cookies$getGardenKey() != null) {
				ci.cancel();
			}
		}
	}

	@Override
	public GardenKeybindsData.GardenKeyBindOverride cookies$getGardenKey() {
		return cookies$gardenKey;
	}

	@Override
	public void cookies$setGardenKey(GardenKeybindsData.GardenKeyBindOverride key) {
		cookies$gardenKey = key;
		if (key != null) {
			GARDEN_KEY_TO_BINDINGS.put(key.key(), this);
		}
	}

	@Inject(method = "matchesKey", at = @At(value = "HEAD"), cancellable = true)
	private void cookies$matchesKey(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir) {
		if (FarmingCategory.keybindPredicate.getShouldBeEnabled().get()) {
			if (this.cookies$getGardenKey() != null) {
				cir.setReturnValue(keyCode == InputUtil.UNKNOWN_KEY.getCode()
						? this.cookies$getGardenKey()
						.key()
						.getCategory() == InputUtil.Type.SCANCODE && this.cookies$getGardenKey()
						.key()
						.getCode() == scanCode
						: this.cookies$getGardenKey()
						.key()
						.getCategory() == InputUtil.Type.KEYSYM && this.cookies$getGardenKey()
						.key()
						.getCode() == keyCode);
			}
		}
	}

	@Inject(method = "matchesMouse", at = @At(value = "HEAD"), cancellable = true)
	public void matchesMouse(int code, CallbackInfoReturnable<Boolean> cir) {
		if (FarmingCategory.keybindPredicate.getShouldBeEnabled().get()) {
			if (this.cookies$getGardenKey() != null) {
				cir.setReturnValue(this.cookies$getGardenKey()
						.key()
						.getCategory() == InputUtil.Type.MOUSE && this.cookies$getGardenKey().key().getCode() == code);
			}
		}
	}
}
