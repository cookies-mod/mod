package dev.morazzer.cookies.mod.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Class to represent an item.
 */
@Getter
@SuppressWarnings("unused")
public class RepositoryItem {

    @Getter
    private static final Map<String, RepositoryItem> itemMap = new ConcurrentHashMap<>();

    /**
     * Codec to serialize and deserialize an repository item.
     */
    public static final PrimitiveCodec<RepositoryItem> CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<RepositoryItem> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).map(m -> m.toLowerCase(Locale.ENGLISH)).map(RepositoryItem::of);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, RepositoryItem value) {
            return ops.createString(value.internalId);
        }
    };

    @Setter(AccessLevel.PACKAGE)
    private Set<Recipe> recipes;
    @Setter(AccessLevel.PACKAGE)
    private Set<Recipe> usedInRecipeAsIngredient;
    private Text name;
    @SerializedName("internal_id")
    private String internalId;
    private String category;
    private Tier tier;
    private double value;
    @SerializedName("motes_value")
    private double motesValue;
    private String soulboundtype;
    private boolean museumable;
    @SerializedName("rift_transferrable")
    private boolean riftTransferrable;
    private boolean sackable;
    private Text lore;
    //TODO gemslots, bazaarable, essence (already included in data)

    /**
     * Loads a collection of items.
     *
     * @param path The path to the file.
     */
    public static void load(Path path) {
        if (!Files.exists(path)) {
            System.err.println("Unable to load item list. (FILE_NOT_FOUND)");
            return;
        }

        try {
            final String content = Files.readString(path, StandardCharsets.UTF_8);
            final Text.Serializer serializer = new Text.Serializer(DynamicRegistryManager.EMPTY);
            Gson gson = new GsonBuilder().registerTypeAdapter(Text.class, serializer).create();
            final RepositoryItem[] repositoryItems = gson.fromJson(content, new TypeToken<>() {});
            for (RepositoryItem repositoryItem : repositoryItems) {
                repositoryItem.setRecipes(new HashSet<>());
                repositoryItem.setUsedInRecipeAsIngredient(new HashSet<>());
                itemMap.put(repositoryItem.internalId.toLowerCase(Locale.ENGLISH), repositoryItem);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the item corresponding to the id or null.
     *
     * @param id The id.
     * @return The item or null.
     */
    public static RepositoryItem of(String id) {
        return itemMap.get(id.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Tries to fine an item by its name.
     *
     * @param name The name of the item.
     * @return The item.
     */
    public static Optional<RepositoryItem> ofName(String name) {
        return itemMap.values()
            .stream()
            .filter(repositoryItem -> repositoryItem.getName().getString().equalsIgnoreCase(name))
            .findFirst();
    }

    /**
     * Gets the name as text with the formatting applied.
     *
     * @return The formatted name.
     */
    public Text getFormattedName() {
        return this.name.copyContentOnly().formatted(this.tier.formatting);
    }

    /**
     * All tiers (rarities) available as default (so without rarity upgrades)
     */
    @Getter
    @SuppressWarnings("MissingJavadoc")
    public enum Tier {
        @SerializedName("Common") COMMON(Formatting.WHITE),
        @SerializedName("Uncommon") UNCOMMON(Formatting.GREEN),
        @SerializedName("Rare") RARE(Formatting.BLUE),
        @SerializedName("Epic") EPIC(Formatting.DARK_PURPLE),
        @SerializedName("Legendary") LEGENDARY(Formatting.GOLD),
        @SerializedName("Mythic") MYTHIC(Formatting.LIGHT_PURPLE),
        @SerializedName("Special") SPECIAL(Formatting.RED),
        @SerializedName("Very Special") VERY_SPECIAL(Formatting.RED),
        @SerializedName("Ultimate") ULTIMATE(Formatting.DARK_RED),
        @SerializedName("Admin") ADMIN(Formatting.DARK_RED);


        private final Formatting formatting;

        Tier(Formatting formatting) {
            this.formatting = formatting;
        }
    }
}
