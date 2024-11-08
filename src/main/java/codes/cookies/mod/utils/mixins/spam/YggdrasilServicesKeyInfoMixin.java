package codes.cookies.mod.utils.mixins.spam;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import codes.cookies.mod.config.ConfigKeys;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Hide spam from log.
 */
@Mixin(value = YggdrasilServicesKeyInfo.class, remap = false)
public class YggdrasilServicesKeyInfoMixin {

    @WrapOperation(method = "validateProperty", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 1))
    private void validateProperty(Logger instance, String s, Object o, Object o2, Operation<Void> original) {
        if (!ConfigKeys.DEV_HIDE_SPAM.get()) {
            original.call(instance, s, o, o2);
        }
    }

}
