package codes.cookies.mod.events.mixins;

import codes.cookies.mod.events.world.ParticleEmitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.particle.Particle;

@Mixin(net.minecraft.client.particle.ParticleManager.class)
public class ParticleManager {

	@Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At(value = "INVOKE", target = "Ljava/util/Queue;add(Ljava/lang/Object;)Z"))
	public void test(Particle particle, CallbackInfo ci) {
		ParticleEmitEvent.EVENT.invoker().accept(particle);
	}

}
