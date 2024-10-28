package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.accessors.PlayerEntityRenderStateAccessor;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements PlayerEntityRenderStateAccessor {

	@Unique
	private boolean cookies$isSelf;

	@Override
	public boolean cookies$isSelf() {
		return cookies$isSelf;
	}

	@Override
	public void cookies$setIsSelf(boolean self) {
		this.cookies$isSelf = self;
	}
}
