package codes.cookies.mod.render.hud.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

public class LiteralSetting extends HudElementSetting {

	private final Text literal;

	public LiteralSetting(Text literal, HudElementSettingType hudElementSettingType) {
		super(hudElementSettingType);
		this.literal = literal;
	}

	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.cm$drawCenteredText(
				literal,
				x + sidebarWidth / 2 - 5,
				y + sidebarElementHeight / 2 - 4,
				-1,
				false);
	}

	@Override
	public int getWidth() {
		return MinecraftClient.getInstance().textRenderer.getWidth(literal);
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}
}
