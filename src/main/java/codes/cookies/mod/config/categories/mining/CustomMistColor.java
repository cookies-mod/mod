package codes.cookies.mod.config.categories.mining;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@RequiredArgsConstructor
@Getter
public enum CustomMistColor {

	BLACK(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_CARPET),
	PURPLE(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_CARPET),
	MAGENTA(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_CARPET),
	PINK(Blocks.PINK_STAINED_GLASS, Blocks.PINK_CARPET),
	RED(Blocks.RED_STAINED_GLASS, Blocks.RED_CARPET),
	ORANGE(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_CARPET),
	YELLOW(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_CARPET),
	LIME(Blocks.LIME_STAINED_GLASS, Blocks.LIME_CARPET),
	GREEN(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_CARPET),
	CYAN(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_CARPET),
	LIGHT_BLUE(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_CARPET),
	BLUE(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_CARPET),
	BROWN(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_CARPET),
	GRAY(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_CARPET),
	LIGHT_GRAY(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_CARPET);


	private final Block glassBlock;
	private final Block carpetBlock;
}
