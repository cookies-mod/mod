package codes.cookies.mod.render.hud.elements;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.render.hud.internal.BoundingBox;
import codes.cookies.mod.render.hud.internal.HudEditAction;
import codes.cookies.mod.render.hud.internal.HudElementSettings;
import codes.cookies.mod.render.hud.settings.BooleanSetting;
import codes.cookies.mod.render.hud.settings.ColorSetting;
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

@Getter
public abstract class HudElement {

	private final Identifier identifier;
	private final HudElementSettings position = new HudElementSettings();
	@Setter
	protected HudEditAction hudEditAction = HudEditAction.NONE;

	public HudElement(Identifier identifier) {
		this.identifier = identifier;
	}

	public abstract void render(DrawContext drawContext, TextRenderer textRenderer, float ticks);

	public void renderBackground(DrawContext drawContext) {
		if (this.position.isBackground()) {
			this.getBoundingBox().fill(drawContext, this.position.getBackgroundColor());
		}
	}

	public abstract boolean shouldRender();

	public final void renderChecks(DrawContext drawContext, TextRenderer textRenderer, float partialTicks) {
		if (!shouldRender()) {
			return;
		}
		render(drawContext, textRenderer, partialTicks);
	}

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract Text getName();

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

		builder.addSetting(new BooleanSetting(Text.literal("test"), Text.literal("Test"), this.position::isBackground, this.position::setBackground));
		builder.addSetting(new ColorSetting(Text.literal("test2"), Text.literal("Test3"), this.position::getColorValue, this.position::setColorValue, true));

		final List<Option<?, ?>> hudSettings = ConfigManager.getConfigReader().getHudSettings(this);
		hudSettings.forEach(builder::addOption);
	}

	public int getX() {
		return this.position.clampX(getWidth());
	}

	public int getY() {
		return this.position.clampY(getHeight());
	}

	public void load(HudElementSettings value) {
		this.position.setScale(value.getScale());
		this.position.setX(value.getRelativeX());
		this.position.setY(value.getRelativeY());
		this.position.setAlignment(value.getAlignment());
		this.position.setBackground(value.isBackground());
		this.position.setBackgroundColor(value.getBackgroundColor());
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
