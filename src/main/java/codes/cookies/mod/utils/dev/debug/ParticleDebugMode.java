package codes.cookies.mod.utils.dev.debug;

import codes.cookies.mod.events.world.ParticleEmitEvent;
import codes.cookies.mod.render.WorldRender;
import codes.cookies.mod.render.types.Timed;
import codes.cookies.mod.render.types.WorldText;
import codes.cookies.mod.utils.dev.DevUtils;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.TimeUnit;

public interface ParticleDebugMode {
	Identifier DEBUG_MODE = DevUtils.createIdentifier("particle_debug_mode");

	static void register() {
		ParticleEmitEvent.EVENT.register(particle -> {
			if (DevUtils.isDisabled(DEBUG_MODE)) {
				return;
			}

			WorldRender.addRenderable(new Timed(
					new WorldText(
							particle.getBoundingBox().getCenter(),
							Text.literal(particle.getClass().getSimpleName()),
							false), 1,
					TimeUnit.SECONDS));
		});
	}
}
