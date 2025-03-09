package codes.cookies.mod.utils.mixins.spam;

import codes.cookies.mod.config.categories.DevCategory;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.network.ClientPlayNetworkHandler;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Removes spam from the game log.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	/**
	 * Wraps a logger invocation that is called multiple times per minute/second while playing on hypixel, to prevent
     * spam in the console this invocation is cancelled.
	 *
	 * @param instance Logger instance.
	 * @param string   Logged message.
	 * @param o        First formatting object.
	 * @param o2       Second formatting object.
	 * @param original The original call.
	 */

	@WrapOperation(
			method = "onPlayerList",
			at = @At(
					value = "INVOKE",
					target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
					remap = false
			)
	)
	public void onPlayerList(Logger instance, String string, Object o, Object o2, Operation<Void> original) {
		if (!DevCategory.hideConsoleSpam) {
			original.call(instance, string, o, o2);
		}
	}

}
