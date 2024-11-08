package codes.cookies.mod.utils.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.utils.SkyblockUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowPacketSizeAndPingCharts()Z"))
    public boolean tick(boolean original) {
        return original || ConfigManager.getConfig().miscConfig.showPing.getValue() && SkyblockUtils.isCurrentlyInSkyblock();
    }

}
