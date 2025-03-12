package codes.cookies.mod.config.categories.objects;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;

@ConfigObject
public class CraftHelperSourceObjects {

	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_CHEST)
	@ConfigEntry(id = "chests")
	public boolean chests = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_STORAGE)
	@ConfigEntry(id = "storage")
	public boolean storage = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_SACK)
	@ConfigEntry(id = "sacks")
	public boolean sacks = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_INVENTORY)
	@ConfigEntry(id = "inventory")
	public boolean inventory = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_FORGE)
	@ConfigEntry(id = "forge")
	public boolean forge = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_VAULT)
	@ConfigEntry(id = "vault")
	public boolean vault = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_SACK_OF_SACKS)
	@ConfigEntry(id = "sack_of_sacks")
	public boolean sackOfSacks = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_POTION_BAG)
	@ConfigEntry(id = "potion_bag")
	public boolean potionBag = true;
	@CookiesOptions.Translatable(TranslationKeys.ITEM_SOURCE_ACCESSORY_BAG)
	@ConfigEntry(id = "accessory_bag")
	public boolean accessoryBag = true;

}
