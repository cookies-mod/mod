package codes.cookies.mod.features.mining.shafts;

import java.util.HashMap;
import java.util.Map;

import codes.cookies.mod.config.categories.MiningConfig;
import codes.cookies.mod.events.IslandChangeEvent;
import codes.cookies.mod.events.ScoreboardUpdateEvent;
import codes.cookies.mod.render.Renderable;
import codes.cookies.mod.render.WorldRender;
import codes.cookies.mod.repository.constants.mining.ShaftCorpseLocations;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.entity.decoration.ArmorStandEntity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class CorpseWaypoints {
	private static boolean isInShaft = false;
	private static final Map<BlockPos, Renderable> renderables = new HashMap<>();
	private static ShaftCorpseLocations.ShaftLocations locations;

	public static void register() {
		IslandChangeEvent.EVENT.register(CorpseWaypoints::swapIsland);
		ScoreboardUpdateEvent.EVENT.register(CorpseWaypoints::updateLine);
		UseEntityCallback.EVENT.register(CorpseWaypoints::useEntity);
	}

	private static ActionResult useEntity(
			PlayerEntity playerEntity,
			World world,
			Hand hand,
			Entity entity,
			@Nullable EntityHitResult entityHitResult
	) {
		if (!MiningConfig.getInstance().shaftConfig.enable.getValue()) {
			return ActionResult.PASS;
		}
		if (!(entity instanceof ArmorStandEntity)) {
			return ActionResult.PASS;
		}
		if (entity.isInvisible()) {
			return ActionResult.PASS;
		}
		if (locations != null) {
			if (!locations.corpseLocations().contains(entity.getBlockPos())) {
				locations.corpseLocations().add(entity.getBlockPos());
				final BlockPos blockPos = entity.getBlockPos();
				CookiesUtils.sendSuccessMessage(
						"You found a new corpse location, if you want to help us continue support for this feature please send a screenshot of this message on our discord! (%s: %s,%s,%s)".formatted(
								locations.id(),
								blockPos.getX(),
								blockPos.getY(),
								blockPos.getZ()));
			}
			WorldRender.removeRenderable(renderables.get(entity.getBlockPos()));
		}


		return ActionResult.PASS;
	}

	private static void updateLine(int index, String line) {
		if (!MiningConfig.getInstance().shaftConfig.enable.getValue()) {
			return;
		}
		if (!isInShaft) {
			return;
		}
		if (locations != null) {
			return;
		}
		final String server = LocationUtils.getServerDisplayName().orElse("");
		if (!line.contains(server)) {
			return;
		}
		final String[] split = line.split(server);
		if (split.length != 2) {
			return;
		}
		final String shaftType = split[1].trim();

		locations = ShaftCorpseLocations.getCachedOrCreate(shaftType);
		if (!locations.cached()) {
			CookiesUtils.sendFailedMessage("No cached data found, creating new one!");
		}
		setupRenderables();
	}

	private static void setupRenderables() {
		for (BlockPos blockPos : locations.corpseLocations()) {
			final CorpseHighlight box = new CorpseHighlight(blockPos, MiningConfig.getInstance().shaftConfig.color.getColorValue());
			renderables.put(blockPos, box);
			WorldRender.addRenderable(box);
		}
	}

	private static void swapIsland(LocationUtils.Island previous, LocationUtils.Island current) {
		isInShaft = false;
		locations = null;
		renderables.values().forEach(WorldRender::removeRenderable);
		renderables.clear();

		if (current == LocationUtils.Island.MINESHAFT) {
			isInShaft = true;
		}
	}

}
