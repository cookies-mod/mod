package codes.cookies.mod.features.dungeons.solver.puzzle;

import codes.cookies.mod.config.categories.dungeons.PuzzleCategory;
import codes.cookies.mod.repository.constants.dungeons.DungeonConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Predicates;
import codes.cookies.mod.features.dungeons.map.DungeonRoom;
import codes.cookies.mod.render.types.BlockHighlight;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2i;

import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class QuizPuzzleSolver extends PuzzleSolver {

	boolean hasStarted;
	long startedAt = -1;
	boolean isDone = false;
	private Solution solution;
	private final List<String> solutionStrings = new ArrayList<>();
	private BlockPos center;
	private Direction puzzleDirection;

	public QuizPuzzleSolver() {
		super(PuzzleCategory.quiz);
	}

	@Override
	public void onUnloadedChatMessage(String message) {
		if (this.isDisabled()) {
			return;
		}
		if (message.startsWith("[STATUE] Oruo the Omniscient")) {
			this.solutionStrings.clear();
			this.solution = null;
			this.isDone = false;
			this.clearRenderables();
			if (!this.hasStarted) {
				this.hasStarted = true;
				this.startedAt = System.currentTimeMillis();
			}
		}

		if (!this.hasStarted) {
			return;
		}

		if (DungeonConstants.quizAnswers.containsKey(message)) {
			this.solutionStrings.addAll(DungeonConstants.quizAnswers.getOrDefault(message, Collections.emptyList()));
		}

		if (message.equalsIgnoreCase("What SkyBlock year is it?")) {
			this.solutionStrings.add(
					"Year " + Math.floor((System.currentTimeMillis() / 1000d - 1560276000) / 446400 + 1));
			return;
		}

		if (!message.matches("[ⓐⓑⓒ] .+") || this.solution != null) {
			return;
		}

		final String answer = message.substring(1).trim();
		boolean correctAnswer = this.solutionStrings.stream().anyMatch(answer::equalsIgnoreCase);
		if (correctAnswer) {
			this.solution = Solution.getForMessage(message);
			this.placeRenderables();
			CookiesUtils.sendMessage("Correct solution: " + this.solution);
		}
	}

	@Override
	protected void onRoomEnter(DungeonRoom dungeonRoom) {
		if (this.isDisabled()) {
			return;
		}
		super.onRoomEnter(dungeonRoom);
		final Optional<Vector2i> center = dungeonRoom.getCenter();
		if (isDebugEnabled()) {
			center.ifPresent(vector2i -> this.addDebugRenderable(new BlockHighlight(new BlockPos(vector2i.x,
					56,
					vector2i.y), Constants.MAIN_COLOR)));
		}
		if (center.isEmpty()) {
			return;
		}

		final Vector2i vector2i = center.get();
		this.center = new BlockPos(vector2i.x, 56, vector2i.y);

		List<Direction> directions = List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

		final Optional<ClientWorld> optionalWorld = this.getWorld();
		if (optionalWorld.isEmpty()) {
			return;
		}

		final ClientWorld world = optionalWorld.get();
		for (Direction direction : directions) {
			final BlockPos add = this.center.add(direction.getVector().multiply(2));
			if (world.getBlockState(add).getBlock() != Blocks.BLACK_TERRACOTTA) {
				continue;
			}
			this.puzzleDirection = direction;
			if (isDebugEnabled()) {
				this.addDebugRenderable(new BlockHighlight(add, Constants.SUCCESS_COLOR));
			}
			break;
		}

		if (this.puzzleDirection == null) {
			return;
		}

		if (!this.isDone && this.solution != null) {
			this.placeRenderables();
		}
	}

	private void placeRenderables() {
		if (this.center == null || this.puzzleDirection == null || this.solution == null) {
			return;
		}

		final Optional<ClientWorld> optionalWorld = this.getWorld();
		if (optionalWorld.isEmpty()) {
			return;
		}
		final ClientWorld world = optionalWorld.get();
		for (Solution value : Solution.values()) {
			final BlockPos buttonCenter = value.getButtonCenter(this.center, this.puzzleDirection);
			this.addButtonHighlight(buttonCenter,
					value == this.solution ? Constants.SUCCESS_COLOR : Constants.FAIL_COLOR);
			for (ArmorStandEntity entitiesByClass : world.getEntitiesByClass(ArmorStandEntity.class,
					Box.from(new BlockBox(buttonCenter)).expand(1),
					Predicates.alwaysTrue())) {
				if (entitiesByClass.getCustomName() != null &&
					Solution.getForMessage(CookiesUtils.stripColor(entitiesByClass.getCustomName().getString())) !=
					value) {
					entitiesByClass.setCustomNameVisible(false);
				}
			}
		}
		this.isDone = true;
	}

	private void addButtonHighlight(BlockPos buttonCenter, int color) {
		this.addRenderable(new BlockHighlight(buttonCenter, color));
		this.addRenderable(new BlockHighlight(buttonCenter.add(1, 0, 0), color));
		this.addRenderable(new BlockHighlight(buttonCenter.add(-1, 0, 0), color));
		this.addRenderable(new BlockHighlight(buttonCenter.add(0, 0, 1), color));
		this.addRenderable(new BlockHighlight(buttonCenter.add(0, 0, -1), color));
	}

	@Override
	protected void resetPuzzle() {
		super.resetPuzzle();
		this.startedAt = -1;
		this.hasStarted = false;
		this.solution = null;
	}

	@RequiredArgsConstructor
	private enum Solution {
		A("ⓐ"),
		B("ⓑ"),
		C("ⓒ");

		public BlockPos getButtonCenter(BlockPos center, Direction puzzleDirection) {
			if ((this.ordinal() + 1) % 2 == 0) {
				return center.up(14).offset(puzzleDirection.getOpposite(), 6);
			} else {
				return center.up(14)
						.offset(puzzleDirection.getOpposite(), 9)
						.offset(this == A ? puzzleDirection.rotateYCounterclockwise() :
								puzzleDirection.rotateYClockwise(), 5);
			}
		}

		public static Solution getForMessage(String message) {
			for (Solution value : values()) {
				if (message.startsWith(value.display)) {
					return value;
				}
			}
			return null;
		}

		final String display;
	}
}
