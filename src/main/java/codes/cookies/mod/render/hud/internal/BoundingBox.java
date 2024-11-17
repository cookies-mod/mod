package codes.cookies.mod.render.hud.internal;

import codes.cookies.mod.screen.CookiesScreen;

import net.minecraft.client.gui.DrawContext;

/**
 * A bounding box for the hud system, only really used as utility class.
 */
public record BoundingBox(float x, float y, float width, float height) {

	public BoundingBox scale(float scaleFactor) {
		return new BoundingBox(this.x, this.y, (int) (this.width * scaleFactor), (int) (this.height * scaleFactor));
	}

	public BoundingBox expand(float expand) {
		return new BoundingBox(this.x - expand, this.y - expand, this.width + expand * 2, this.height + expand * 2);
	}

	public boolean isPointInsideBox(int x, int y) {
		return CookiesScreen.isInBound(x, y, (int) this.x, (int) this.y, (int) this.width, (int) this.height);
	}

	public void fill(DrawContext drawContext, int color) {
		drawContext.fill((int) this.x, (int) this.y, (int) (this.x + this.width), (int) (this.y + this.height), color);
	}

}
