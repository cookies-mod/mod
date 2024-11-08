package codes.cookies.mod.render.types;

import codes.cookies.mod.render.Renderable;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Combine multiple different renderables into one.
 *
 * @param renderables The renderables to combine.
 */
public record Composite(Renderable... renderables) implements Renderable {
	@Override
	public void render(WorldRenderContext context) {
		for (Renderable renderable : this.renderables) {
			if (renderable == null) {
				continue;
			}
			if (renderable.shouldRender(context)) {
				renderable.render(context);
			}
		}
	}

	@Override
	public void load() {
		for (Renderable renderable : this.renderables) {
			if (renderable == null) {
				continue;
			}
			renderable.load();
		}
	}

	@Override
	public void remove() {
		for (Renderable renderable : this.renderables) {
			if (renderable == null) {
				continue;
			}
			renderable.remove();
		}
	}

	@Override
	public boolean shouldRemove() {
		for (Renderable renderable : this.renderables) {
			if (renderable == null) {
				continue;
			}
			if (renderable.shouldRemove()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean requiresEntityOutlineShader() {
		for (Renderable renderable : this.renderables) {
			if (renderable == null) {
				continue;
			}
			if (renderable.requiresEntityOutlineShader()) {
				return true;
			}
		}
		return false;
	}
}
