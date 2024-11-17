package codes.cookies.mod.events.mixins;

import codes.cookies.mod.events.PlayerListEvent;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Calls events based on player list entries.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class PlayerListMixin {

	@Inject(method = "handlePlayerListAction", at = @At("RETURN"))
	public void playerListEvents(
			PlayerListS2CPacket.Action action,
			PlayerListS2CPacket.Entry receivedEntry,
			PlayerListEntry currentEntry,
			CallbackInfo ci
	) {
		PlayerListEvent.EVENT.invoker().accept(currentEntry);
	}

}
