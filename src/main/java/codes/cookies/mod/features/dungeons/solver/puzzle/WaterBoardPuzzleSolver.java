package codes.cookies.mod.features.dungeons.solver.puzzle;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.features.dungeons.DungeonInstance;
import codes.cookies.mod.features.dungeons.map.DungeonRoom;
import codes.cookies.mod.render.Renderable;
import codes.cookies.mod.render.WorldRender;
import codes.cookies.mod.render.types.BlockHighlight;
import codes.cookies.mod.render.types.Line;
import codes.cookies.mod.render.types.WorldText;
import codes.cookies.mod.repository.constants.dungeons.DungeonConstants;
import codes.cookies.mod.repository.constants.dungeons.WaterEntry;
import codes.cookies.mod.utils.RenderUtils;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.minecraft.NonCacheMutableText;
import codes.cookies.mod.utils.minecraft.SupplierTextContent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector2i;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Solver for the water board puzzle.
 */
public class WaterBoardPuzzleSolver extends PuzzleSolver {
	private static final Block[] ALLOWED_BLOCKS = new Block[]{Blocks.GOLD_BLOCK,
			Blocks.TERRACOTTA,
			Blocks.EMERALD_BLOCK,
			Blocks.DIAMOND_BLOCK,
			Blocks.QUARTZ_BLOCK,
			Blocks.COAL_BLOCK};
	private final DungeonInstance dungeon;
	private final Map<LeverType, List<Double>> solution = new HashMap<>();
	private Direction puzzleDirection = null;
	private BlockPos chestPosition = null;
	private long openedWater = -1;
	private int totalClicks = 0;
	private int lastTotalClicks = -1;
	private Renderable line;
	private Vec3d leverPos;

	public WaterBoardPuzzleSolver(DungeonInstance dungeon) {
		super(DungeonConfig.getInstance().puzzleFoldable.waterBoard);
		this.dungeon = dungeon;
	}

	@Override
	protected void onRoomEnter(DungeonRoom dungeonRoom) {
		if (this.isDisabled()) {
			return;
		}
		super.onRoomEnter(dungeonRoom);
		final Vector2i vector2i = dungeonRoom.getCenter().orElse(null);
		if (vector2i == null) {
			return;
		}

		final BlockPos roomCenter = new BlockPos(vector2i.x, 56, vector2i.y);

		if (this.puzzleDirection != null) {
			return;
		}

		List<Direction> directions = List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

		Direction puzzleDirection = null;
		BlockPos chestPosition = null;
		for (Direction direction : directions) {
			final BlockPos add = roomCenter.add(direction.getVector().multiply(7));
			if (this.getWorld().map(world -> world.getBlockEntity(add)).orElse(null) == null) {
				continue;
			}
			chestPosition = add;
			puzzleDirection = direction;
			if (isDebugEnabled()) {
				this.addDebugRenderable(new BlockHighlight(add, Constants.SUCCESS_COLOR));
			}
			break;
		}

		if (puzzleDirection == null) {
			return;
		}
		this.puzzleDirection = puzzleDirection;
		this.chestPosition = chestPosition;

		final BlockPos boardCenterBottom =
				this.chestPosition.add(this.puzzleDirection.getVector().multiply(4)).add(0, 4, 0);

		final Direction first = this.puzzleDirection.getAxis() == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
		final Direction second = this.puzzleDirection.getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
		BlockPos top = boardCenterBottom.withY(77);

		Set<Block> blocks = new HashSet<>();

		for (BlockPos blockPos : BlockPos.iterate(
				top.offset(first),
				top.offset(second).offset(this.puzzleDirection).offset(Direction.UP))) {
			final Block block = this.getWorld().get().getBlockState(blockPos).getBlock();
			if (ArrayUtils.contains(ALLOWED_BLOCKS, block)) {
				blocks.add(block);
			} else {
				continue;
			}
			if (isDebugEnabled()) {
				this.addDebugRenderable(new BlockHighlight(blockPos.mutableCopy(), Constants.SUCCESS_COLOR));
			}
		}

		final Variant variant = Variant.getVariant(blocks);
		final Set<DoorType> closedDoors = new HashSet<>();
		for (DoorType value : DoorType.values()) {
			if (this.getWorld()
					.map(world -> value.isClosed(this.chestPosition, this.puzzleDirection, world))
					.orElse(false)) {
				closedDoors.add(value);
			}
		}

		if (isDebugEnabled()) {
			CookiesUtils.sendMessage("Closed Doors: [%s]".formatted(String.join(
					", ",
					closedDoors.stream().map(Enum::name).toList())));
			CookiesUtils.sendMessage("Variants: " + variant);
		}

		final Optional<WaterEntry> optionalWaterEntry = DungeonConstants.getFor(
				variant,
				closedDoors.stream()
						.sorted(Comparator.comparingInt(Enum::ordinal))
						.map(Enum::ordinal)
						.map(String::valueOf)
						.collect(Collectors.joining("")));

		if (optionalWaterEntry.isEmpty()) {
			CookiesUtils.sendFailedMessage("No solution found for puzzle");
			return;
		}
		CookiesUtils.sendSuccessMessage("Found solution for puzzle!");

		final WaterEntry waterEntry = optionalWaterEntry.get();
		this.solution.clear();
		this.solution.putAll(waterEntry.times());
	}

	@Override
	public void tick() {
		if (this.isDisabled()) {
			return;
		}
		super.tick();
		long totalTime = System.nanoTime();
		if (this.totalClicks == this.lastTotalClicks) {
			return;
		}
		this.clearRenderables();

		final List<Pair<LeverType, Double>> list = this.solution.entrySet()
				.stream()
				.filter(Predicate.not(entry -> entry.getValue().isEmpty()))
				.filter(entry -> entry.getValue().size() > entry.getKey().get(this.dungeon).orElse(0))
				.map(entry -> new Pair<>(
						entry.getKey(),
						entry.getValue().get(entry.getKey().get(this.dungeon).orElse(0))))
				.sorted(Comparator.<Pair<LeverType, Double>>comparingDouble(Pair::getSecond)
						.thenComparingInt(value -> value.getFirst().ordinal()))
				.toList();

		if (list.isEmpty()) {
			this.leverPos = null;
			return;
		}

		final Pair<LeverType, Double> first = list.getFirst();
		final LeverType firstType = first.getFirst();

		final Optional<BlockPos> firstLeverBlockPosOptional = firstType.getLeverPosition(
				this.chestPosition,
				this.puzzleDirection);
		if (firstLeverBlockPosOptional.isEmpty()) {
			return;
		}
		final BlockPos firstLeverBlockPos = firstLeverBlockPosOptional.get();

		final Vec3d firstLeverPosition = firstLeverBlockPos.toCenterPos();
		this.leverPos = firstLeverPosition;
		this.addRenderable(new BlockHighlight(firstLeverBlockPos, Constants.SUCCESS_COLOR));

		if (list.size() > 1) {
			final Pair<LeverType, Double> second = list.get(1);

			final Optional<BlockPos> secondLeverBlockPosOptional = second.getFirst()
					.getLeverPosition(this.chestPosition, this.puzzleDirection);
			if (secondLeverBlockPosOptional.isEmpty()) {
				return;
			}
			final BlockPos secondLeverBlockPos = secondLeverBlockPosOptional.get();

			final Vec3d secondLeverPosition = secondLeverBlockPos.toCenterPos();
			this.addRenderable(new BlockHighlight(secondLeverBlockPos, Constants.FAIL_COLOR));
			this.addRenderable(new Line(
					secondLeverPosition,
					firstLeverPosition,
					Constants.FAIL_COLOR,
					Constants.SUCCESS_COLOR));
		}


		for (Map.Entry<LeverType, List<Double>> entry : this.solution.entrySet()) {
			int i = 0;
			final LeverType leverType = entry.getKey();
			final Optional<BlockPos> leverBlockPosOptional = leverType.getLeverPosition(this.chestPosition, this.puzzleDirection);
			if (leverBlockPosOptional.isEmpty()) {
				continue;
			}
			final Vec3d add = leverBlockPosOptional.get().toCenterPos().add(0.0, 1, 0.0);
			for (Double rawTime : entry.getValue()) {
				if (rawTime == null) {
					continue;
				}

				Text display =
						NonCacheMutableText.of(new SupplierTextContent(this.getTextSupplier(rawTime, leverType)));

				if (i < leverType.get(this.dungeon).orElse(0)) {
					i++;
					continue;
				}
				this.addRenderable(new WorldText(
						add.offset(
								Direction.UP,
								(i++ - leverType.get(this.dungeon).orElse(0)) * 0.4),
						display,
						true,
						0.04f,
						0f,
						-1,
						false));
			}
		}

		if (isDebugEnabled()) {
			this.addDebugRenderable(new WorldText(
					this.chestPosition.toCenterPos().add(0, 4, 0),
					Text.literal(String.valueOf(System.nanoTime() - totalTime))));
		}

		this.lastTotalClicks = this.totalClicks;
	}

	@Override
	public void beforeRender(final float tickDelta) {
		if (this.isDisabled()) {
			return;
		}
		if (this.chestPosition == null) {
			return;
		}
		final Optional<ClientPlayerEntity> optionalPlayer = CookiesUtils.getPlayer();
		if (optionalPlayer.isEmpty()) {
			return;
		}
		final ClientPlayerEntity player = optionalPlayer.get();
		if (this.leverPos != null) {
			WorldRender.removeRenderable(this.line);
			this.line = new Line(
					RenderUtils.getInterpolated(player, tickDelta),
					this.leverPos,
					Constants.MAIN_COLOR,
					Constants.SUCCESS_COLOR);
			WorldRender.addRenderable(this.line);
		} else if (this.line != null) {
			WorldRender.removeRenderable(this.line);
			this.line = null;
		}
	}

	@Override
	protected void onDisalbe() {
		WorldRender.removeRenderable(this.line);
	}

	@Override
	protected void onEnable() {
		if (this.line != null && this.isLoaded) {
			WorldRender.addRenderable(this.line);
		}
	}

	private Supplier<String> getTextSupplier(double rawTime, LeverType leverType) {
		return () -> {
			final double time;
			if (this.openedWater == -1) {
				time = rawTime;
			} else {
				time = rawTime - ((System.currentTimeMillis() - this.openedWater) / 1000.0);
			}

			if (time <= 0) {
				if (leverType == LeverType.WATER) {
					return "§cClick Me!";
				} else {
					return "§aClick Me!";
				}
			} else {

				return "§f(%.2fs)".formatted(time);
			}
		};
	}

	@Override
	public void onInteract(World world, BlockHitResult blockHitResult, Hand hand) {
		if (this.isDisabled()) {
			return;
		}
		if (blockHitResult.getType() != HitResult.Type.BLOCK) {
			return;
		}
		if (this.puzzleDirection == null || this.chestPosition == null) {
			return;
		}

		final BlockState blockState = world.getBlockState(blockHitResult.getBlockPos());
		final Block block = blockState.getBlock();
		if (block != Blocks.LEVER) {
			return;
		}
		for (LeverType value : LeverType.values()) {
			final Optional<BlockPos> leverPositionOptional = value.getLeverPosition(this.chestPosition, this.puzzleDirection);
			if (leverPositionOptional.isPresent() && leverPositionOptional.get().equals(blockHitResult.getBlockPos())) {
				if (value.lastInteraction + 100 < System.currentTimeMillis()) {
					value.increment(this.dungeon);
					this.totalClicks++;
					if (value == LeverType.WATER && this.openedWater == -1) {
						this.openedWater = System.currentTimeMillis();
					}
				}
				value.lastInteraction = System.currentTimeMillis();
				return;
			}
		}
	}

	public enum Variant implements StringIdentifiable {
		FIRST(Blocks.GOLD_BLOCK, Blocks.TERRACOTTA),
		SECOND(Blocks.EMERALD_BLOCK, Blocks.QUARTZ_BLOCK),
		THIRD(Blocks.QUARTZ_BLOCK, Blocks.DIAMOND_BLOCK),
		FOURTH(Blocks.GOLD_BLOCK, Blocks.QUARTZ_BLOCK);

		public static final Codec<Variant> CODEC = StringIdentifiable.createCodec(Variant::values);
		private final Block[] blocks;

		Variant(Block... blocks) {
			this.blocks = blocks;
		}

		public static Variant getVariant(Collection<Block> blocks) {
			for (Variant value : values()) {
				if (value.blocks.length == blocks.size() && value.matches(blocks)) {
					return value;
				}
			}
			return null;
		}

		public boolean matches(Collection<Block> blocks) {
			for (Block block : blocks) {
				if (ArrayUtils.contains(this.blocks, block)) {
					continue;
				}
				return false;
			}
			return true;
		}

		@Override
		public String asString() {
			return String.valueOf(this.ordinal());
		}
	}

	@RequiredArgsConstructor
	enum DoorType {
		PURPLE(Blocks.PURPLE_WOOL),
		ORANGE(Blocks.ORANGE_WOOL),
		BLUE(Blocks.BLUE_WOOL),
		GREEN(Blocks.LIME_WOOL),
		RED(Blocks.RED_WOOL);

		private final Block block;

		public boolean isClosed(BlockPos chestPos, Direction puzzleDirection, ClientWorld clientWorld) {
			final BlockPos offset = chestPos.offset(puzzleDirection.getOpposite(), 3 + this.ordinal());
			return clientWorld.getBlockState(offset).getBlock() == this.block;
		}
	}

	public enum LeverType implements StringIdentifiable {
		QUARTZ,
		GOLD,
		COAL,
		DIAMOND,
		EMERALD,
		CLAY,
		WATER,
		NONE;

		public static final Codec<LeverType> CODEC = StringIdentifiable.createCodec(LeverType::values);
		private final Map<DungeonInstance, Integer> instances = new HashMap<>();
		private long lastInteraction = -1;

		public static void remove(DungeonInstance dungeonInstance) {
			for (LeverType value : values()) {
				value.instances.remove(dungeonInstance);
			}
		}

		public Optional<Integer> get(DungeonInstance dungeonInstance) {
			return Optional.ofNullable(this.instances.get(dungeonInstance));
		}

		public void increment(DungeonInstance dungeonInstance) {
			this.instances.put(dungeonInstance, this.get(dungeonInstance).map(i -> i + 1).orElse(1));
		}


		public Optional<BlockPos> getLeverPosition(BlockPos chestPosition, Direction puzzleDirection) {
			if (chestPosition == null || puzzleDirection == null) {
				return Optional.empty();
			}
			if (this == WATER) {
				return Optional.ofNullable(chestPosition.offset(puzzleDirection.getOpposite(), 17).up(4));
			} else {
				int offset = this.ordinal() % 3 * 5;
				boolean left = this.ordinal() < 3;
				Direction site = left ? puzzleDirection.rotateYCounterclockwise() : puzzleDirection.rotateYClockwise();
				return Optional.ofNullable(chestPosition.up(5)
						.offset(puzzleDirection.getOpposite(), 2 + offset)
						.offset(site, 5));
			}
		}

		@Override
		public String asString() {
			return String.valueOf(this.ordinal());
		}
	}
}
