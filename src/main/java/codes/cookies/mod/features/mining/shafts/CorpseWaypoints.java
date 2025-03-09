package codes.cookies.mod.features.mining.shafts;

import java.util.HashMap;
import java.util.Map;

import codes.cookies.mod.config.categories.mining.ShaftCategory;
import codes.cookies.mod.events.mining.MineshaftEvents;
import codes.cookies.mod.render.Renderable;
import codes.cookies.mod.render.WorldRender;
import codes.cookies.mod.repository.constants.mining.ShaftCorpseLocations;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class CorpseWaypoints {
	private static final Map<BlockPos, Renderable> renderables = new HashMap<>();

	public static void register() {
		UseEntityCallback.EVENT.register(CorpseWaypoints::useEntity);
		MineshaftEvents.JOIN_SHAFT.register(CorpseWaypoints::setupRenderables);
		MineshaftEvents.LEAVE.register(CorpseWaypoints::leaveShaft);
	}

	private static void leaveShaft() {
		renderables.values().forEach(WorldRender::removeRenderable);
		renderables.clear();
	}

	private static ActionResult useEntity(
			PlayerEntity playerEntity,
			World world,
			Hand hand,
			Entity entity,
			@Nullable EntityHitResult entityHitResult
	) {
		if (!ShaftCategory.enabled) {
			return ActionResult.PASS;
		}
		if (!(entity instanceof ArmorStandEntity)) {
			return ActionResult.PASS;
		}
		if (entity.isInvisible()) {
			return ActionResult.PASS;
		}
		ShaftFeatures.getCurrentMineshaftLocations().ifPresent(shaftLocations -> {
			if (!shaftLocations.corpseLocations().contains(entity.getBlockPos())) {
				shaftLocations.corpseLocations().add(entity.getBlockPos());
				final BlockPos blockPos = entity.getBlockPos();
				CookiesUtils.sendSuccessMessage(
						"You found a new corpse location, if you want to help us continue support for this feature please send a screenshot of this message on our discord! (%s: %s,%s,%s)".formatted(
								shaftLocations.id(),
								blockPos.getX(),
								blockPos.getY(),
								blockPos.getZ()));
			}
			WorldRender.removeRenderable(renderables.get(entity.getBlockPos()));
		});


		return ActionResult.PASS;
	}

	private static void setupRenderables(ShaftCorpseLocations.ShaftLocations shaftLocations) {
		for (BlockPos blockPos : shaftLocations.corpseLocations()) {
			final CorpseHighlight box = new CorpseHighlight(blockPos, ShaftCategory.color);
			renderables.put(blockPos, box);
			WorldRender.addRenderable(box);
		}
	}
}
