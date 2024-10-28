package dev.morazzer.cookies.mod.utils.accessors;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

public interface PlayerEntityRenderStateAccessor {

	static PlayerEntityRenderStateAccessor get(PlayerEntityRenderState state) {
		return (PlayerEntityRenderStateAccessor) state;
	}

	static boolean isSelf(PlayerEntityRenderState state) {
		return get(state).cookies$isSelf();
	}

	static void setSelf(PlayerEntityRenderState state, boolean self) {
		get(state).cookies$setIsSelf(self);
	}

	boolean cookies$isSelf();
	void cookies$setIsSelf(boolean self);

}
