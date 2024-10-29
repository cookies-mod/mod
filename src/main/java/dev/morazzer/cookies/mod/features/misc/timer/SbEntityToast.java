package dev.morazzer.cookies.mod.features.misc.timer;

import java.util.function.Supplier;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SbEntityToast implements Toast {
	private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
	private final Identifier identifier;
	private final Supplier<Text> messageSupplier;
	private final int timeToStay;
	private Visibility visibility = Visibility.SHOW;

	public SbEntityToast(Identifier identifier, Supplier<Text> messageSupplier, int timeToStay) {
		this.identifier = identifier;
		this.messageSupplier = messageSupplier;
		this.timeToStay = timeToStay;
	}

	@Override
	public Visibility getVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager manager, long time) {
		if (time > timeToStay) {
			this.visibility = Visibility.HIDE;
		}
	}

	@Override
	public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
		context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
		context.drawTexture(RenderLayer::getGuiTextured, this.identifier, 10, 4, 0, 0, 12, 25, 12, 25);
		context.drawText(textRenderer, this.messageSupplier.get(),30, this.getHeight() / 2 - 4, -1, true);
	}
}
