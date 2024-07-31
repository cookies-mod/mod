package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.ConfigKey;
import dev.morazzer.cookies.mod.config.ConfigKeys;
import dev.morazzer.cookies.mod.events.InventoryEvents;
import dev.morazzer.cookies.mod.events.api.InventoryContentUpdateEvent;
import dev.morazzer.cookies.mod.screen.inventory.ForgeRecipeOverviewScreen;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.types.MiscDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.InventoryUtils;
import dev.morazzer.cookies.mod.utils.skyblock.SelectSlotInventory;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanMutablePair;
import java.util.function.Predicate;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Adds the forge recipe item to the recipe book inventory.
 */
public class ForgeRecipes {
    private static final ConfigKey<Integer> FORGE_SLOT = ConfigKeys.MISC_FORGE_RECIPE_SLOT;
    private static final ConfigKey<Boolean> FORGE_RECIPE = ConfigKeys.MISC_FORGE_RECIPE;
    private static final ItemStack FORGE_STACK;

    static {
        FORGE_STACK = new ItemBuilder(Items.LAVA_BUCKET).setName(TextUtils.literal("Forge Recipes", Formatting.GREEN))
            .setLore(TextUtils.literal("View all of the Forge Recipes!", Formatting.GRAY),
                Text.empty(),
                TextUtils.literal("Left-click to view!", Formatting.YELLOW),
                TextUtils.literal("Right-click to move!", Formatting.YELLOW))
            .set(MiscDataComponentTypes.FORGE_RECIPES_MODIFIED, true)
            .build();
    }

    BooleanBooleanMutablePair couldPlace = new BooleanBooleanMutablePair(false, false);

    public ForgeRecipes() {
        InventoryEvents.beforeInit("Recipe Book", this::shouldInstrument, this::afterInit);
    }


    private boolean shouldInstrument(HandledScreen<?> handledScreen) {
        return FORGE_RECIPE.get();
    }

    private void afterInit(HandledScreen<?> handledScreen) {
        InventoryContentUpdateEvent.register(handledScreen.getScreenHandler(), this::updateContents);
    }

    private void updateContents(int slot, ItemStack item) {
        if (slot > 53 || item == null || item.isEmpty()) {
            return;
        }

        if (slot != FORGE_SLOT.get() && couldPlace.firstBoolean()) {
            return;
        }

        if (slot == FORGE_SLOT.get() || couldPlace.secondBoolean()) {
            if (InventoryUtils.isSkyblockUiElement(item)) {
                set(item);
            } else {
                couldPlace.second(true);
            }
        }
    }

    private void set(ItemStack itemStack) {
        final ItemStack forgeStack = FORGE_STACK.copy();
        forgeStack.set(CookiesDataComponentTypes.ITEM_CLICK_CONSUMER, InventoryUtils.wrapWithSound(this::click));
        itemStack.set(CookiesDataComponentTypes.OVERRIDE_ITEM, forgeStack);
        couldPlace.first(true);
        couldPlace.second(false);
    }

    private void click(int button) {
        if (button == 1) {
            new SelectSlotInventory(this.getItemPredicate().or(InventoryUtils.isSkyblockUiElement()),
                this::selectNewSlot);
        } else {
            CookiesMod.openScreen(new ForgeRecipeOverviewScreen());
        }
    }

    private Predicate<ItemStack> getItemPredicate() {
        return this::itemPredicate;
    }

    private void selectNewSlot(Slot slot) {
        FORGE_SLOT.set(slot.getIndex());
        this.set(slot.getStack());
    }

    private boolean itemPredicate(ItemStack itemStack) {
        final Boolean data = ItemUtils.getData(itemStack, MiscDataComponentTypes.FORGE_RECIPES_MODIFIED);
        if (data == null) {
            return false;
        }

        return data;
    }
}
