package dev.morazzer.cookies.mod.services;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemCompound;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.profile.IslandChestStorage;
import dev.morazzer.cookies.mod.events.ItemStackEvents;
import dev.morazzer.cookies.mod.render.Renderable;
import dev.morazzer.cookies.mod.render.WorldRender;
import dev.morazzer.cookies.mod.render.types.BlockHighlight;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * The service for easy interaction with the {@link IslandChestStorage}.
 */
public class ItemSearchService {

    static final List<Renderable> currentlyActive = new CopyOnWriteArrayList<>();
    static ItemStack currentlySearched;
    static List<ItemStack> modifiedStacks = new CopyOnWriteArrayList<>();

    static {
        ItemStackEvents.EVENT.register(ItemSearchService::modify);
    }

    /**
     * Adds a chest to the currently active profile, and ignores it if there is no active profile.
     *
     * @param first  The first chest block.
     * @param second The second chest block (or null).
     * @param stacks The items in the chest.
     */
    public static void add(BlockPos first, BlockPos second, List<ItemStack> stacks) {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            return;
        }

        final IslandChestStorage islandStorage = currentProfile.get().getGlobalProfileData().getIslandStorage();
        islandStorage.add(first, second, stacks);
    }

    /**
     * Removes a chest from the currently active profile, or ignores it if there is no active profile.
     *
     * @param pos The position of the chest borken (either left or right works for double chests)
     */
    public static void chestBreak(BlockPos pos) {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            return;
        }

        final IslandChestStorage islandStorage = currentProfile.get().getGlobalProfileData().getIslandStorage();
        islandStorage.remove(pos);
    }

    /**
     * Highlights the chests associated with the provided context for 10s and also displays a title to notify the
     * user of the highlight.
     *
     * @param context The context to highlight.
     */
    public static synchronized void highlight(ItemCompound context) {
        removeActive(currentlyActive, currentlySearched);
        currentlySearched = context.itemStack();

        final RepositoryItem data = ItemUtils.getData(context.itemStack(), CookiesDataComponentTypes.REPOSITORY_ITEM);
        final int color;
        if (data != null && data.getTier() != null) {
            final Formatting formatting = data.getTier().getFormatting();
            switch (formatting) {
                case GREEN -> color = Constants.SUCCESS_COLOR;
                case WHITE -> color = 0xFFEBEBEB;
                default -> color = Objects.requireNonNullElse(formatting.getColorValue(), Constants.SUCCESS_COLOR);
            }
        } else {
            color = Constants.SUCCESS_COLOR;
        }
        context.itemStack().set(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR, 0xFF000000 | color);

        if (LocationUtils.Island.PRIVATE_ISLAND.isActive()) {
            for (Item<?> item : context.items()) {
                if (item.source() != ItemSources.CHESTS) {
                    continue;
                }
                final BiBlockPosKey data1 = (BiBlockPosKey) item.data();
                final BlockHighlight first = new BlockHighlight(data1.first(), color);
                final BlockHighlight second = data1.second() != null ? new BlockHighlight(data1.second(), color) : null;

                WorldRender.addRenderable(first);
                if (second != null) {
                    WorldRender.addRenderable(second);
                }

                currentlyActive.add(first);
                if (second != null) {
                    currentlyActive.add(second);
                }
            }
        }
        final ArrayList<Renderable> copy = new ArrayList<>(currentlyActive);
        CookiesMod.getExecutorService()
            .schedule(() -> removeActive(copy, context.itemStack()), 10, TimeUnit.SECONDS);
        MinecraftClient.getInstance().inGameHud.setTitleTicks(4, 40, 4);
        MinecraftClient.getInstance().inGameHud.setTitle(context.itemStack().getName());
        MinecraftClient.getInstance().inGameHud.setSubtitle(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_HIGHLIGHT)
            .withColor(Constants.MAIN_COLOR));
    }

    /**
     * Removes the active highlight.
     *
     * @param renderables All currently active highlights that should be removed.
     * @param current     The currently active item.
     */
    public static void removeActive(List<Renderable> renderables, ItemStack current) {
        renderables.forEach(WorldRender::removeRenderable);
        currentlyActive.clear();
        for (ItemStack modifiedStack : modifiedStacks) {
            if (isSame(modifiedStack, currentlySearched)) {
                modifiedStack.remove(CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
            }
        }
        if (isSame(current, currentlySearched)) {
            currentlySearched = null;
        }
    }

    /**
     * Whether the two items are the same.
     * <br>
     * This will be figured out by the following criteria. <br>
     * 1. Skyblock ID <br>
     * 2. Enchants <br>
     * 3. Attributes <br>
     * 4. Custom Name <br>
     * 5. Modifier <br>
     * <br>
     * If one is not present on both, that criteria is considered a match. <br>
     * If on is missing on either but present on the other, it is not considered a match and will return false.
     *
     * @param first  The first item stack to check.
     * @param second The second item stack to check.
     * @return Whether the two items are (more or less) the same.
     */
    public static boolean isSame(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return false;
        }

        if (first.getItem() == Items.ENCHANTED_BOOK && isSame(first, second, CookiesDataComponentTypes.SKYBLOCK_ID)) {
            return true;
        }

        if (!isSame(first, second, CookiesDataComponentTypes.SKYBLOCK_ID)) {
            return false;
        }
        if (!isSame(first, second, CookiesDataComponentTypes.ENCHANTMENTS)) {
            return false;
        }
        if (!isSame(first, second, CookiesDataComponentTypes.ATTRIBUTES)) {
            return false;
        }
        if (!isSame(first, second, DataComponentTypes.CUSTOM_NAME)) {
            return false;
        }
        return isSame(first, second, CookiesDataComponentTypes.MODIFIER);
    }

    private static <T> boolean isSame(ItemStack first, ItemStack second, ComponentType<T> type) {
        T firstComponent = ItemUtils.getData(first, type);
        T secondComponent = ItemUtils.getData(second, type);

        if (firstComponent == null || secondComponent == null) {
            return firstComponent == null && secondComponent == null;
        }

        if (firstComponent instanceof Map<?, ?> firstMap && secondComponent instanceof Map<?, ?> secondMap) {
            for (Object firstMapKey : firstMap.keySet()) {
                if (!secondMap.containsKey(firstMapKey)) {
                    return false;
                }
                if (!Objects.equals(secondMap.get(firstMapKey), firstMap.get(firstMapKey))) {
                    return false;
                }
            }
        } else if (firstComponent instanceof Text firstText && secondComponent instanceof Text secondText &&
                   (firstText.getString() == null || !firstText.getString().equalsIgnoreCase(secondText.getString()))) {
            return false;
        }

        return Objects.deepEquals(firstComponent, secondComponent);
    }

    private static void modify(ItemStack itemStack) {
        if (currentlySearched == null) {
            return;
        }

        if (isSame(itemStack, currentlySearched)) {
            itemStack.set(
                CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR,
                ItemUtils.getData(currentlySearched, CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR));
            modifiedStacks.add(itemStack);
        }
    }

    /**
     * A data class to store both a {@link BiBlockPosKey} and a {@link List<ItemStack>}.
     *
     * @param blockPos The block key for the chest.
     * @param stacks   The stacks in the chest.
     */
    public record IslandItems(BiBlockPosKey blockPos, List<ItemStack> stacks) {}

    /**
     * A data class to save two {@link BlockPos}.
     *
     * @param first  The first position.
     * @param second The second position (or null).
     */
    public record BiBlockPosKey(BlockPos first, BlockPos second) {
        /**
         * A codec for serialization and deserialization of the {@link BiBlockPosKey}.
         */
        public static final Codec<BiBlockPosKey> CODEC =
            Codec.STRING.flatXmap(s -> DataResult.success(new BiBlockPosKey(s)), b -> DataResult.success(b.toString()));

        /**
         * Parses the serialized string value to a key.
         *
         * @param key The serialized key.
         */
        public BiBlockPosKey(String key) {
            this(BlockPos.fromLong(getFirst(key)), BlockPos.fromLong(getSecond(key)));
        }

        /**
         * Gets the first block pos from the key.
         *
         * @param key The key.
         * @return The block pos (as long).
         */
        public static long getFirst(String key) {
            final String[] split = key.split(";");
            return Long.parseLong(split[0]);
        }

        /**
         * Gets the second block pos from the key.
         *
         * @param key The key.
         * @return The block pos (as long).
         */
        public static long getSecond(String key) {
            final String[] split = key.split(";");
            return Long.parseLong(split.length == 2 ? split[1] : "0");
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof BiBlockPosKey otherBi) {
                return otherBi.first().asLong() == first().asLong() &&
                       (second() == null || otherBi.second().asLong() == second().asLong());
            } else if (other instanceof BlockPos blockPos) {
                return blockPos.asLong() == first().asLong() ||
                       (second() != null && blockPos.asLong() == second().asLong());
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (second() == null) {
                return Long.hashCode(first.asLong());
            }

            long first = Math.min(first().asLong(), second().asLong());
            long second = Math.max(second().asLong(), first().asLong());
            int result = Long.hashCode(first);
            result = 31 * result + Long.hashCode(second);

            return result;
        }

        @Override
        public String toString() {
            if (second() == null) {
                return String.valueOf(first.asLong());
            }

            long first = Math.min(first().asLong(), second().asLong());
            long second = Math.max(second().asLong(), first().asLong());
            return "%s;%s".formatted(first, second);
        }
    }

    /**
     * A context to keep an {@link ItemStack} in relation to its chests stored as {@link Block}
     *
     * @param stack  The stack.
     * @param blocks The chests locations.
     */
    public record Context(ItemStack stack, Set<Block> blocks) {}

    /**
     * A data class to save a {@link BiBlockPosKey} and the amount of items found in that chest.
     *
     * @param blocks The block.
     * @param count  The amount of items.
     */
    public record Block(ItemSearchService.BiBlockPosKey blocks, AtomicInteger count) {}
}
