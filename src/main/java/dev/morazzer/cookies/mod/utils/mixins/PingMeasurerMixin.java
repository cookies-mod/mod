package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import net.minecraft.client.network.PingMeasurer;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PingMeasurer.class)
public class PingMeasurerMixin {

    @Inject(method = "onPingResult", at = @At(value = "HEAD"))
    public void onPingResult(PingResultS2CPacket packet, CallbackInfo ci) {
        SkyblockUtils.setLastPing(Util.getMeasuringTimeMs() - packet.startTime());
    }

}
