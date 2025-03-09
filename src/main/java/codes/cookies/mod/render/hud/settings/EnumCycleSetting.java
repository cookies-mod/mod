package codes.cookies.mod.render.hud.settings;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import codes.cookies.mod.screen.CookiesScreen;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EnumCycleSetting<T extends Enum<T>> extends HudElementSetting {

	private final Text name;
	private final Text description;
	private final Supplier<T> getter;
	private final Consumer<T> setter;

	private ButtonWidget buttonWidget;
	private int maxButtonWidth;
	private int amount = 0;
	private Text text;

	@Setter
	private Function<T, Text> textSupplier = t -> Text.literal(StringUtils.capitalize(t.name()
			.replace("_", " ")
			.toLowerCase()));

	public EnumCycleSetting(Text name, Text description, Supplier<T> getter, Consumer<T> setter) {
		this(name, description, getter, setter, HudElementSettingType.CUSTOM);
	}

	public EnumCycleSetting(
			Text name,
			Text description,
			Supplier<T> getter,
			Consumer<T> setter,
			HudElementSettingType type
	) {
		super(type);
		this.name = name;
		this.description = description;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void init() {
		this.buttonWidget = new ButtonWidget.Builder(Text.empty(), this::onClick).build();
		this.amount = getter.get().getDeclaringClass().getEnumConstants().length;
		this.text = this.getEnumText();

		this.maxButtonWidth = Arrays.stream(getter.get().getDeclaringClass().getEnumConstants())
				.map(this.textSupplier)
				.mapToInt(getTextRenderer()::getWidth)
				.max()
				.orElse(0);

		this.buttonWidget.setHeight(14);
		this.buttonWidget.setWidth(this.maxButtonWidth + 6);
	}

	private Text getEnumText() {
		return this.textSupplier.apply(this.getter.get());
	}

	private void onClick(ButtonWidget buttonWidget) {
		int index = (getter.get().ordinal() + 1) % this.amount;
		setter.accept(getter.get().getDeclaringClass().getEnumConstants()[index]);
		this.text = this.getEnumText();
	}

	@Override
	public int getHeight() {
		return 18;
	}

	@Override
	public int getWidth() {
		return getTextRenderer().getWidth(this.name) + 26 + this.maxButtonWidth;
	}

	@Override
	int getActualWidth() {
		return super.sidebarWidth;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		int textY = this.y + this.sidebarElementHeight/ 2 - this.getTextRenderer().fontHeight / 2;
		context.drawText(this.getTextRenderer(), this.name, this.x,
				textY, 0xFFFFFFFF, false);

		this.buttonWidget.setX((this.x + sidebarWidth - maxButtonWidth) - 10);
		this.buttonWidget.setY(this.y + this.getHeight() - 16);
		this.buttonWidget.render(context, mouseX, mouseY, delta);

		context.cm$drawCenteredText(
				this.text,
				this.buttonWidget.getX() + this.buttonWidget.getWidth() / 2,
				this.buttonWidget.getY() + this.sidebarElementHeight / 2 - this.buttonWidget.getHeight() / 2,
				0xFFFFFFFF,
				false);

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
		if (this.buttonWidget.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}
}
