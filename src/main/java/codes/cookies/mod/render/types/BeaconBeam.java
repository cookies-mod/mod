package codes.cookies.mod.render.types;

import codes.cookies.mod.render.Renderable;

import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Renders a beacon beam in the world.
 *
 * @param origin    The origin of the beam.
 * @param maxHeight The max height of the beam.
 * @param color     The color in argb.
 */
public record BeaconBeam(Vec3d origin, int maxHeight, int color) implements Renderable {

	@Override
	public void render(WorldRenderContext context) {
		context.matrixStack().push();
		context.matrixStack().translate(origin.getX() - 0.5, origin.getY() - 0.5, origin.getZ() - 0.5);
		BeaconBlockEntityRenderer.renderBeam(
				context.matrixStack(),
				context.consumers(),
				context.tickCounter().getTickDelta(true),
				0,
				0,
				maxHeight,
				color);
		context.matrixStack().pop();
	}
}
