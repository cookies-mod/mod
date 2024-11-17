package codes.cookies.mod.render.hud.elements;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.render.hud.internal.BoundingBox;
import codes.cookies.mod.render.hud.internal.HudEditAction;
import codes.cookies.mod.render.hud.internal.HudPosition;
import codes.cookies.mod.render.hud.settings.EnumCycleSetting;
import codes.cookies.mod.render.hud.settings.HudElementSettingBuilder;
import codes.cookies.mod.render.hud.settings.HudElementSettingType;
import codes.cookies.mod.render.hud.settings.LiteralSetting;
import codes.cookies.mod.render.hud.settings.ValueSetting;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * A generic hud element.
 */
@Getter
public abstract class HudElement {

	private final Identifier identifier;
	private final HudPosition position = new HudPosition();
	@Setter
	protected HudEditAction hudEditAction = HudEditAction.NONE;

	public HudElement(Identifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * Renders the hud element.
	 */
	public abstract void render(DrawContext drawContext, TextRenderer textRenderer, float ticks);

	/**
	 * @return Whether the element should be rendered.
	 */
	public abstract boolean shouldRender();

	/**
	 * Renders the element with all checks.
	 */
	public final void renderChecks(DrawContext drawContext, TextRenderer textRenderer, float partialTicks) {
		if (!shouldRender()) {
			return;
		}
		render(drawContext, textRenderer, partialTicks);
	}

	/**
	 * @return The width of the element.
	 */
	public abstract int getWidth();

	/**
	 * @return The height of the element.
	 */
	public abstract int getHeight();

	/**
	 * @return The name of the element.
	 */
	public abstract Text getName();

	/**
	 * Adds all settings that are used by the element.
	 * @param builder The settings builder.
	 */
	@MustBeInvokedByOverriders
	public void buildSettings(HudElementSettingBuilder builder) {
		builder.prependSetting(new EnumCycleSetting<>(
				Text.literal("Alignment"),
				Text.literal(""),
				this.position::getAlignment,
				this.position::setAlignment,
				HudElementSettingType.METADATA));
		builder.prependSetting(new ValueSetting(Text.literal("Scale: %.2f".formatted(this.getScale()))));
		builder.prependSetting(new ValueSetting(Text.literal("Y: " + this.getY())));
		builder.prependSetting(new ValueSetting(Text.literal("X: " + this.getX())));
		builder.prependSetting(new LiteralSetting(getName(), HudElementSettingType.METADATA));

		final List<Option<?, ?>> hudSettings = ConfigManager.getConfigReader().getHudSettings(this);
		hudSettings.forEach(builder::addOption);
	}

	public int getX() {
		return this.position.clampX(getWidth());
	}

	public int getY() {
		return this.position.clampY(getHeight());
	}

	public void load(HudPosition value) {
		this.position.setScale(value.getScale());
		this.position.setX(value.getRelativeX());
		this.position.setY(value.getRelativeY());
		this.position.setAlignment(value.getAlignment());
	}

	public float getScale() {
		return this.position.getScale();
	}

	public BoundingBox getBoundingBox() {
		return new BoundingBox(getX(), getY(), getWidth(), getHeight());
	}

	public BoundingBox getScaledBoundingBox() {
		return getBoundingBox().scale(getScale());
	}

	public BoundingBox getNormalizedBoundingBox() {
		return new BoundingBox(0, 0, getWidth(), getHeight());
	}

}
