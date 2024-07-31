package dev.morazzer.cookies.mod.screen.inventory;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.repository.Ingredient;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.ForgeRecipe;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.InventoryUtils;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ClientSideInventory;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.Position;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * An overview of all forge recipes that the mod knows of.
 */
public class ForgeRecipeOverviewScreen extends ClientSideInventory {

    private static final Position LEFT_ARROW = new Position(5, 0);
    private static final Position RIGHT_ARROW = new Position(5, 8);
    private static final int ROWS = 6;

    public ForgeRecipeOverviewScreen() {
        super(Text.literal("Forge Recipes"), ROWS);
        this.inventoryContents.fillBorders(outline);

        this.initPagination(Recipe.ALL_FORGE_RECIPES.stream()
            .map(this::mapToItem)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(this::sort, String::compareTo))
            .toList(), new Position(1, 1), new Position(4, 7), null);

        this.inventoryContents.set(new Position(0, 4),
            new ItemBuilder(Items.LAVA_BUCKET).setName(TextUtils.literal("Forge Recipes", Formatting.GREEN))
                .setLore(TextUtils.literal("View all of the Forge Recipes!", Formatting.GRAY))
                .hideAdditionalTooltips()
                .build());

        this.inventoryContents.set(new Position(5, 4),
            new ItemBuilder(Items.BARRIER).hideTooltips()
                .setName(TextUtils.literal("Close", Formatting.RED))
                .setClickRunnable(InventoryUtils.wrapWithSound(this::close))
                .hideAdditionalTooltips()
                .build());

        this.inventoryContents.set(new Position(5, 3),
            new ItemBuilder(Items.ARROW).setName(TextUtils.literal("Go Back", Formatting.GREEN))
                .setLore(TextUtils.literal("To Recipe Book", Formatting.GRAY))
                .setClickRunnable(this::openRecipeBook)
                .build());
    }

    private ItemStack mapToItem(ForgeRecipe forgeRecipe) {
        final Ingredient output = forgeRecipe.getOutput();
        final RepositoryItem repositoryItem = output.getRepositoryItem();
        if (repositoryItem == null) {
            return null;
        }
        final ItemStack itemStack = repositoryItem.constructItemStack();
        final List<Text> texts = itemStack.get(CookiesDataComponentTypes.CUSTOM_LORE);
        if (texts == null) {
            return null;
        }
        final ArrayList<Text> lore = new ArrayList<>(texts);
        lore.add(Text.empty());
        lore.add(Text.literal("Click to view!").formatted(Formatting.YELLOW));
        itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE, lore);
        itemStack.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE,
            InventoryUtils.wrapWithSound(this.itemClicked(forgeRecipe)));
        return itemStack;
    }

    private String sort(ItemStack itemStack) {
        return itemStack.getName().getString();
    }

    private void openRecipeBook() {
        MinecraftClient.getInstance().player.networkHandler.sendCommand("recipebook");
    }

    private Runnable itemClicked(ForgeRecipe forgeRecipe) {
        return () -> CookiesMod.openScreen(new ForgeRecipeScreen(forgeRecipe, this));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void paginationUpdate() {
        super.paginationUpdate();
        if (this.pagination.hasPreviousPage()) {
            this.inventoryContents.set(LEFT_ARROW,
                new ItemBuilder(Items.ARROW).setName(TextUtils.literal("Previous Page", Formatting.GREEN))
                    .setLore(TextUtils.literal("Page %s".formatted(this.pagination.getCurrentPage() - 1),
                        Formatting.YELLOW))
                    .setClickRunnable(InventoryUtils.wrapWithSound(this.pagination::previousPage))
                    .build());
        } else {
            this.inventoryContents.set(LEFT_ARROW, outline);
        }
        if (this.pagination.hasNextPage()) {
            this.inventoryContents.set(RIGHT_ARROW,
                new ItemBuilder(Items.ARROW).setName(TextUtils.literal("Next Page", Formatting.GREEN))
                    .setLore(TextUtils.literal("Page %s".formatted(this.pagination.getCurrentPage() + 1),
                        Formatting.YELLOW))
                    .setClickRunnable(InventoryUtils.wrapWithSound(this.pagination::nextPage))
                    .build());
        } else {

            this.inventoryContents.set(RIGHT_ARROW, outline);
        }

        this.inventoryTitle = Text.literal("(%s/%s) Forge Recipes".formatted(this.pagination.getCurrentPage(),
            this.pagination.getMaxPage())).formatted(Formatting.DARK_GRAY);
    }
}
