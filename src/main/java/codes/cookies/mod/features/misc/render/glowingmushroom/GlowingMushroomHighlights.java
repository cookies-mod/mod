package codes.cookies.mod.features.misc.render.glowingmushroom;

import java.util.concurrent.TimeUnit;

import codes.cookies.mod.config.categories.FarmingCategory;
import codes.cookies.mod.events.world.ParticleEmitEvent;
import codes.cookies.mod.render.WorldRender;
import codes.cookies.mod.render.types.Outlines;
import codes.cookies.mod.render.types.Timed;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GlowingMushroomHighlights {

	public static void register() {
		ParticleEmitEvent.EVENT.register(GlowingMushroomHighlights::onParticle);
	}

	private static void onParticle(Particle particle) {
		if (!SkyblockUtils.isCurrentlyInSkyblock()) {
			return;
		}

		if (!LocationUtils.Island.MUSHROOM_DESERT.isActive()) {
			return;
		}

		if (!FarmingCategory.highlightGlowingMushrooms) {
			return;
		}

		if (particle instanceof SpellParticle spellParticle) {
			final Vec3d center = spellParticle.getBoundingBox().getCenter();
			final BlockPos centerBlockPos = CookiesUtils.mapToBlockPos(center);
			final BlockState blockState = spellParticle.world.getBlockState(centerBlockPos);
			if (blockState.isOf(Blocks.BROWN_MUSHROOM) || blockState.isOf(Blocks.RED_MUSHROOM)) {
				final net.minecraft.util.math.Box boundingBox = blockState.getOutlineShape(
						particle.world,
						centerBlockPos).getBoundingBox().offset(centerBlockPos);
				WorldRender.addRenderable(new Timed(
						new Outlines(
								boundingBox.getMinPos(),
								boundingBox.getMaxPos(),
								blockState.isOf(Blocks.RED_MUSHROOM) ? Formatting.RED.getColorValue() : 0xFF9A7B4D, 5),
						1,
						TimeUnit.SECONDS));
			}
		}
	}

}
