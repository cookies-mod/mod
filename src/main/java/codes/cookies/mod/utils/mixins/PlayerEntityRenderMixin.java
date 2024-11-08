package codes.cookies.mod.utils.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import codes.cookies.mod.utils.accessors.PlayerEntityRenderStateAccessor;

import net.minecraft.client.network.ClientPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRenderMixin {

	@Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("RETURN"))
	public void updateRenderState(
			CallbackInfo ci,
			@Local(argsOnly = true) AbstractClientPlayerEntity clientPlayer,
			@Local(argsOnly = true) PlayerEntityRenderState playerEntityRenderState
	) {
		PlayerEntityRenderStateAccessor.setSelf(playerEntityRenderState, clientPlayer instanceof ClientPlayerEntity);
	}

}
