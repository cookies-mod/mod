package codes.cookies.mod.render.widgets;

import java.awt.*;
import java.util.function.Consumer;

import codes.cookies.mod.utils.accessors.TextRenderUtils;
import lombok.Setter;

import net.minecraft.client.gui.DrawContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Consumers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ColorInputWidget extends TextFieldWidget {
	private final boolean canHaveAlpha = false;
	private Color color;
	@Setter
	private Consumer<Color> callback = Consumers.nop();

	public ColorInputWidget(
			TextRenderer textRenderer,
			int height,
			Color text,
			boolean canHaveAlpha
	) {
		super(textRenderer, 10, height, Text.literal(toText(text, canHaveAlpha)));
		this.update();
	}
	private int getBackgroundColor() {
		return 0xFF << 24 | ~this.color.getRGB() & 0xFFFFFF;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		final int fullAlphaColor =
				this.getFullAlphaColor(this.color == null ? -1 : this.color.getRGB());
		context.fill(
				this.getX() - 2,
				this.getY() - 2,
				this.getX() + this.getWidth(),
				this.getY() + this.getHeight() - 1,
				fullAlphaColor);
		context.fill(
				this.getX() - 1,
				this.getY() - 1,
				this.getX() + this.getWidth() - 1,
				this.getY() + this.getHeight() - 2,
				this.getBackgroundColor());

		TextRenderUtils.disableShadows();
		super.renderWidget(context, mouseX, mouseY, delta);
		TextRenderUtils.enableShadows();

		context.getMatrices().push();
		context.getMatrices().translate(0, 0, 10);
		context.fill(
				this.getX() - 2,
				this.getY() + this.getHeight() - 2,
				this.getX() + this.getWidth(),
				this.getY() + this.getHeight() - 1,
				fullAlphaColor);
		context.getMatrices().pop();
	}

	public Color getColor() {
		return null;
	}

	public void setColor(Color color) {
		this.color = color;
		this.setEditableColor(this.getFullAlphaColor(this.color == null ? -1 : this.color.getRGB()));
		this.setText(this.toText(this.color));
	}

	private int getFullAlphaColor(int color) {
		return 0xFF << 24 | color & 0xFFFFFF;
	}


	private void changeValue(String s) {
		final Color color = this.toColor(s);
		if (color == null) {
			return;
		}
		this.setEditableColor(this.getFullAlphaColor(color.getRGB()));
		this.color = this.toColor(s);
		this.callback.accept(this.color);
	}

	public Color toColor(String text) {
		if (StringUtils.isBlank(text)) {
			return null;
		}
		try {

			final int color = Integer.parseUnsignedInt(text.substring(1), 16);
			return new Color(color, this.canHaveAlpha);
		} catch (NumberFormatException e) {
			return null;
		}
	}


	public void update() {
		this.setEditable(true);
		this.setEditableColor(this.getFullAlphaColor(this.color == null ? -1 : this.color.getRGB()));
		this.setChangedListener(this::changeValue);
		this.active = true;
		this.setVisible(true);
		this.setTextPredicate(this::isAllowed);
		this.setEditable(true);
		this.setDrawsBackground(false);
		this.setMaxLength(this.canHaveAlpha ? 9 : 7);
		this.setWidth((this.canHaveAlpha ? 9 : 7) * 5 + 10);
		this.setText(this.toText(this.color));
		this.setCursor(0, false);
	}

	private static String toText(Color color, boolean canHaveAlpha) {
		if (color == null) {
			if (canHaveAlpha) {
				return "#00000000";
			}
			return "#000000";
		}
		return "#" + Integer.toHexString(canHaveAlpha ? color.getRGB() : color.getRGB() & 0xFFFFFF);
	}

	private String toText(Color color) {
		return toText(color, this.canHaveAlpha);
	}


	private boolean isAllowed(String s) {
		if (!s.startsWith("#")) {
			return false;
		}
		if (s.length() == 1) {
			return true;
		}
		try {
			Integer.parseUnsignedInt(s.substring(1), 16);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
