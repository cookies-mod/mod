package dev.morazzer.cookies.mod.render.types;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.morazzer.cookies.mod.render.Renderable;

import dev.morazzer.cookies.mod.utils.dev.DevUtils;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public record CallbackRemovable(Renderable renderable, AtomicBoolean reference) implements Renderable {

	public CallbackRemovable(Renderable renderable, CompletableFuture<Boolean> completableFuture) {
		this(renderable, new AtomicBoolean(false));
		completableFuture.whenComplete((aBoolean, throwable) -> this.reference.set(true));
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
		if (reference.get()) {
			DevUtils.log("re", "Remove renderable :3");
		}
		return reference.get();
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
