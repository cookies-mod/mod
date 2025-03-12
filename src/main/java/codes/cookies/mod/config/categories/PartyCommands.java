package codes.cookies.mod.config.categories;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;

@ConfigInfo(title = "Party Commands")
@Category("party_commands")
public class PartyCommands {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_PT_ME)
	@ConfigEntry(id = "party_transfer")
	public static boolean partyTransfer = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_WARP)
	@ConfigEntry(id = "warp")
	public static boolean warp = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_JOIN_INSTANCE)
	@ConfigEntry(id = "join_instance")
	public static boolean joinInstance = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_COIN_FLIP)
	@ConfigEntry(id = "coin_flip")
	public static boolean coinFlip = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_DOWN_TIME)
	@ConfigEntry(id = "down_time")
	public static boolean downTime = true;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_DUNGEON_PARTY_CHAT_COMMANDS_DOWN_TIME_PARTY_MESSAGE)
	@ConfigEntry(id = "send_downtime_message")
	public static boolean sendDowntimePartyMessage = false;

	public static boolean isCommandEnabled(String command) {
		return switch (command) {
			case "ptme" -> partyTransfer;
			case "warp" -> warp;
			case "joininstance" -> joinInstance;
			case "dt" -> downTime;
			case "cf" -> coinFlip;
			default -> false;
		};
	}

}
