package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;
import dev.morazzer.cookies.mod.features.dungeons.map.PuzzleType;
import dev.morazzer.cookies.mod.render.WorldRender;
import dev.morazzer.cookies.mod.render.types.Line;
import dev.morazzer.cookies.mod.utils.accessors.GlowingEntityAccessor;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import org.joml.Vector2i;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class HigherLowerPuzzleSolver extends PuzzleSolver {
	private static final Pattern PATTERN = Pattern.compile("\\[Lv15] Blaze [\\d,]+/([\\d,]+)❤");
	private Direction direction = null;
	private int lastBlazeCount = -1;
	private Line line = null;
	private Entity first, second;
	private long enteredBlaze = -1;
	private long startedBlaze = 1;

	@Override
	protected PuzzleType getType() {
		return PuzzleType.HIGHER_LOWER;
	}

	@Override
	protected void onRoomEnter(DungeonRoom dungeonRoom) {
		if (this.direction == null) {
			this.findDirection(dungeonRoom);
		}
		if (this.enteredBlaze == -1) {
			this.enteredBlaze = System.currentTimeMillis();
		}
	}

	private void findDirection(DungeonRoom dungeonRoom) {
		final Optional<Vector2i> center = dungeonRoom.getCenter();
		if (center.isEmpty()) {
			return;
		}

		BlockPos platformLocation = center.map(vector2i -> new BlockPos(vector2i.x + 1, 118, vector2i.y)).get();

		this.direction = this.getWorld()
				.map(world -> world.getBlockState(platformLocation).isAir())
				.map(isAir -> isAir ? Direction.DOWN : Direction.UP)
				.orElse(null);
	}

	@Override
	public void tick() {
		Set<Pair<Entity, Integer>> blazes = new HashSet<>();

		this.getWorld().map(ClientWorld::getEntities).orElse(Collections.emptyList()).forEach(entity -> {
			if (!(entity instanceof ArmorStandEntity armorStandEntity)) {
				return;
			}

			Matcher matcher = PATTERN.matcher(CookiesUtils.stripColor(armorStandEntity.getName().getString()));
			if (!matcher.find()) {
				return;
			}

			String hp = matcher.group(1).replaceAll("\\D", "");
			blazes.add(new Pair<>(entity, Integer.parseInt(hp)));
		});

		Comparator<Pair<Entity, Integer>> comparator = Comparator.comparingInt(Pair::getRight);
		if (this.direction == Direction.DOWN) {
			comparator = comparator.reversed();
		}

		if (this.lastBlazeCount == blazes.size()) {
			return;
		}

		if (blazes.size() == 9) {
			this.startedBlaze = System.currentTimeMillis();
		}
		if (blazes.isEmpty()) {
			this.finishBlaze();
		}

		this.lastBlazeCount = blazes.size();
		WorldRender.removeRenderable(this.line);
		this.line = null;
		this.first = null;
		this.second = null;

		AtomicInteger count = new AtomicInteger(0);
		blazes.stream().sorted(comparator).forEach(pair -> {
			final Entity left = pair.getLeft();
			final List<Entity> otherEntities =
					left.getWorld().getOtherEntities(left, new Box(left.getBlockPos()).expand(1));
			for (Entity otherEntity : otherEntities) {
				if (otherEntity instanceof BlazeEntity blazeEntity) {
					this.highlightBlaze(blazeEntity, count.getAndIncrement());
				}
			}
		});

		if (this.first != null && this.second != null) {
			final Vec3d firstPos = this.first.getPos();
			final Vec3d secondPos = this.second.getPos();

			final Vec3d actualFirst;
			final Vec3d actualSecond;

			if (firstPos.y > secondPos.y) {
				actualFirst = firstPos;
				actualSecond = secondPos.add(0, 2, 0);
			} else {
				actualFirst = firstPos.add(0, 2, 0);
				actualSecond = secondPos;
			}

			this.line = new Line(actualFirst, actualSecond, Constants.SUCCESS_COLOR, -1);
			WorldRender.addRenderable(this.line);
		}
	}

	private void finishBlaze() {
		final long finishedAt = System.currentTimeMillis();
		final long timeFromEnter = finishedAt - this.enteredBlaze;
		final long timeFromStart = finishedAt - this.startedBlaze;

		CookiesUtils.sendSuccessMessage("Finished blaze in " + CookiesUtils.formattedMs(timeFromStart) + " (from start)");
		CookiesUtils.sendSuccessMessage("Finished blaze in " + CookiesUtils.formattedMs(timeFromEnter) + " (from enter)");
	}

	private void highlightBlaze(BlazeEntity blazeEntity, int count) {
		if (count > 1) {
			return;
		}
		if (count == 0) {
			this.first = blazeEntity;
			GlowingEntityAccessor.setGlowing(blazeEntity, true);
			GlowingEntityAccessor.setGlowColor(blazeEntity, Constants.SUCCESS_COLOR);
		} else {
			GlowingEntityAccessor.setGlowing(blazeEntity, true);
			this.second = blazeEntity;
		}
	}

	@Override
	protected void onRoomExit() {
		super.onRoomExit();
		if (this.lastBlazeCount == 10 || this.lastBlazeCount == -1) {
			this.enteredBlaze = -1;
			this.startedBlaze = -1;
		}
	}

	@Override
	protected void resetPuzzle() {
		this.direction = null;
		this.startedBlaze = -1;
		this.enteredBlaze = -1;
		this.first = null;
		this.second = null;
		WorldRender.removeRenderable(this.line);
		this.line = null;
		super.resetPuzzle();
	}
}
