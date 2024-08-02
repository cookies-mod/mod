package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.config.ConfigKey;
import dev.morazzer.cookies.mod.config.ConfigKeys;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;
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
    private static final ConfigKey<Integer> CRAFT_HELPER_SLOT = ConfigKeys.HELPER_CRAFT_HELPER_SLOT;
    private static final ConfigKey<Boolean> CRAFT_HELPER = ConfigKeys.HELPER_CRAFT_HELPER;
    public static ItemStack CRAFT_HELPER_SELECT;

    static {
        CRAFT_HELPER_SELECT = new ItemBuilder(Items.DIAMOND_PICKAXE).setName(TextUtils.literal("Set craft helper item",
                Constants.SUCCESS_COLOR))
            .setLore(TextUtils.literal("Set the recipe as the selected", Formatting.GRAY),
                TextUtils.literal("craft helper item!", Formatting.GRAY),
                Text.empty(),
                TextUtils.literal("Left-click to set!", Formatting.YELLOW),
                TextUtils.literal("Right-click to move!", Formatting.YELLOW))
            .set(MiscDataComponentTypes.CRAFT_HELPER_MODIFIED, Unit.INSTANCE)
            .hideAdditionalTooltips()
            .set(DataComponentTypes.TOOL, null)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, null)
            .build();
    }

    private RepositoryItem current;

    @SuppressWarnings("MissingJavadoc")
    public ModifyRecipeScreen() {
        super(CRAFT_HELPER_SELECT, "cookies-regex:.*Recipe", CRAFT_HELPER, CRAFT_HELPER_SLOT);
    }

    @Override
    protected void onItem(int slot, ItemStack item) {
        if (slot != 25) {
            return;
        }

        this.current = ItemUtils.getData(item, CookiesDataComponentTypes.REPOSITORY_ITEM);
    }

    @Override
    protected void clicked() {
        SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f);
        CraftHelper.setSelectedItem(this.current);
    }

    @Override
    protected ComponentType<?> getModifiedComponentType() {
        return MiscDataComponentTypes.CRAFT_HELPER_MODIFIED;
    }
}
