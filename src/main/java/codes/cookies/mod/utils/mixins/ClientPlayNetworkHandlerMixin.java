package codes.cookies.mod.utils.mixins;

import codes.cookies.mod.config.categories.MiscCategory;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import codes.cookies.mod.utils.SkyblockUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowPacketSizeAndPingCharts()Z"))
    public boolean tick(boolean original) {
        return original || MiscCategory.showPing && SkyblockUtils.isCurrentlyInSkyblock();
    }

}
