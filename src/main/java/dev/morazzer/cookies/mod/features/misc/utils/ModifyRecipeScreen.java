package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.accessors.SlotAccessor;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import java.util.concurrent.TimeUnit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

/**
 * Adds more functionality to the recipe screen.
 */
public class ModifyRecipeScreen {

    public static ItemStack CRAFT_HELPER_SELECT;
    static {
        CRAFT_HELPER_SELECT = new ItemBuilder(Items.DIAMOND_PICKAXE)
            .setName(TextUtils.literal("Set craft helper item", Constants.SUCCESS_COLOR))
            .setLore(
                TextUtils.literal("Set the recipe as the selected", Formatting.GRAY),
                TextUtils.literal("craft helper item!", Formatting.GRAY)
            ).hideAdditionalTooltips().build();
    }

    @SuppressWarnings("MissingJavadoc")
    public ModifyRecipeScreen() {

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GenericContainerScreen genericContainerScreen) {
                if (!SkyblockUtils.isCurrentlyInSkyblock()) {
                    return;
                }
                if (!ConfigManager.getConfig().helpersConfig.craftHelper.getValue()) {
                    return;
                }

                CookiesMod.getExecutorService().schedule(() -> {
                    if (screen.getTitle().getString().trim().endsWith("Recipe")) {
                        final Slot craftingTable = genericContainerScreen.getScreenHandler().slots.get(23);
                        if (craftingTable.getStack().getItem() != Items.CRAFTING_TABLE) {
                            return;
                        }
                        if (getItemOrNull(genericContainerScreen) == null) {
                            return;
                        }
                        final Slot slot = genericContainerScreen.getScreenHandler().slots.get(14);
                        SlotAccessor.setItem(slot, CRAFT_HELPER_SELECT);
                        SlotAccessor.setRunnable(slot, this.setSelectedItem(genericContainerScreen));
                    }
                }, 1, TimeUnit.SECONDS);
            }
        });
    }

    private @Nullable RepositoryItem getItemOrNull(GenericContainerScreen genericContainerScreen) {
        final Slot slot = genericContainerScreen.getScreenHandler().slots.get(25);
        final ItemStack stack = slot.getStack();
        if (stack == null) {
            return null;
        }
        return ItemUtils.getData(stack, CookiesDataComponentTypes.REPOSITORY_ITEM);
    }

    private Runnable setSelectedItem(GenericContainerScreen genericContainerScreen) {
        return () -> {
            SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f);
            RepositoryItem item = getItemOrNull(genericContainerScreen);
            CraftHelper.setSelectedItem(item);
        };
    }

}
