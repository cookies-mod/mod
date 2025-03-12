package codes.cookies.mod.config.categories;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.config.categories.objects.PestTimerObject;
import codes.cookies.mod.config.data.CodecData;
import codes.cookies.mod.config.data.RancherSpeedConfig;
import codes.cookies.mod.config.data.SqueakyMousematOption;
import codes.cookies.mod.features.farming.garden.keybinds.GardenKeybindPredicate;
import codes.cookies.mod.features.farming.garden.keybinds.GardenKeybindsScreen;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

import net.minecraft.client.MinecraftClient;

import javax.swing.*;

@ConfigInfo(title = "Farming", description = "Settings related to farming and the garden")
@Category("farming_category")
public class FarmingCategory {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_SHOW_PLOT_PRICE_BREAKDOWN)
	@ConfigEntry(id = "show_plot_price_breakdown")
	public static boolean showPlotPriceBreakdown = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_YAW_PITCH_DISPLAY)
	@ConfigEntry(id = "yaw_pitch_display")
	public static boolean yawPitchDisplay = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_NOTIFICATIONS_PEST)
	@ConfigEntry(id = "pest_timer")
	public static final PestTimerObject pestTimer = new PestTimerObject();

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_FARMING_CATEGORIES_GARDEN_KEYBINDS)
	@CookiesOptions.Button(value = TranslationKeys.CONFIG_FARMING_OPEN_KEYBIND_MENU, buttonText = TranslationKeys.CONFIG_FARMING_OPEN_KEYBIND_TEXT)
	@ConfigButton(text = "")
	public static final Runnable keybindsButton = () -> {
		CookiesMod.openScreen(new GardenKeybindsScreen(MinecraftClient.getInstance().currentScreen));
	};

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_GARDEN_KEYBIND_PREDICATE)
	@ConfigEntry(id = "keybind_predicate")
	public static GardenKeybindPredicate keybindPredicate = GardenKeybindPredicate.ON_GARDEN;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_FARMING_CATEGORIES_RANCHERS)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_SHOW_RANCHER_SPEED)
	@ConfigEntry(id = "show_rancher_speed")
	public static boolean showRancherSpeed = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_SHOW_RANCHER_OPTIMAL_SPEED)
	@ConfigEntry(id = "show_rancher_optimal_speed")
	public static boolean showRancherOptimalSpeed = false;

	@ConfigOption.Hidden
	@ConfigEntry(id = "rancher_speeds")
	public static final CodecData<RancherSpeedConfig> rancherSpeed = new CodecData<>(new RancherSpeedConfig(), RancherSpeedConfig.CODEC);

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_FARMING_CATEGORIES_VISITORS)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_VISITOR_DROP_PROTECTION)
	@ConfigEntry(id = "visitor_drop_protection")
	public static boolean visitorRareDropProtection = true;

	//TODO Add select menu where you can choose what you want protected and what you dont want protected :3
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_VISITOR_NOT_AS_RARE_DROP_PROTECTION)
	@ConfigEntry(id = "visitor_common_protection")
	public static boolean visitorCommonDropProtection = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_FARMING_SQUEAKY_MOUSEMAT)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_SQUEAKY_MOUSEMAT_OVERLAY)
	@ConfigEntry(id = "mousemat_overlay")
	public static boolean mousematOverlay = false;

	@ConfigOption.Hidden
	@ConfigEntry(id = "squeaky_mousemat_data")
	public static final CodecData<SqueakyMousematOption> squeakyMousematOption = new CodecData<>(SqueakyMousematOption.createDefault(), SqueakyMousematOption.CODEC);

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_FARMING_CATEGORIES_COMPOST)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_SHOW_COMPOST_PRICE_BREAKDOWN)
	@ConfigEntry(id = "compost_price_breakdown")
	public static boolean showCompostPriceBreakdown = false;

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_COMPOST_SORT_ORDER)
	@ConfigEntry(id = "compost_sort_order")
	public static SortOrder sortOrder = SortOrder.ASCENDING;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_FARMING_CATEGORIES_JACOBS)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_HIGHLIGHT_UNCLAIMED_JACOB_CONTENTS)
	@ConfigEntry(id = "highlight_unclaimed_jacob_contests")
	public static boolean highlightUnclaimedJacobContests = false;

	@CookiesOptions.Seperator(TranslationKeys.CONFIG_FARMING_RENDER)
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_FARMING_RENDER_HIGHLIGHT_GLOWING_MUSHROOMS)
	@ConfigEntry(id = "highlight_glowing_mushrooms")
	public static boolean highlightGlowingMushrooms = false;
}
