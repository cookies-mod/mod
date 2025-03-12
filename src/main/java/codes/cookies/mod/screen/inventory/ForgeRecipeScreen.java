package codes.cookies.mod.screen.inventory;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.categories.CraftHelperCategory;
import codes.cookies.mod.features.misc.utils.ModifyRecipeScreen;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.ForgeRecipe;
import codes.cookies.mod.repository.recipes.Recipe;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.InventoryUtils;
import codes.cookies.mod.utils.skyblock.inventories.ClientSideInventory;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import codes.cookies.mod.utils.skyblock.inventories.Position;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

/**
 * A specific forge recipe view, the screen adapts based on the parameters passed to the constructor.
 */
public class ForgeRecipeScreen extends ClientSideInventory {
    private static final int ROWS = 6;
    private final ForgeRecipe recipe;
    private final Screen previous;

    public ForgeRecipeScreen(ForgeRecipe recipe, Screen previous) {
        super(getName(recipe), ROWS);
        this.recipe = recipe;
        this.previous = previous;
        this.inventoryContents.fill(outline);
        this.setItems();

        this.inventoryContents.set(new Position(5, 4),
            new ItemBuilder(Items.BARRIER)
                .setName(TextUtils.translatable("mco.selectServer.close", Formatting.RED))
                .setClickRunnable(InventoryUtils.wrapWithSound(this::close))
                .hideAdditionalTooltips()
                .build());

        this.inventoryContents.set(new Position(1, 4),
            new ItemBuilder(Items.LAVA_BUCKET).setName(TextUtils.translatable(TranslationKeys.SCREEN_FORGE_RECIPE_OVERVIEW_RECIPE, Formatting.GREEN)).setLore(
                TextUtils.literal("<-- ", Formatting.WHITE)
                    .append(TextUtils.literal("Required items", Formatting.YELLOW)),
                TextUtils.literal("      Result item ", Formatting.YELLOW)
                    .append(TextUtils.literal(" -->", Formatting.WHITE))).build());

        if (CraftHelperCategory.enable) {
            final ItemStack craftHelperSelect = ModifyRecipeScreen.CRAFT_HELPER_SELECT.copy();
            craftHelperSelect.set(
                CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE,
                InventoryUtils.wrapWithSound(this::setSelected, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f));
            this.inventoryContents.set(new Position(3, 4), craftHelperSelect);
        }

        final ItemBuilder goBack = new ItemBuilder(Items.ARROW).setName(TextUtils.translatable("dataPack.validation.back", Formatting.GREEN))
            .setClickRunnable(InventoryUtils.wrapWithSound(this::openForgeRecipes));
        if (previous instanceof ForgeRecipeOverviewScreen) {
            goBack.setLore(TextUtils.translatable(TranslationKeys.SCREEN_FORGE_RECIPE_OVERVIEW_BACK_TO_FORGE_RECIPES, Formatting.GRAY));
        } else if (previous instanceof ForgeRecipeScreen forgeRecipeScreen) {
            goBack.setLore(TextUtils.literal("To ", Formatting.GRAY)
                .append(getName(forgeRecipeScreen.recipe).formatted(Formatting.GRAY)));
        } else {
            return;
        }
        this.inventoryContents.set(new Position(5, 3), goBack.build());
    }

    private static MutableText getName(ForgeRecipe recipe) {
        return recipe.getOutput()
            .getRepositoryItem()
            .getName()
            .copyContentOnly()
            .append(" Recipe")
            .formatted(Formatting.DARK_GRAY);
    }

    private void setItems() {
        for (int i = 0; i < this.recipe.getIngredients().length; i++) {
            int row = i / 2;
            final Ingredient ingredient = this.recipe.getIngredients()[i];
            final Position position = new Position(1 + row, 1 + i % 2);
            final ItemStack asItem = ingredient.getAsItem();
            if (ingredient.getRepositoryItem() != null) {
                final RepositoryItem repositoryItem = ingredient.getRepositoryItem();
                final Set<Recipe> recipes = repositoryItem.getRecipes();
                if (recipes.stream().anyMatch(ForgeRecipe.class::isInstance)) {
                    final Optional<ForgeRecipe> first =
                        recipes.stream().filter(ForgeRecipe.class::isInstance).map(ForgeRecipe.class::cast).findFirst();
                    final ForgeRecipe forgeRecipe = first.orElseThrow();
                    asItem.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE,
                        InventoryUtils.wrapWithSound(this.clickedForgeRecipe(forgeRecipe)));
                } else if (!recipes.isEmpty()) {
                    asItem.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE,
                        InventoryUtils.wrapWithSound(this.clickedNonForge(repositoryItem.getInternalId())));
                }
            }
            this.inventoryContents.set(position, asItem);
        }

        this.inventoryContents.set(new Position(1, 7),
            this.recipe.getOutput().getRepositoryItem().constructItemStack());
    }

    private void setSelected() {
        CraftHelperManager.pushNewCraftHelperItem(this.recipe.getOutput().getRepositoryItemNotNull(), 1);
    }

    private void openForgeRecipes() {
        CookiesMod.openScreen(previous);
    }

    private Runnable clickedForgeRecipe(ForgeRecipe forgeRecipe) {
        return () -> CookiesMod.openScreen(new ForgeRecipeScreen(forgeRecipe, this));
    }

    private Runnable clickedNonForge(String internalId) {
        return () -> MinecraftClient.getInstance().player.networkHandler.sendCommand("viewrecipe %s".formatted(
            internalId.toUpperCase()));
    }
}
