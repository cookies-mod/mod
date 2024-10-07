package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;

import java.util.concurrent.TimeUnit;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Wraps a renderable that will be removed after a certain time
 * @param renderable The renderable.
 * @param timeToRemoved The time when it should be removed.
 */
public record Timed(Renderable renderable, long timeToRemoved) implements Renderable {

	public Timed(Renderable renderable, int time, TimeUnit timeUnit) {
		this(renderable, System.currentTimeMillis() + timeUnit.toMillis(time));
	}


	@Override
	public void render(WorldRenderContext context) {
		this.renderable.render(context);
	}

	@Override
	public boolean shouldRender(WorldRenderContext context) {
		return this.renderable.shouldRender(context) && Renderable.super.shouldRender(context);
	}

	@Override
	public boolean shouldRemove() {
		if (this.renderable.shouldRemove()) {
			return true;
		}
		return System.currentTimeMillis() > this.timeToRemoved;
	}

	@Override
	public boolean requiresEntityOutlineShader() {
		return this.renderable.requiresEntityOutlineShader();
	}

	@Override
	public void remove() {
		this.renderable.remove();
	}

	@Override
	public void load() {
		this.renderable.load();
	}
}
