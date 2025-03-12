package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.categories.MiscCategory;
import codes.cookies.mod.screen.inventory.ForgeRecipeOverviewScreen;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.dev.BackedReference;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Adds the forge recipe item to the recipe book inventory.
 */
public class ForgeRecipes extends InventoryModifier {
	private static final ItemStack FORGE_STACK;

	static {
		FORGE_STACK =
				new ItemBuilder(Items.LAVA_BUCKET).setName(TextUtils.translatable(
								TranslationKeys.SCREEN_FORGE_RECIPE_OVERVIEW,
								Formatting.GREEN))
						.setLore(
								TextUtils.translatable(
										TranslationKeys.SCREEN_FORGE_RECIPE_OVERVIEW_VIEW_ALL,
										Formatting.GRAY),
								Text.empty(),
								TextUtils.translatable(TranslationKeys.LEFT_CLICK_TO_VIEW, Formatting.YELLOW)
										.append("!"),
								TextUtils.translatable(TranslationKeys.RIGHT_CLICK_TO_EDIT, Formatting.YELLOW)
										.append("!"))
						.set(MiscDataComponentTypes.FORGE_RECIPES_MODIFIED, true)
						.build();
	}

	public ForgeRecipes() {
		super(
				FORGE_STACK,
				"Recipe Book",
				new BackedReference<>(
						() -> MiscCategory.showForgeRecipes,
						newValue -> MiscCategory.showForgeRecipes = newValue
				),
				new BackedReference<>(
						() -> MiscCategory.forgeSlot,
						newValue -> MiscCategory.forgeSlot = newValue
				)
		);
	}

	@Override
	protected void clicked() {
		CookiesMod.openScreen(new ForgeRecipeOverviewScreen());
	}

	@Override
	protected ComponentType<?> getModifiedComponentType() {
		return MiscDataComponentTypes.FORGE_RECIPES_MODIFIED;
	}
}
