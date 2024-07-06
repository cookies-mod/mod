package dev.morazzer.cookies.mod.features.misc.items;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;

/**
 * Tracker to save the amount of items in the sack.
 */
public class SackInventoryTracker {
    private static final String LOGGER_KEY = "SackInventoryTracker";
    @RegExp
    private static final String GEMSTONE_PATTER = "(?:Rough|Flawed|Fine): ([\\d,]+) \\(.*\\)";

    public SackInventoryTracker() {
        ScreenEvents.AFTER_INIT.register(this::afterInitScreen);
    }

    private void afterInitScreen(MinecraftClient minecraftClient, Screen screen, int scaledWidth, int scaledHeight) {
        if (!(screen instanceof GenericContainerScreen genericContainerScreen)) {
            return;
        }
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return;
        }
        if (!genericContainerScreen.getTitle().getString().contains("Sack")) {
            return;
        }
        if (genericContainerScreen.getTitle().getString().equalsIgnoreCase("Sack of Sacks")) {
            return;
        }
        ScreenEvents.remove(screen).register(ExceptionHandler.wrap(this::remove));
    }

    private void remove(Screen screen) {
        GenericContainerScreen genericContainerScreen = (GenericContainerScreen) screen;
        DevUtils.log(LOGGER_KEY, "Registered removing of sack inventory!");
        for (Slot slot : genericContainerScreen.getScreenHandler().slots) {
            if (slot.inventory == MinecraftClient.getInstance().player.getInventory()) {
                continue;
            }

            if (!slot.hasStack()) {
                continue;
            }

            final ItemStack stack = slot.getStack();
            if (stack.getItem() == Items.BLACK_STAINED_GLASS_PANE) {
                continue;
            }

            this.saveItem(slot.getStack());
        }
    }

    private void saveItem(ItemStack stack) {
        final Optional<? extends LoreComponent> loreComponent =
            stack.getComponentChanges().get(DataComponentTypes.LORE);

        // Minecraft can return null in some cases, so even though it's an optional the null check is required
        //noinspection OptionalAssignedToNull
        if (loreComponent == null || loreComponent.isEmpty()) {
            return;
        }

        final LoreComponent lore = loreComponent.get();
        final List<Text> lines = lore.lines();
        if (lines.size() < 3) {
            DevUtils.log(LOGGER_KEY, "Item description to small, skipping!");
            return;
        }

        final Text first = lines.getFirst();

        BiConsumer<ItemStack, List<Text>> consumer = switch (first.getString()) {
            case "Gemstones" -> this::saveGemstoneSackItem;
            default -> this::saveDefaultSackItem;
        };

        if (!lines.getLast().getString().equalsIgnoreCase("Click to pickup!")
            && !lines.getLast().getString().equals("Empty sack!")) {
            return;
        }

        consumer.accept(stack, lines);
    }

    private void saveGemstoneSackItem(ItemStack stack, List<Text> lines) {
        if (lines.size() < 9) {
            return;
        }

        String rough = lines.get(2).getString().trim();
        String flawed = lines.get(3).getString().trim();
        String fine = lines.get(4).getString().trim();

        if (!rough.matches(GEMSTONE_PATTER) || !flawed.matches(GEMSTONE_PATTER) || !fine.matches(GEMSTONE_PATTER)) {
            DevUtils.log(LOGGER_KEY, "One or more don't match the regex!");
            return;
        }

        int roughAmount = Integer.parseInt(rough.replaceAll(GEMSTONE_PATTER, "$1").replaceAll("\\D", ""));
        int flawedAmount = Integer.parseInt(flawed.replaceAll(GEMSTONE_PATTER, "$1").replaceAll("\\D", ""));
        int fineAmount = Integer.parseInt(fine.replaceAll(GEMSTONE_PATTER, "$1").replaceAll("\\D", ""));

        final String internalId = ItemUtils.getData(stack, CookiesDataComponentTypes.SKYBLOCK_ID);

        ProfileStorage.getCurrentProfile().ifPresent(profileData -> {
            this.set(profileData, RepositoryItem.of(internalId), roughAmount);
            this.set(profileData, RepositoryItem.of(internalId.replace("ROUGH", "FLAWED")), flawedAmount);
            this.set(profileData, RepositoryItem.of(internalId.replace("ROUGH", "FINE")), fineAmount);
        });
    }

    private void saveDefaultSackItem(ItemStack stack, List<Text> lines) {
        final Text storedLine = lines.get(2);
        final String storedString = storedLine.getString();

        final String count = storedString.replaceAll("Stored: ([\\d,]+)/\\d+.*", "$1").replaceAll("\\D", "");

        if (count.isEmpty()) {
            DevUtils.log(LOGGER_KEY, "No count found, skipping!");
            return;
        }

        final int amount = Integer.parseInt(count);
        final RepositoryItem item = ItemUtils.getData(stack, CookiesDataComponentTypes.REPOSITORY_ITEM);

        if (item == null) {
            DevUtils.log(LOGGER_KEY, "Couldn't find item with id %s",
                ItemUtils.getData(stack, CookiesDataComponentTypes.SKYBLOCK_ID));
            return;
        }

        ProfileStorage.getCurrentProfile().ifPresent(profileData -> {
            this.set(profileData, item, amount);
        });
    }

    private void set(ProfileData profileData, RepositoryItem repositoryItem, int amount) {
        DevUtils.log(LOGGER_KEY, "Setting %s to %s", repositoryItem.getInternalId(), amount);
        profileData.getSackTracker().set(repositoryItem, amount);
    }
}
