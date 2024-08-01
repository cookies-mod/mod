package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.ConfigKey;
import dev.morazzer.cookies.mod.config.ConfigKeys;
import dev.morazzer.cookies.mod.screen.inventory.ForgeRecipeOverviewScreen;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Adds the forge recipe item to the recipe book inventory.
 */
public class ForgeRecipes extends InventoryModifier {
    private static final ConfigKey<Integer> FORGE_SLOT = ConfigKeys.MISC_FORGE_RECIPE_SLOT;
    private static final ConfigKey<Boolean> FORGE_RECIPE = ConfigKeys.MISC_FORGE_RECIPE;
    private static final ItemStack FORGE_STACK;

    static {
        FORGE_STACK =
            new ItemBuilder(Items.LAVA_BUCKET).setName(TextUtils.literal("Forge Recipes", Formatting.GREEN))
                .setLore(TextUtils.literal("View all of the Forge Recipes!", Formatting.GRAY),
                    Text.empty(),
                    TextUtils.literal("Left-click to view!", Formatting.YELLOW),
                    TextUtils.literal("Right-click to move!", Formatting.YELLOW))
                .set(MiscDataComponentTypes.FORGE_RECIPES_MODIFIED, true)
                .build();
    }

    public ForgeRecipes() {
        super(FORGE_STACK, "Recipe Book", FORGE_RECIPE, FORGE_SLOT);
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
