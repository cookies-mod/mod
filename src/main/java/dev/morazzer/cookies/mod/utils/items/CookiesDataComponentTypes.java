package dev.morazzer.cookies.mod.utils.items;

import com.mojang.serialization.Codec;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import lombok.Getter;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

/**
 * All custom data component types.
 */
@SuppressWarnings("MissingJavadoc")
public class CookiesDataComponentTypes {

    @GenerateAccessor
    public static final ComponentType<String> SKYBLOCK_ID;
    @GenerateAccessor
    public static final ComponentType<UUID> UUID;
    @GenerateAccessor
    public static final ComponentType<Instant> TIMESTAMP;
    @GenerateAccessor
    public static final ComponentType<String> MODIFIER;
    @GenerateAccessor
    public static final ComponentType<Boolean> DONATED_MUSEUM;
    @GenerateAccessor
    public static final ComponentType<Map<String, Integer>> ENCHANTMENTS;
    @GenerateAccessor
    public static final ComponentType<Integer> HOT_POTATO_COUNT;
    @GenerateAccessor
    public static final ComponentType<Integer> RARITY_UPGRADES;
    @GenerateAccessor
    public static final ComponentType<Map<String, Integer>> RUNES;
    @GenerateAccessor
    public static final ComponentType<Integer> UPGRADE_LEVEL;
    @GenerateAccessor
    public static final ComponentType<Integer> STACKING_ENCHANT_XP;
    @GenerateAccessor
    public static final ComponentType<RepositoryItem> REPOSITORY_ITEM;
    @GenerateAccessor
    public static final ComponentType<String> DYE;

    public static final ComponentType<String> CUSTOM_SLOT_TEXT;
    public static final ComponentType<ItemStack> OVERRIDE_RENDER_ITEM;
    public static final ComponentType<Integer> ITEM_BACKGROUND_COLOR;
    public static final ComponentType<List<Text>> CUSTOM_LORE;
    public static final ComponentType<ItemTooltipComponent> LORE_ITEMS;

    private static final List<ComponentType<?>> list = new ArrayList<>();
    @Getter
    private static final List<DataType<?, ?>> dataTypes = new LinkedList<>();

    static {
        //noinspection deprecation
        SKYBLOCK_ID = register(
            builder -> builder.codec(Codecs.NON_EMPTY_STRING),
            "id",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> nbtComponent.getNbt().getString(key));
        //noinspection deprecation
        UUID = register(
            builder -> builder.codec(Uuids.CODEC),
            "uuid",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> java.util.UUID.randomUUID()
            //nbtComponent.getNbt().getUuid(key)
        );
        //noinspection deprecation
        TIMESTAMP = register(
            builder -> builder.codec(Codecs.INSTANT),
            "timestamp",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> Instant.ofEpochMilli(nbtComponent.getNbt().getLong(key)));
        //noinspection deprecation
        MODIFIER = register(
            builder -> builder.codec(Codec.STRING),
            "modifier",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> nbtComponent.getNbt().getString(key));
        //noinspection deprecation
        DONATED_MUSEUM = register(
            builder -> builder.codec(Codec.BOOL),
            "donated_museum",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> nbtComponent.getNbt().getInt(key) == 1);
        ENCHANTMENTS = register(
            builder -> builder.codec(Codec.unboundedMap(Codec.STRING, Codec.INT)),
            "enchantments",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> {
                //noinspection deprecation
                final NbtCompound enchantments = nbtComponent.getNbt().getCompound(key);

                Map<String, Integer> map = new HashMap<>();
                for (String elementKey : enchantments.getKeys()) {
                    map.put(elementKey, enchantments.getInt(elementKey));
                }
                return map;
            });
        HOT_POTATO_COUNT = register(
            builder -> builder.codec(Codec.INT),
            "hot_potato_count",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> nbtComponent.getNbt().getInt(key));
        RARITY_UPGRADES = register(
            builder -> builder.codec(Codec.INT),
            "rarity_upgrades",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> nbtComponent.getNbt().getInt(key));
        RUNES = register(
            builder -> builder.codec(Codec.unboundedMap(Codec.STRING, Codec.INT)),
            "runes",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> {
                //noinspection deprecation
                final NbtCompound enchantments = nbtComponent.getNbt().getCompound(key);

                Map<String, Integer> map = new HashMap<>();
                for (String elementKey : enchantments.getKeys()) {
                    map.put(elementKey, enchantments.getInt(elementKey));
                }
                return map;
            });
        UPGRADE_LEVEL = register(
            builder -> builder.codec(Codec.INT),
            "upgrade_level",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> nbtComponent.getNbt().getInt(key));
        STACKING_ENCHANT_XP = register(
            builder -> builder.codec(Codec.INT),
            new String[] {"stacking_enchantment_xp"},
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, key) -> 0);
        REPOSITORY_ITEM = register(
            builder -> builder.codec(RepositoryItem.CODEC),
            "",
            SKYBLOCK_ID,
            value(true),
            (skyblockId, key) -> RepositoryItem.of(skyblockId));
        DYE = register(
            builder -> builder.codec(Codec.STRING),
            "dye_item",
            DataComponentTypes.CUSTOM_DATA,
            defaultTest(),
            (nbtComponent, s) -> nbtComponent.getNbt().getString(s));
        CUSTOM_SLOT_TEXT = new CookiesDataComponent<>(Identifier.of("cookies:custom_slot_text"));
        OVERRIDE_RENDER_ITEM = new CookiesDataComponent<>(Identifier.of("cookies:override_render_item"));
        ITEM_BACKGROUND_COLOR = new CookiesDataComponent<>(Identifier.of("cookies:item_background_color"));
        CUSTOM_LORE = new CookiesDataComponent<>(Identifier.of("cookies:custom_lore"));
        LORE_ITEMS = new CookiesDataComponent<>(Identifier.of("cookies:lore_items"));
    }

    private static <T, D> ComponentType<T> register(
        UnaryOperator<ComponentType.Builder<T>> operator,
        String key,
        ComponentType<D> source,
        BiFunction<D, String, Boolean> test,
        BiFunction<D, String, T> mapper) {
        return register(operator, new String[] {key}, source, test, mapper);
    }

    private static <T, D> ComponentType<T> register(
        UnaryOperator<ComponentType.Builder<T>> operator,
        String[] key,
        ComponentType<D> source,
        BiFunction<D, String, Boolean> test,
        BiFunction<D, String, T> mapper) {
        final ComponentType.Builder<T> builder = ComponentType.builder();
        final ComponentType<T> build = operator.apply(builder).build();
        list.add(build);
        dataTypes.add(new DataType<>(build, key, source, test, mapper));
        return build;
    }

    private static BiFunction<NbtComponent, String, Boolean> defaultTest() {
        return NbtComponent::contains;
    }

    private static <T> BiFunction<T, String, Boolean> value(boolean result) {
        return (t, key) -> result;
    }

    public static boolean isCustomType(ComponentType<?> type) {
        if (type instanceof CookiesDataComponent<?>) {
            return true;
        }
        return list.contains(type);
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.FIELD)
    private @interface GenerateAccessor {}

    public record DataType<T, D>(
        ComponentType<T> target, String[] key, ComponentType<D> source, BiFunction<D, String, Boolean> test,
        BiFunction<D, String, T> mapper
    ) {

    }
}
