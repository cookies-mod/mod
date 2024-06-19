package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.accessors.SlotAccessor;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.sound.SoundUtils;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

/**
 * Adds more functionality to the recipe screen.
 */
public class ModifyRecipeScreen {

    @SuppressWarnings("MissingJavadoc")
    public ModifyRecipeScreen() {
        final ItemStack itemStack = new ItemStack(Items.DIAMOND_PICKAXE);
        itemStack.set(DataComponentTypes.CUSTOM_NAME,
            Text.literal("Set craft helper item").styled(style -> style.withColor(
                Constants.SUCCESS_COLOR).withItalic(false)));
        itemStack.set(DataComponentTypes.LORE, new LoreComponent(Stream.of(
                Text.literal("Set the recipe as the selected"),
                Text.literal("craft helper item!")
            ).map(it -> it.formatted(Formatting.GRAY).styled(style -> style.withItalic(false)))
            .map(Text.class::cast).toList()));
        itemStack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS,
            new AttributeModifiersComponent(Collections.emptyList(), false));
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GenericContainerScreen genericContainerScreen) {
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
                        SlotAccessor.setItem(slot, itemStack);
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
