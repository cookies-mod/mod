package codes.cookies.mod.config.categories.dungeons;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@ConfigInfo(
		title = "Puzzles"
)
@Category("puzzles")
public class PuzzleCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PUZZLE_CREEPER_BEAMS_SOLVER)
	@ConfigEntry(id = "creeper_beams")
	public static Observable<Boolean> creeperBeams = Observable.of(true);
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PUZZLE_HIGHER_LOWER_SOLVER)
	@ConfigEntry(id = "higher_lower")
	public static Observable<Boolean> higherLower = Observable.of(true);
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PUZZLE_QUIZ_SOLVER)
	@ConfigEntry(id = "quiz")
	public static Observable<Boolean> quiz = Observable.of(true);
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PUZZLE_THREE_WEIRDOS_SOLVER)
	@ConfigEntry(id = "three_weirdos")
	public static Observable<Boolean> threeWeirdos = Observable.of(true);

	@CookiesOptions.Seperator(value = TranslationKeys.CONFIG_DUNGEON_PUZZLE_WATER_BOARD_CREDITS, hasDescription = true)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PUZZLE_WATER_BOARD_SOLVER)
	@ConfigEntry(id = "water_board")
	public static Observable<Boolean> waterBoard = Observable.of(true);

}
