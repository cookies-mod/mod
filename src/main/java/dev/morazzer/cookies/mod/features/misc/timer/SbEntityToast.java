package dev.morazzer.cookies.mod.features.misc.timer;

import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SbEntityToast implements Toast {
	private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
	private final ImageData data;
	private final Supplier<Text> messageSupplier;
	private final int timeToStay;
	private Visibility visibility = Visibility.SHOW;

	public SbEntityToast(ImageData data, Supplier<Text> messageSupplier, int timeToStay) {
		this.data = data;
		this.messageSupplier = messageSupplier;
		this.timeToStay = timeToStay;
	}

	@Override
	public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
		if (startTime > timeToStay) {
			this.visibility = Visibility.HIDE;
		}
		context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
		context.drawTexture(
				this.data.texture,
				data.x,
				data.y,
				0,
				0,
				data.width,
				data.height,
				data.width,
				data.height);
		context.drawText(
				MinecraftClient.getInstance().textRenderer,
				this.messageSupplier.get(),
				30,
				this.getHeight() / 2 - 4,
				-1,
				true);
		return this.visibility;
	}

	public record ImageData(int width, int height, int x, int y, Identifier texture) {
	}
}
