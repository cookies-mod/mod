package codes.cookies.mod.config.categories;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.config.categories.objects.TimerObjects;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

@Category(value = "misc_config", categories = PartyCommands.class)
@ConfigInfo(title = "Misc Config", description = "Random stuff")
public class MiscCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_ENABLE_SCROLL_TOOLTIPS)
	@ConfigEntry(id = "scrollable_tooltips")
	public static boolean enableScrollableTooltips = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SIGN_EDIT_ENTER_SUBMITS)
	@ConfigEntry(id = "sign_enter_submits")
	public static boolean signEditEnterSubmits = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_STORAGE_PREVIEW)
	@ConfigEntry(id = "enable_storage_preview")
	public static boolean enableStoragePreview = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_PING)
	@ConfigEntry(id = "show_ping")
	public static boolean showPing = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_MUSEUM_ARMOR_SETS)
	@ConfigEntry(id = "show_museum_armor_sets",  translation = TranslationKeys.CONFIG_MISC_SHOW_MUSEUM_ARMOR_SETS + ".name")
	public static boolean showMuseumArmorSets = true;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_NOTIFICATIONS_PRIMAL_FEAR)
	@ConfigEntry(id = "primal_fear")
	public static final TimerObjects primalFear = new TimerObjects();

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_MISC_CATEGORIES_ITEMS)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_ITEM_CREATION_DATE)
	@ConfigEntry(id = "item_creation_date")
	public static boolean showItemCreationDate = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_ITEM_NPC_VALUE)
	@ConfigEntry(id = "item_npc_value")
	public static boolean showItemNpcValue = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_MISC_CATEGORIES_RENDER)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_OWN_ARMOR)
	@ConfigEntry(id = "hide_own_armour")
	public static boolean hideOwnArmour = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_OTHER_ARMOR)
	@ConfigEntry(id = "hide_other_armour")
	public static boolean hideOtherArmour = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_DYE_ARMOR)
	@ConfigEntry(id = "show_dyed_armour")
	public static boolean showDyedArmor = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_FIRE_ON_ENTITIES)
	@ConfigEntry(id = "hide_fire_on_entities")
	public static boolean hideFireOnEntities = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_LIGHTNING_BOLT)
	@ConfigEntry(id = "hide_lightning_bolt")
	public static boolean hideLightningBolt = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_MISC_CATEGORIES_RENDER_UI)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_POTION_EFFECTS)
	@ConfigEntry(id = "hide_potion_effects")
	public static boolean hidePotionEffects = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_HEALTH)
	@ConfigEntry(id = "hide_health")
	public static boolean hideHealth = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_ARMOR)
	@ConfigEntry(id = "hide_armour")
	public static boolean hideArmour = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_HIDE_FOOD)
	@ConfigEntry(id = "hide_food")
	public static boolean hideFood = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_MISC_CATEGORIES_RENDER_INVENTORY)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_PET_LEVEL)
	@ConfigEntry(id = "show_pet_level")
	public static boolean showPetLevelAsStackSize = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_PET_RARITY_IN_LEVEL_TEXT)
	@ConfigEntry(id = "show_pet_rarity_in_level_text")
	public static boolean showPetRarityInLevelText = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_ITEM_UPGRADES)
	@ConfigEntry(id = "show_item_upgrades")
	public static boolean showItemUpgrades = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_SHOW_FORGE_RECIPE_STACK)
	@ConfigEntry(id = "show_forge_recipes")
	public static boolean showForgeRecipes = false;

	@ConfigEntry(id = "forge_slot")
	@ConfigOption.Hidden
	public static int forgeSlot = 47;
}
