package dev.morazzer.cookies.mod.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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
	@SerializedName("minecraft_id")
	private String minecraftId;
	private String category;
	private int color;
	private Tier tier;
	private double value;
	@SerializedName("motes_value")
	private double motesValue;
	private String soulboundtype;
	private boolean museumable;
	@SerializedName("rift_transferrable")
	private boolean riftTransferrable;
	private boolean sackable;
	private List<Text> lore;
	//TODO gemslots, bazaarable, essence (already included in data)
	private String skin;

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
				itemMap.put(repositoryItem.internalId.toLowerCase(Locale.ROOT)
						.replaceAll(":", "_")
						.replaceAll(";", "_")
						.replaceAll("-", "_"), repositoryItem);
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
		final RepositoryItem repositoryItem = itemMap.get(id.toLowerCase(Locale.ROOT));
		if (repositoryItem != null) {
			return repositoryItem;
		}

		return itemMap.get(id.toLowerCase(Locale.ROOT).replaceAll(":", "_").replaceAll("-", "_").replaceAll(";", "_"));
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

	@Override
	public boolean equals(Object obj) {
		return switch (obj) {
			case null -> false;
			case Identifier identifier -> (identifier.getNamespace().equalsIgnoreCase("cookies") ||
										   identifier.getNamespace().equalsIgnoreCase("skyblock")) &&
										  identifier.getPath().equals(this.internalId);
			case Ingredient ingredient -> this.equals(ingredient.getRepositoryItem());
			case RepositoryItem repositoryItem -> Objects.equals(this.internalId, repositoryItem.getInternalId());
			default -> super.equals(obj);
		};
	}

	/**
	 * Gets the name as text with the formatting applied.
	 *
	 * @return The formatted name.
	 */
	public Text getFormattedName() {
		if (this.tier == null) {
			return this.name.copy();
		}
		if (this.name.getStyle().isEmpty()) {
			return this.name.copy().setStyle(Style.EMPTY.withFormatting(this.tier.formatting));
		}
		return this.name.copy();
	}

	/**
	 * Creates a new item stack for the repository item.
	 *
	 * @return The item stack.
	 */
	public ItemStack constructItemStack() {
		final ItemStack itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(this.minecraftId)));
		itemStack.set(DataComponentTypes.CUSTOM_NAME, this.name.copy().styled(style -> style.withItalic(false)));
		itemStack.set(CookiesDataComponentTypes.REPOSITORY_ITEM, this);
		final PropertyMap propertyMap = new PropertyMap();
		final ProfileComponent component = new ProfileComponent(new GameProfile(UUID.randomUUID(), "meowora"));
		component.properties().put("textures", new Property("textures", this.skin));
		itemStack.set(DataComponentTypes.PROFILE, component);
		itemStack.set(DataComponentTypes.LORE, new LoreComponent(this.lore, this.lore));
		itemStack.set(CookiesDataComponentTypes.SKYBLOCK_ID, this.internalId);
		itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, false));
		itemStack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		return itemStack;
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
