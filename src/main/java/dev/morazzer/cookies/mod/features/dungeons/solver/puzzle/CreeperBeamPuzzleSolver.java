package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import dev.morazzer.cookies.mod.config.categories.DungeonConfig;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;
import dev.morazzer.cookies.mod.render.types.Line;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector2i;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CreeperBeamPuzzleSolver extends PuzzleSolver {
	private static final Block[] BLOCKS = {Blocks.PRISMARINE, Blocks.SEA_LANTERN};
	private static final int[] COLORS = {Constants.SUCCESS_COLOR, 0xFFBED2FE, 0xFFFDFD96, 0xFF6EB5FF};

	public CreeperBeamPuzzleSolver() {
		super(DungeonConfig.getInstance().puzzleFoldable.creeperBeams);
	}

	@Override
	protected void onRoomEnter(DungeonRoom dungeonRoom) {
		this.clearRenderables();
		super.onRoomEnter(dungeonRoom);
		if (this.isDisabled()) {
			return;
		}
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

		final Vec3d creeperCenter = center.up().toCenterPos();
		final Set<Connection> map = new HashSet<>();

		for (int firstIndex = 0; firstIndex < list.size(); firstIndex++) {
			final BlockPos firstPos = list.get(firstIndex);
			for (int secondIndex = firstIndex; secondIndex < list.size(); secondIndex++) {
				final BlockPos secondPos = list.get(secondIndex);
				map.add(new Connection(creeperCenter, firstPos, secondPos));
			}
		}

		final Set<BlockPos> usedBlocks = new HashSet<>();
		final AtomicInteger currentColor = new AtomicInteger();

		map.stream().sorted(Comparator.comparingDouble(Connection::distance)).filter(connection -> {
			if (usedBlocks.contains(connection.pos1) || usedBlocks.contains(connection.pos2)) {
				return false;
			} else {
				usedBlocks.add(connection.pos1);
				usedBlocks.add(connection.pos2);
				return true;
			}
		}).limit(4).forEach(connection -> this.addRenderable(new Line(
				connection.pos1.toCenterPos(),
				connection.pos2.toCenterPos(),
				COLORS[Math.min(currentColor.getAndIncrement(), COLORS.length - 1)])));
	}

	record Connection(double distance, BlockPos pos1, BlockPos pos2) {
		public Connection(Vec3d point, BlockPos pos1, BlockPos pos2) {
			this(MathUtils.distance(point, pos1.toCenterPos(), pos2.toCenterPos()), pos1, pos2);
		}
	}
}
