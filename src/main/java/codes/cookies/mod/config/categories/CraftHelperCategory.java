package codes.cookies.mod.config.categories;

import java.util.ArrayList;
import java.util.List;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.config.categories.objects.CraftHelperSourceObjects;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperLocation;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperPlacement;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;


@Category("craft_helper")
@ConfigInfo(title = "Craft Helper", description = "Craft helper settings :3")
public class CraftHelperCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_HELPERS_CRAFT_HELPER_SETTING)
	@ConfigEntry(id = "enabled")
	public static boolean enable = true;

	@CookiesOptions.Button(value = TranslationKeys.CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS, buttonText = TranslationKeys.CONFIG_HELPERS_CRAFT_HELPER_LOCATIONS_BUTTON)
	@ConfigButton(text = "")
	public static final Runnable button = CraftHelperCategory::openCraftHelperLocationEditor;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_HELPERS_CRAFT_HELPER_SOURCES)
	@ConfigEntry(id = "sources")
	public static final CraftHelperSourceObjects sources = new CraftHelperSourceObjects();

	@ConfigEntry(id = "slot")
	@ConfigOption.Hidden
	public static int slot = 14;

	@ConfigEntry(id = "location")
	@ConfigOption.Hidden
	public static Observable<CraftHelperLocation> location = Observable.of(CraftHelperLocation.RIGHT_INVENTORY);


	public static List<ItemSources> getSources() {
		List<ItemSources> list = new ArrayList<>();
		if (sources.chests) {
			list.add(ItemSources.CHESTS);
		}
		if (sources.storage) {
			list.add(ItemSources.STORAGE);
		}
		if (sources.sacks) {
			list.add(ItemSources.SACKS);
		}
		if (sources.inventory) {
			list.add(ItemSources.INVENTORY);
		}
		if (sources.forge) {
			list.add(ItemSources.FORGE);
		}
		if (sources.vault) {
			list.add(ItemSources.VAULT);
		}
		if (sources.sackOfSacks) {
			list.add(ItemSources.SACK_OF_SACKS);
		}
		if (sources.potionBag) {
			list.add(ItemSources.POTION_BAG);
		}
		if (sources.accessoryBag) {
			list.add(ItemSources.ACCESSORY_BAG);
		}
		return list;
	}

	public static void openCraftHelperLocationEditor() {
		CookiesMod.openScreen(new CraftHelperPlacement());
	}

}
