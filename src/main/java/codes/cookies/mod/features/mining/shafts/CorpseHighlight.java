package codes.cookies.mod.features.mining.shafts;

import codes.cookies.mod.config.categories.mining.ShaftCategory;
import codes.cookies.mod.render.Renderable;
import codes.cookies.mod.render.types.BeaconBeam;
import codes.cookies.mod.render.types.Box;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public record CorpseHighlight(BlockPos blockPos, BeaconBeam beam, Box box, int color) implements Renderable {

	public CorpseHighlight(BlockPos blockPos, int color) {
		this(blockPos, new BeaconBeam(blockPos.toCenterPos(), 200, color), new Box(blockPos, color, false), color);
	}

	@Override
	public void render(WorldRenderContext context) {
		if (!ShaftCategory.enabled) {
			return;
		}
		CookiesUtils.getPlayer().ifPresent(player -> {
			if (ShaftCategory.beam) {
				this.beam.render(context);
			}
			if (ShaftCategory.box) {
				this.box.render(context);
			}
			if (!ShaftCategory.text) {
				return;
			}

			double distance = player.getBlockPos().toCenterPos().distanceTo(this.blockPos.toCenterPos());

			final float scale = (float) Math.max(0.01f, Math.sqrt(distance) / 50);
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			Camera camera = context.camera();
			if (!camera.isReady() || minecraftClient.getEntityRenderDispatcher().gameOptions == null) {
				return;
			}
			TextRenderer textRenderer = minecraftClient.textRenderer;
			final MatrixStack matrices = context.matrixStack();
			matrices.push();
			matrices.translate(
					(float) (this.blockPos.getX()) + 0.5,
					(float) (this.blockPos.getY()) + 2.07,
					(float) (this.blockPos.getZ()) + 0.5);
			matrices.multiply(camera.getRotation());
			matrices.scale(scale, -scale, scale);

			final MutableText corpseLocation = Text.literal("Corpse Location");
			textRenderer.draw(
					corpseLocation,
					(-textRenderer.getWidth(corpseLocation)) / 2.0f - (0 / scale),
					0.0f,
					color,
					false,
					matrices.peek().getPositionMatrix(),
					context.consumers(),
					TextRenderer.TextLayerType.SEE_THROUGH,
					0,
					0xF000F0
			);
			matrices.pop();
		});
	}
}
