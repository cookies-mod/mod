package codes.cookies.mod.utils.skyblock;

import codes.cookies.mod.repository.RepositoryItem;

import codes.cookies.mod.repository.recipes.ForgeRecipe;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;

/**
 * Some utility methods related to the forge.
 */
public class ForgeUtils {

	/**
	 * Gets the starting time based on the item and the remaining time.
	 * @param timeRemaining The remaining time.
	 * @param item The item.
	 * @return The start time.
	 */
	public static long getStartTimeSeconds(String timeRemaining, RepositoryItem item) {
		if (timeRemaining == null || item == null) {
			return -1;
		}

		if (item.getRecipes().isEmpty()) {
			return -1;
		}

		final long playerDuration = getForgeTime(item);
		final long timeRemainingSeconds = getTimeRemainingInSeconds(timeRemaining);
		final long timePassed = playerDuration - timeRemainingSeconds;

		return (System.currentTimeMillis() / 1000) - timePassed;
	}

	/**
	 * Gets the remaining time from the string.
	 * @param timeRemaining The remaining time.
	 * @return The remaining time in seconds.
	 */
	public static long getTimeRemainingInSeconds(String timeRemaining) {
		final Matcher matcher =
				Pattern.compile("(?:(\\d+)d ?)?(?:(\\d+)h ?)?(?:(\\d+)m ?)?(?:(\\d+)s)?").matcher(timeRemaining);
		if (!matcher.find()) {
			return -1;
		}

		long days = longOrZero(matcher.group(1));
		long hours = longOrZero(matcher.group(2)) + days * 24;
		long minutes = longOrZero(matcher.group(3)) + hours * 60;

		return longOrZero(matcher.group(4)) + minutes * 60;
	}

	/**
	 * Gets the forge time of the provided item.
	 * @param repositoryItem The item.
	 * @return The forge time.
	 */
	public static long getForgeTime(RepositoryItem repositoryItem) {
		if (repositoryItem == null) {
			return -1L;
		}
		final Optional<ForgeRecipe> first = repositoryItem.getRecipes()
				.stream()
				.filter(ForgeRecipe.class::isInstance)
				.map(ForgeRecipe.class::cast)
				.findFirst();
		if (first.isEmpty()) {
			return -1L;
		}

		final ForgeRecipe recipe = first.get();
		return recipe.getPlayerDuration();
	}

	/**
	 * Parses the long or returns zero.
	 * @param number The number to parse.
	 * @return The number or zero.
	 */
	private static long longOrZero(String number) {
		return ExceptionHandler.removeThrowsSilent(() -> Long.parseLong(number), 0L);
	}

	/**
	 * Extracts the time remaining text from the lore.
	 * @param lore The item lore.
	 * @return The text.
	 */
	@Nullable
	public static String extractTimeRemaining(List<Text> lore) {
		for (Text text : lore) {
			String content = text.getString();
			if (content.startsWith("Time Remaining: ")) {
				return content.substring(16);
			}
		}

		return null;
	}
}
