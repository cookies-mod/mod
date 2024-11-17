package codes.cookies.mod.render.hud.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

/**
 * A "value" setting for a hud element, used to display location and scale.
 */
public class ValueSetting extends HudElementSetting {

	private final Text literal;

	public ValueSetting(Text label) {
		this(label, HudElementSettingType.METADATA);
	}

	public ValueSetting(Text label, HudElementSettingType type) {
		super(type);
		this.literal = label;
	}


	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public int getWidth() {
		return MinecraftClient.getInstance().textRenderer.getWidth(literal);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawText(
				MinecraftClient.getInstance().textRenderer,
				literal,
				x,
				y + this.sidebarElementHeight / 2 - 4,
				-1,
				false);
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}
}
