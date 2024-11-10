package codes.cookies.mod.features.misc.items;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.sub.StorageData;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.exceptions.ExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;

/**
 * Saves the content of the storage (enderchest/backpack) to the {@link ProfileData}.
 */
public class StorageTracker {

    private static final String LOGGER_KEY = "StorageTracker";
	private String lastTitle;

    @SuppressWarnings("MissingJavadoc")
    public StorageTracker() {
        ScreenEvents.AFTER_INIT.register(this::afterInitScreen);
    }


    private void afterInitScreen(MinecraftClient minecraftClient, Screen screen, int scaledWidth, int scaledHeight) {
        if (!(screen instanceof GenericContainerScreen genericContainerScreen)) {
            return;
        }
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return;
        }
        if (!genericContainerScreen.getTitle().getString().startsWith("Ender Chest (") &&
            !genericContainerScreen.getTitle().getString().contains("Backpack§r (Slot #")) {
            return;
        }
		this.lastTitle = genericContainerScreen.getTitle().getString();
        ScreenEvents.remove(screen).register(ExceptionHandler.wrap(this::remove));
    }

    private void remove(Screen screen) {
        GenericContainerScreen genericContainerScreen = (GenericContainerScreen) screen;
        DevUtils.log(LOGGER_KEY, "Registered removing of storage inventory!");
        List<Pair<Integer, ItemStack>> slots = new ArrayList<>();
        for (Slot slot : genericContainerScreen.getScreenHandler().slots) {
            if (slot.inventory == MinecraftClient.getInstance().player.getInventory()) {
                continue;
            }

            if (!slot.hasStack() && (slot.getIndex() + 1) % 9 != 0) {
                continue;
            }

            if (slot.getIndex() <= 8) {
                continue;
            }

            slots.add(new Pair<>(slot.getIndex() - 9, slot.getStack()));
        }

        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            return;
        }

        final String literalTitle = this.lastTitle;
		this.lastTitle = null;
        boolean isEnderChest = literalTitle.startsWith("Ender Chest");

        final int page;
        if (isEnderChest) {
            page = Integer.parseInt(literalTitle.substring(13, 14));
        } else {
            page = Integer.parseInt(literalTitle.replaceAll("\\D", ""));
        }

        currentProfile.get()
            .getStorageData()
            .saveItems(slots,
                page - 1,
                isEnderChest ? StorageData.StorageLocation.ENDER_CHEST : StorageData.StorageLocation.BACKPACK);
    }


}
