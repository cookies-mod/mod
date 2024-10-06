package dev.morazzer.cookies.mod.features.dungeons.solver.puzzle;

import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;

import dev.morazzer.cookies.mod.render.types.BlockHighlight;
import dev.morazzer.cookies.mod.utils.cookies.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ThreeWeirdosPuzzleSolver extends PuzzleSolver {
	private static final Pattern PATTERN = Pattern.compile("\\[NPC] (\\w+): (.+)");
	private static final List<String> CORRECT_MESSAGES = Arrays.asList("The reward is not in my chest!",
			"At least one of them is lying, and the reward is not in \\w+'s chest.?",
			"My chest doesn't have the reward\\. We are all telling the truth.?",
			"My chest has the reward and I'm telling the truth!",
			"The reward isn't in any of our chests.?",
			"Both of them are telling the truth\\. Also, \\w+ has the reward in their chest.?");
	private static final List<BlockPos> DIRECTIONS = Arrays.asList(new BlockPos(0, 0, 1),
			new BlockPos(1, 0, 0),
			new BlockPos(0, 0, -1),
			new BlockPos(-1, 0, 0));

	private final Set<String> solved = new HashSet<>();

	public ThreeWeirdosPuzzleSolver() {
		super(DungeonConfig.getInstance().puzzleFoldable.threeWeirdos);
	}

	@Override
	protected void onRoomEnter(DungeonRoom dungeonRoom) {
		if (this.isDisabled()) {
			return;
		}
		super.onRoomEnter(dungeonRoom);
	}

	@Override
	protected void onRoomExit() {
		if (this.isDisabled()) {
			return;
		}
		super.onRoomExit();
	}

	@Override
	public void onChatMessage(String literalMessage) {
		if (this.isDisabled()) {
			return;
		}
		final Matcher matcher = PATTERN.matcher(literalMessage);

		if (!matcher.find()) {
			return;
		}

		String npc = matcher.group(1);
		String message = matcher.group(2);
		boolean isCorrect = CORRECT_MESSAGES.stream().anyMatch(message::matches);

		if (this.solved.contains(npc)) {
			return;
		}

		this.findBlockPos(npc).ifPresent(blockPos -> this.addHighlight(blockPos, isCorrect, npc));
	}

	private Optional<BlockPos> findBlockPos(String name) {
		if (MinecraftClient.getInstance().world == null) {
			return Optional.empty();
		}
		for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
			if (!(entity instanceof ArmorStandEntity)) {
				continue;
			}
			if (entity.squaredDistanceTo(MinecraftClient.getInstance().player) > 25) {
				continue;
			}
			final String string = Optional.ofNullable(entity.getDisplayName()).map(Text::getString).orElse("");
			if (string.equals(name)) {
				return Optional.ofNullable(entity.getBlockPos());
			}
		}
		return Optional.empty();
	}

	private void addHighlight(BlockPos center, boolean correct, String npc) {
		for (BlockPos direction : DIRECTIONS) {
			final BlockPos add = center.add(direction);
			if (MinecraftClient.getInstance().world == null) {
				return;
			}
			if (MinecraftClient.getInstance().world.getBlockState(add).isOf(Blocks.CHEST)) {
				this.solved.add(npc);
				final BlockHighlight blockHighlight =
						new BlockHighlight(add, correct ? Constants.SUCCESS_COLOR : Constants.FAIL_COLOR);
				this.addRenderable(blockHighlight);
				return;
			}
		}
	}

	@Override
	protected void resetPuzzle() {
		this.clearRenderables();
		this.solved.clear();
	}
}
