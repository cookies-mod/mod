package codes.cookies.mod.render.hud.settings;

import java.util.function.Consumer;
import java.util.function.Supplier;

import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.screen.CookiesScreen;
import codes.cookies.mod.utils.minecraft.SoundUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class BooleanSetting extends HudElementSetting {
	private static final ItemStack ACTIVATED = new ItemStack(Items.GREEN_WOOL);
	private static final ItemStack DEACTIVATED = new ItemStack(Items.RED_WOOL);

	private final Text name;
	private final Text description;
	private final Supplier<Boolean> getter;
	private final Consumer<Boolean> setter;

	public BooleanSetting(
			Text name,
			Text description,
			Supplier<Boolean> getter,
			Consumer<Boolean> setter,
			HudElementSettingType type
	) {
		super(type);
		this.name = name;
		this.description = description;
		this.getter = getter;
		this.setter = setter;
	}

	public BooleanSetting(Text name, Text description, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		this(name, description, getter, setter, HudElementSettingType.CUSTOM);
	}

	public BooleanSetting(BooleanOption booleanOption) {
		this(booleanOption.getName(), booleanOption.getDescription(), booleanOption::getValue, booleanOption::setValue);
	}

	@Override
	public int getHeight() {
		return 18;
	}

	@Override
	public int getWidth() {
		return MinecraftClient.getInstance().textRenderer.getWidth(name) + 25;
	}

	@Override
	int getActualWidth() {
		return super.sidebarWidth;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		int textY = this.y + sidebarElementHeight / 2 - this.getTextRenderer().fontHeight / 2;
		context.drawText(
				this.getTextRenderer(),
				name,
				this.x,
				textY,
				0xFFFFFFFF,
				false);

		context.drawItemWithoutEntity(
				getter.get() ? ACTIVATED : DEACTIVATED,
				this.x + sidebarWidth - 20,
				this.y + (sidebarElementHeight / 2) - 8);

		if (CookiesScreen.isInBound(
				mouseX,
				mouseY,
				this.x,
				textY,
				this.getTextRenderer().getWidth(this.name),
				this.getTextRenderer().fontHeight)) {
			context.drawTooltip(
					getTextRenderer(),
					getTextRenderer().wrapLines(this.description, 300),
					HoveredTooltipPositioner.INSTANCE,
					mouseX,
					mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!super.mouseClicked(mouseX, mouseY, button)) {
			return false;
		}

		if (CookiesScreen.isInBound(
				(int) mouseX,
				(int) mouseY,
				this.x + sidebarWidth - 20,
				this.y + this.getHeight() - 17,
				18,
				18)) {
			this.setter.accept(!this.getter.get());
			SoundUtils.playSound(SoundEvents.BLOCK_LEVER_CLICK, 10, 1);
		}

		return true;
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}
}
