package dev.morazzer.cookies.mod.utils.dev;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

/**
 * Dev utilities related to creating and storing inventories.
 */
public class DevInventoryUtils {

    private static final Path saved = Path.of("cookies/screens/saved");

    /**
     * Loads an inventory that was previously saved via {@link DevInventoryUtils#saveInventory(HandledScreen)}.
     *
     * @param name The name of the inventory file.
     * @return The inventory screen.
     */
    public static Optional<Screen> createInventory(String name) {
        final Path resolve = saved.resolve(name + ".json");
        if (!Files.exists(resolve)) {
            return Optional.empty();
        }

        final String fileContent;
        try {
            fileContent = Files.readString(resolve);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject jsonObject = JsonUtils.CLEAN_GSON.fromJson(fileContent, JsonObject.class);


        final GenericContainerScreenHandler generic9x6 =
            GenericContainerScreenHandler.createGeneric9x6(-1, MinecraftClient.getInstance().player.getInventory());

        final Map<Integer, ItemStack> slots = getSlots(jsonObject.get("slots").getAsJsonObject());
        slots.forEach((integer, itemStack) -> {
            generic9x6.setStackInSlot(integer, 0, itemStack);
        });

        final GenericContainerScreen screen = new GenericContainerScreen(
            generic9x6,
            MinecraftClient.getInstance().player.getInventory(),
            Text.Serialization.fromJsonTree(jsonObject.get("name"),
                MinecraftClient.getInstance().world.getRegistryManager())
        );


        return Optional.of(screen);
    }

    private static @NotNull Map<Integer, ItemStack> getSlots(JsonObject jsonObject) {
        Map<Integer, ItemStack> slots = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            final DataResult<ItemStack> parse = ItemStack.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            if (parse.isSuccess()) {
                slots.put(Integer.parseInt(entry.getKey()), parse.getOrThrow());
            }
        }

        return slots;
    }

    /**
     * Saves the provided inventory and returns the path it was saved to.
     *
     * @param handledScreen The inventory to save.
     * @param <T>           The type of the inventory.
     * @return The path it was saved to.
     */
    public static <T extends ScreenHandler> Path saveInventory(HandledScreen<T> handledScreen) {
        final JsonObject slots = getSlots(handledScreen.getScreenHandler().slots);

        JsonObject screenValues = new JsonObject();
        screenValues.addProperty(
            "name",
            Text.Serialization.toJsonString(handledScreen.getTitle(),
                MinecraftClient.getInstance().world.getRegistryManager())
        );
        screenValues.addProperty("type",
            Registries.SCREEN_HANDLER.getEntry(handledScreen.getScreenHandler().getType())
                .getIdAsString());
        screenValues.add("slots", slots);

        final Path path =
            saved.resolve(System.currentTimeMillis() + "-" + handledScreen.getTitle().getString() + ".json");
        try {
            Files.writeString(path, JsonUtils.CLEAN_GSON.toJson(screenValues), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    private static @NotNull JsonObject getSlots(DefaultedList<Slot> slots) {
        JsonObject slotsObject = new JsonObject();
        slots.forEach(slot -> {
            final DataResult<JsonElement> jsonElementDataResult =
                ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, slot.getStack());
            final Optional<JsonElement> result = jsonElementDataResult.result();
            if (result.isEmpty()) {
                return;
            }
            slotsObject.add(slot.getIndex() + "", result.get());
        });
        return slotsObject;
    }

}
