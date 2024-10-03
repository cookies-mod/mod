package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;
import dev.morazzer.cookies.mod.render.types.Line;
import dev.morazzer.cookies.mod.render.types.Outlines;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector2i;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class CreeperBeamPuzzleSolver extends PuzzleSolver {
	private static final Block[] BLOCKS = {Blocks.PRISMARINE, Blocks.SEA_LANTERN};
	private static final int[] COLORS =
			{Constants.SUCCESS_COLOR, Constants.FAIL_COLOR, Constants.MAIN_COLOR, 0xFFBED2FE, 0xFFFDFD96, 0xFF6EB5FF};

	@Override
	protected void onRoomEnter(DungeonRoom dungeonRoom) {
		this.clearRenderables();
		super.onRoomEnter(dungeonRoom);
		final BlockPos first = dungeonRoom.getTopLeft().map(block -> new BlockPos(block.x, 70, block.y)).orElse(null);
		final BlockPos second =
				dungeonRoom.getCenter().map(block -> new BlockPos(block.x + 15, 84, block.y + 15)).orElse(null);

		if (first == null || second == null) {
			return;
		}
		final Vector2i roomCenter = dungeonRoom.getCenter().get();
		final BlockPos center = new BlockPos(roomCenter.x, 75, roomCenter.y);

		final ClientWorld clientWorld = this.getWorld().orElse(null);
		if (clientWorld == null) {
			return;
		}

		final List<BlockPos> list = BlockPos.stream(first, second)
				.filter(blockPos -> ArrayUtils.contains(BLOCKS, clientWorld.getBlockState(blockPos).getBlock()))
				.map(BlockPos::mutableCopy)
				.map(BlockPos.class::cast)
				.toList();

		final Box tempBox = new Box(center).offset(0, 0.26, 0);
		final Box box = tempBox.withMaxY(tempBox.maxY + 1.1);
		if (isDebugEnabled()) {
			this.addDebugRenderable(new Outlines(box.getMaxPos(), box.getMinPos(), 0xFFFF00FF, 1, true));
		}


		Set<BlockPos> used = new HashSet<>();
		int lines = 0;

		for (int i = 0; i < list.size() - 1; i++) {
			final BlockPos pos1 = list.get(i);
			for (int j = i + 1; j < list.size(); j++) {
				final BlockPos pos2 = list.get(j);
				final Optional<Vec3d> intersectionPoint = box.raycast(pos1.toCenterPos(), pos2.toCenterPos());
				if (intersectionPoint.isPresent()) {
					if (used.contains(pos1) || used.contains(pos2)) {
						continue;
					}
					used.add(pos1);
					used.add(pos2);
					if (lines >= COLORS.length) {
						return;
					}
					this.addRenderable(new Line(pos1.toCenterPos(), pos2.toCenterPos(), COLORS[lines++]));
					break;
				}
			}
		}
	}
}
