package codes.cookies.mod.events.world;

import codes.cookies.mod.utils.cookies.CookiesEventUtils;

import net.minecraft.client.particle.Particle;

import net.fabricmc.fabric.api.event.Event;

import java.util.function.Consumer;

public interface ParticleEmitEvent {

	Event<Consumer<Particle>> EVENT = CookiesEventUtils.consumer();


}
