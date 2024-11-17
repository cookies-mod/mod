package codes.cookies.mod.utils.items;

import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Builder;

/**
 * Info about a pet.
 * @param type The type of the pet.
 * @param active Whether it is active.
 * @param exp The exp on the pet.
 * @param tier The tier of the pet.
 * @param hideInfo Whether the pet is hidden.
 * @param candyUsed How many candies have been used on the pet.
 * @param hideRightClick Whether right click should be hidden.
 * @param noMove Whether the pet is marked as no move.
 */
@Builder
public record PetInfo(String type, boolean active, double exp, RepositoryItem.Tier tier, boolean hideInfo,
					  int candyUsed, boolean hideRightClick, boolean noMove) {

	public static PetInfo EMPTY = new PetInfo("PET", false, 0.0, RepositoryItem.Tier.COMMON, false, 0, false, false);

	public static PetInfo create(String s) {
		if (s == null) {
			return EMPTY;
		}
		final JsonElement jsonElement = JsonParser.parseString(s);
		if (!jsonElement.isJsonObject()) {
			return EMPTY;
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		final PetInfoBuilder builder = PetInfo.builder();
		builder.type(EMPTY.type()).tier(EMPTY.tier());
		if (jsonObject.has("type")) {
			builder.type(jsonObject.get("type").getAsString());
		}
		if (jsonObject.has("active")) {
			builder.active(jsonObject.get("active").getAsBoolean());
		}
		if (jsonObject.has("exp")) {
			builder.exp(jsonObject.get("exp").getAsDouble());
		}
		if (jsonObject.has("tier")) {
			builder.tier(ExceptionHandler.removeThrowsSilent(
					() -> RepositoryItem.Tier.valueOf(jsonObject.get("tier").getAsString()),
					RepositoryItem.Tier.COMMON));
		}
		if (jsonObject.has("hideInfo")) {
			builder.hideInfo(jsonObject.get("hideInfo").getAsBoolean());
		}
		if (jsonObject.has("candyUsed")) {
			builder.candyUsed(jsonObject.get("candyUsed").getAsInt());
		}
		if (jsonObject.has("hideRightClick")) {
			builder.hideRightClick(jsonObject.get("hideRightClick").getAsBoolean());
		}
		if (jsonObject.has("noMove")) {
			builder.noMove(jsonObject.get("noMove").getAsBoolean());
		}

		return builder.build();
	}
}
