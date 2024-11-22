package codes.cookies.mod.mixins;

import codes.cookies.mod.data.farming.GardenKeybindsData;
import codes.cookies.mod.utils.accessors.KeyBindingAccessor;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import net.minecraft.client.util.InputUtil;

import org.spongepowered.asm.mixin.Debug;
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
	private static final Map<InputUtil.Key, KeyBindingAccessor> GARDEN_KEY_TO_BINDINGS = new HashMap<>();

	@Override
	public GardenKeybindsData.GardenKeyBindOverride cookies$getGardenKey() {
		return cookies$gardenKey;
	}

	@Override
	public void cookies$setGardenKey(GardenKeybindsData.GardenKeyBindOverride key) {
		cookies$gardenKey = key;
		if(key != null) {
			GARDEN_KEY_TO_BINDINGS.put(key.key(), this);
		}
	}

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
		if(LocationUtils.Island.GARDEN.isActive()) {
				for (var keyBinding : GARDEN_KEY_TO_BINDINGS.values()) {
					if (keyBinding.cookies$getGardenKey().key().getCategory() == InputUtil.Type.KEYSYM && keyBinding.cookies$getGardenKey().key().getCode() != InputUtil.UNKNOWN_KEY.getCode()) {
						((KeyBinding)keyBinding).setPressed(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.cookies$getGardenKey().key().getCode()));
					}
				}
			ci.cancel();
		}
	}

	@Inject(method = "setKeyPressed", at = @At(value = "HEAD"), cancellable = true)
	private static void cookies$setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		if(LocationUtils.Island.GARDEN.isActive()) {
			KeyBinding keyBinding = (KeyBinding)GARDEN_KEY_TO_BINDINGS.get(key);
			if (keyBinding != null) {
				LogUtils.getLogger().error("Garden key set pressed: {} to {}", keyBinding.boundKey, pressed);
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
				LogUtils.getLogger().error("Garden key timesPressed: {}", keyBinding.boundKey);
				keyBinding.timesPressed++;
				ci.cancel();
			}
		}
	}
}
