package codes.cookies.mod.render.hud.settings;

import codes.cookies.mod.screen.CookiesScreen;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

/**
 * Generic setting for a hud element.
 */
public abstract class HudElementSetting implements Drawable, Selectable, Element {

	private final HudElementSettingType type;
	@Getter
	@Setter
	protected int x;
	@Getter
	@Setter
	protected int y;
	@Setter
	protected int sidebarWidth;
	@Setter
	protected int sidebarElementHeight;
	private boolean focused;

	public HudElementSetting(HudElementSettingType type) {
		this.type = type;
	}

	public void init() {
	}

	public final HudElementSettingType getSettingType() {
		return this.type;
	}

	public abstract int getHeight();

	public abstract int getWidth();

	int getActualWidth() {
		return getWidth();
	}

	@Override
	public boolean isFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	protected boolean isInBound(int x, int y) {
		return CookiesScreen.isInBound(x, y, this.x, this.y, this.getActualWidth(), this.getHeight());
	}

	protected TextRenderer getTextRenderer() {
		return MinecraftClient.getInstance().textRenderer;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return isInBound((int) mouseX, (int) mouseY);
	}
}
