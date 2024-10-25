package dev.morazzer.cookies.mod.features.mining.hollows;

import dev.morazzer.cookies.mod.config.categories.MiningConfig;
import dev.morazzer.cookies.mod.events.profile.ServerSwapEvent;
import dev.morazzer.cookies.mod.render.Renderable;
import dev.morazzer.cookies.mod.render.WorldRender;
import dev.morazzer.cookies.mod.render.types.BeaconBeam;
import dev.morazzer.cookies.mod.render.types.Box;
import dev.morazzer.cookies.mod.render.types.Composite;
import dev.morazzer.cookies.mod.render.types.Line;
import dev.morazzer.cookies.mod.render.types.WorldText;
import dev.morazzer.cookies.mod.repository.constants.RepositoryConstants;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;
import dev.morazzer.mods.cookies.generated.Regions;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class MinesOfDivanHelper {

	static Identifier DEBUG = DevUtils.createIdentifier("ch/mod_debug");
	static @Language("RegExp") String REGEX = "\\[NPC] Keeper of (\\w+): .*?!";
	static BlockPos centerPos;
	static BlockPos lastEntityClicked;
	static Renderable latest;
	static Vec3d lastPlayerPos;
	static double lastDistance;
	static int matches = 0;
	static BlockPos lastSolution;
	static long lastMessageSentAt;

	public static void init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register(MinesOfDivanHelper::allowGame);
		UseBlockCallback.EVENT.register(MinesOfDivanHelper::blockInteract);
		AttackEntityCallback.EVENT.register(MinesOfDivanHelper::attackEntity);
		UseEntityCallback.EVENT.register(MinesOfDivanHelper::useEntity);
		ServerSwapEvent.SERVER_SWAP.register(MinesOfDivanHelper::reset);
	}

	public static void clickEntity(Entity entity) {
		if (isNotInMinesOfDivan()) {
			return;
		}
		lastEntityClicked = entity.getBlockPos();
	}

	public static boolean isEnabled() {
		return MiningConfig.getInstance().modHelper.getValue();
	}

	public static boolean isNotInMinesOfDivan() {
		return !LocationUtils.Island.CRYSTAL_HOLLOWS.isActive() || LocationUtils.getRegion() != Regions.MINES_OF_DIVAN;
	}

	public static void reset() {
		removeRenderables();
		centerPos = null;
		lastEntityClicked = null;
		matches = 0;
		lastPlayerPos = null;
		lastDistance = 0;
	}

	public static void removeRenderables() {
		if (latest != null) {
			WorldRender.removeRenderable(latest);
			latest = null;
		}
	}

	private static ActionResult useEntity(
			PlayerEntity playerEntity,
			World world,
			Hand hand,
			Entity entity,
			@Nullable EntityHitResult entityHitResult) {
		if (isEnabled()) {
			clickEntity(entity);
		}
		return ActionResult.PASS;
	}

	private static ActionResult attackEntity(
			PlayerEntity playerEntity,
			World world,
			Hand hand,
			Entity entity,
			@Nullable EntityHitResult entityHitResult) {
		if (isEnabled()) {
			clickEntity(entity);
		}
		return ActionResult.PASS;
	}

	private static boolean allowGame(Text text, boolean overlay) {
		if (isEnabled()) {
			if (overlay) {
				MinesOfDivanHelper.onActionBarUpdate(text);
			} else {
				MinesOfDivanHelper.onInGameMessage(text);
			}
		}
		return true;
	}

	private static ActionResult blockInteract(
			PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
		if (isEnabled()) {
			MinesOfDivanHelper.onBlockInteract(world, blockHitResult);
		}
		return ActionResult.PASS;
	}

	private static void onActionBarUpdate(Text text) {
		if (isNotInMinesOfDivan()) {
			removeRenderables();
			lastEntityClicked = null;
			return;
		}
		if (centerPos == null) {
			if (lastMessageSentAt + 60000 < System.currentTimeMillis()) {
				lastMessageSentAt = System.currentTimeMillis();
				CookiesUtils.sendFailedMessage("Click a keeper to enable the solver!");
			}
			removeRenderables();
			return;
		}
		final String literal = CookiesUtils.stripColor(text.getString());
		final String distanceLiteral = literal.replaceAll(".* TREASURE: ([\\d.]+)m.*", "$1");

		double chestDistance;
		try {
			chestDistance = Double.parseDouble(distanceLiteral);
		} catch (NumberFormatException ignored) {
			return;
		}

		final Vec3d playerPos = CookiesUtils.getPlayer().map(PlayerEntity::getPos).orElse(null);
		if (playerPos == null) {
			return;
		}
		Vec3d centerPos3d = centerPos.toCenterPos().subtract(0.5, 0.5, 0.5);
		final Vec3d relative = playerPos.subtract(centerPos3d);
		if (lastPlayerPos == playerPos && lastDistance == chestDistance) {
			if (matches < 2) {
				matches++;
				return;
			}
		} else {
			lastPlayerPos = playerPos;
			lastDistance = chestDistance;
			matches = 0;
			return;
		}

		Renderable renderable;
		boolean debug = DevUtils.isEnabled(DEBUG);
		if (debug) {
			renderable = new BeaconBeam(relative, 0, Constants.MAIN_COLOR);
		} else {
			renderable = null;
		}
		BlockPos closest = null;
		double distanceDelta = Double.MAX_VALUE;
		for (BlockPos relativePos : RepositoryConstants.modLocations) {
			Vec3d pos = relativePos.toCenterPos().subtract(0.5, 0, 0.5);
			double distance = Math.abs(relative.distanceTo(pos) - chestDistance);
			if (debug) {
				renderable = new Composite(
						renderable,
						new Line(relative.add(centerPos3d),
								pos.add(centerPos3d).add(0.5, 0.5, 0.5),
								Constants.MAIN_COLOR,
								Constants.SUCCESS_COLOR),
						new WorldText(pos.add(centerPos3d).add(0.5, 2, 0.5), Text.literal("D: " + distance)));
			}
			if (closest == null) {
				closest = relativePos;
				distanceDelta = distance;
			}
			if (distanceDelta > distance) {
				closest = relativePos;
				distanceDelta = distance;
			}
		}
		if (closest == null || lastSolution == closest) {
			return;
		}
		CookiesUtils.sendSuccessMessage("Found new solution!");
		lastSolution = closest;
		WorldRender.removeRenderable(latest);
		renderable = new Composite(renderable, create(closest.add(centerPos)));
		latest = renderable;
		WorldRender.addRenderable(renderable);
		matches = 0;
	}

	private static void onInGameMessage(Text text) {
		if (isNotInMinesOfDivan()) {
			return;
		}
		final String literal = CookiesUtils.stripColor(text.getString());
		if (!literal.matches(REGEX)) {
			return;
		}
		final String clicked = literal.replaceAll(REGEX, "$1");
		if (DevUtils.isEnabled(DEBUG)) {
			CookiesUtils.sendMessage(clicked);
		}
		Direction direction;
		switch (clicked) {
			case "Diamond" -> direction = Direction.EAST;
			case "Gold" -> direction = Direction.NORTH;
			case "Lapis" -> direction = Direction.WEST;
			case "Emerald" -> direction = Direction.SOUTH;
			default -> {
				return;
			}
		}
		if (lastEntityClicked == null) {
			return;
		}
		final BlockPos offset = lastEntityClicked.offset(direction.rotateYClockwise(), 3).offset(direction, 33);
		if (DevUtils.isEnabled(DEBUG)) {
			CookiesUtils.sendMessage(offset.toString());
		}
		if (centerPos == offset) {
			return;
		}
		CookiesUtils.sendSuccessMessage("Enabled solver!");
		centerPos = offset;
	}

	private static void onBlockInteract(World world, BlockHitResult blockHitResult) {
		if (isNotInMinesOfDivan()) {
			return;
		}
		if (centerPos == null || blockHitResult == null) {
			return;
		}
		final BlockPos blockPos = blockHitResult.getBlockPos();
		if (world == null || world.getBlockState(blockPos).getBlock() != Blocks.CHEST) {
			return;
		}
		final BlockPos localPos = blockPos.subtract(centerPos);
		if (RepositoryConstants.modLocations.contains(localPos)) {
			return;
		}
		RepositoryConstants.modLocations.add(localPos);
	}

	private static Renderable create(BlockPos blockPos) {
		final net.minecraft.util.math.Box expand = new net.minecraft.util.math.Box(blockPos).expand(5, 50, 5);
		return new Composite(
				new Box(blockPos, Constants.SUCCESS_COLOR, false),
				new BeaconBeam(blockPos.toCenterPos(), 200, Constants.SUCCESS_COLOR),
				new Box(expand.getMinPos(), expand.getMaxPos(), 0x8F000000 | Constants.SUCCESS_COLOR & 0xFFFFFF));
	}
}
