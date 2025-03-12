package codes.cookies.mod.features.misc.utils;

import codes.cookies.mod.config.categories.CraftHelperCategory;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.dev.BackedReference;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.items.types.MiscDataComponentTypes;
import codes.cookies.mod.utils.minecraft.SoundUtils;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

/**
 * Adds more functionality to the recipe screen.
 */
public class ModifyRecipeScreen extends InventoryModifier {
	public static ItemStack CRAFT_HELPER_SELECT;

	static {
		CRAFT_HELPER_SELECT =
				new ItemBuilder(Items.DIAMOND_PICKAXE).setName(TextUtils.translatable(
								TranslationKeys.CRAFT_HELPER,
								Constants.SUCCESS_COLOR))
						.setLore(
								TextUtils.translatable(TranslationKeys.CRAFT_HELPER_LINE_1, Formatting.GRAY),
								TextUtils.translatable(TranslationKeys.CRAFT_HELPER_LINE_2, Formatting.GRAY),
								Text.empty(),
								TextUtils.translatable(TranslationKeys.LEFT_CLICK_TO_SET, Formatting.YELLOW)
										.append("!"),
								TextUtils.translatable(TranslationKeys.RIGHT_CLICK_TO_EDIT, Formatting.YELLOW)
										.append("!"))
						.set(MiscDataComponentTypes.CRAFT_HELPER_MODIFIED, Unit.INSTANCE)
						.hideAdditionalTooltips()
						.set(DataComponentTypes.TOOL, null)
						.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, null)
						.build();
	}

	private RepositoryItem current;

	@SuppressWarnings("MissingJavadoc")
	public ModifyRecipeScreen() {
		super(
				CRAFT_HELPER_SELECT, "cookies-regex:.*Recipe",
				new BackedReference<>(
						() -> CraftHelperCategory.enable,
						newValue -> CraftHelperCategory.enable = newValue
				), new BackedReference<>(
						() -> CraftHelperCategory.slot,
						newValue -> CraftHelperCategory.slot = newValue
				)
		);
	}

	@Override
	protected void onItem(int slot, ItemStack item) {
		if (slot != 25) {
			return;
		}

		this.current = ItemUtils.getData(item, CookiesDataComponentTypes.REPOSITORY_ITEM);
	}

	@Override
	protected boolean shouldInstrument(int clicked) {
		return super.shouldInstrument(clicked) && Screen.hasControlDown();
	}

	@Override
	protected void clicked(int button) {
		SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f);
		CraftHelperManager.pushNewCraftHelperItem(this.current, 1);
	}

	@Override
	protected ComponentType<?> getModifiedComponentType() {
		return MiscDataComponentTypes.CRAFT_HELPER_MODIFIED;
	}
}
