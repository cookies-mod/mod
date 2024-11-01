package dev.morazzer.cookies.mod.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.utils.dev.FunctionUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

/**
 * Class to represent an item.
 */
@Getter
@SuppressWarnings("unused")
public class RepositoryItem {
	public static final RepositoryItem EMPTY = createEmpty();
	public static final Codec<RepositoryItem> CODEC =
			RecordCodecBuilder.create(instance -> instance.group(
							Codec.STRING.fieldOf("internal_id")
									.forGetter(RepositoryItem::getInternalId),
							Codec.STRING.optionalFieldOf("minecraft_id", "minecraft:barrier")
									.forGetter(RepositoryItem::getMinecraftId),
							TextCodecs.CODEC.optionalFieldOf(
											"name",
											Text.literal("<name not found>").formatted(Formatting.RED))
									.forGetter(RepositoryItem::getName),
							TextCodecs.CODEC.listOf()
									.optionalFieldOf("lore", Collections.emptyList())
									.forGetter(RepositoryItem::getLore),
							Codec.STRING.optionalFieldOf("category")
									.forGetter(FunctionUtils.wrapOptionalF(RepositoryItem::getCategory)),
							Codec.INT.optionalFieldOf("color", 0).forGetter(RepositoryItem::getColor),
							Tier.CODEC.optionalFieldOf("tier", Tier.COMMON).forGetter(RepositoryItem::getTier),
							SoulBoundType.CODEC.optionalFieldOf("soulboundtype", SoulBoundType.NONE)
									.forGetter(RepositoryItem::getSoulboundtype),
							Codec.DOUBLE.optionalFieldOf("value", 0d).forGetter(RepositoryItem::getValue),
							Codec.DOUBLE.optionalFieldOf("motes_value", 0d).forGetter(RepositoryItem::getMotesValue),
							Codec.BOOL.optionalFieldOf("museumable", false).forGetter(RepositoryItem::isMuseumable),
							Codec.BOOL.optionalFieldOf("rift_transferrable", false)
									.forGetter(RepositoryItem::isRiftTransferrable),
							Codec.BOOL.optionalFieldOf("sackable", false).forGetter(RepositoryItem::isSackable),
							Codec.STRING.optionalFieldOf("skin")
									.forGetter(FunctionUtils.wrapOptionalF(RepositoryItem::getSkin)))
					.apply(instance, RepositoryItem::create));
	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryItem.class);
	private static final Logger log = LoggerFactory.getLogger(RepositoryItem.class);
	@Getter
	private static final Map<String, RepositoryItem> itemMap = new ConcurrentHashMap<>();
	/**
	 * Codec to serialize and deserialize an repository item.
	 */
	public static final Codec<RepositoryItem> ID_CODEC =
			Codec.STRING.xmap(
					s -> Optional.ofNullable(RepositoryItem.of(s)).orElse(EMPTY),
					RepositoryItem::getInternalId);

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
	private SoulBoundType soulboundtype;
	private boolean museumable;
	@SerializedName("rift_transferrable")
	private boolean riftTransferrable;
	private boolean sackable;
	private List<Text> lore;
	//TODO gemslots, bazaarable, essence (already included in data)
	private String skin;
	private RepositoryItemMuseumData museumData;

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
			final JsonElement jsonElement = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
			final DataResult<List<RepositoryItem>> parse = CODEC.listOf().parse(JsonOps.INSTANCE, jsonElement);
			for (RepositoryItem repositoryItem : parse.resultOrPartial(log::error).orElse(Collections.emptyList())) {
				repositoryItem.setRecipes(new HashSet<>());
				repositoryItem.setUsedInRecipeAsIngredient(new HashSet<>());
				repositoryItem.museumable = false;
				final RepositoryItem put = itemMap.put(repositoryItem.internalId.toLowerCase(Locale.ROOT)
						.replace(":", "/")
						.replace("-", "/")
						.replace(";", "/"), repositoryItem);
				if (put != null) {
					LOGGER.warn("Duplicate id detected {}", repositoryItem.internalId);
				}
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

		return itemMap.get(id.toLowerCase(Locale.ROOT).replace(":", "/").replace("-", "/").replace(";", "/"));
	}

	/**
	 * Tries to fine an item by its name.
	 *
	 * @param name The name of the item.
	 * @return The item.
	 */
	public static Optional<RepositoryItem> ofName(String name) {
		String actualSearch;
		if (name.endsWith("x1")) {
			actualSearch = name.substring(0, name.length() - 3);
		} else {
			actualSearch = name;
		}
		return itemMap.values()
				.stream()
				.filter(repositoryItem -> repositoryItem.getName().getString().equalsIgnoreCase(actualSearch))
				.findFirst();
	}

	private static RepositoryItem createEmpty() {
		return create(
				"empty_" + UUID.randomUUID(),
				"minecraft:barrier",
				Text.literal("Not found").formatted(Formatting.RED),
				Collections.emptyList(),
				Optional.empty(),
				0,
				Tier.ADMIN,
				SoulBoundType.SOULBOUND,
				0D,
				0D,
				false,
				false,
				false,
				Optional.empty());
	}

	public static RepositoryItem createNotFound(String id) {
		return create(
				id,
				"minecraft:barrier",
				Text.literal("Not found (%s)".formatted(id)).formatted(Formatting.RED),
				Collections.emptyList(),
				Optional.empty(),
				0,
				Tier.ADMIN,
				SoulBoundType.SOULBOUND,
				0D,
				0D,
				false,
				false,
				false,
				Optional.empty());
	}

	private static RepositoryItem create(
			String internalId,
			String minecraftId,
			Text name,
			List<Text> lore,
			Optional<String> category,
			Integer color,
			Tier tier,
			SoulBoundType soulBoundType,
			Double value,
			Double motesValue,
			Boolean museumable,
			Boolean riftTransferrable,
			Boolean sackable,
			Optional<String> skin
	) {
		RepositoryItem item = new RepositoryItem();
		item.internalId = internalId;
		item.minecraftId = minecraftId;
		item.name = name;
		item.lore = lore;
		item.category = category.orElse(null);
		item.color = color;
		item.tier = tier;
		item.soulboundtype = soulBoundType;
		item.value = value;
		item.motesValue = motesValue;
		item.museumable = museumable;
		item.riftTransferrable = riftTransferrable;
		item.sackable = sackable;
		item.skin = skin.orElse(null);
		item.recipes = Collections.emptySet();
		item.usedInRecipeAsIngredient = Collections.emptySet();
		return item;
	}

	public static <T> Function<T, RepositoryItem> getMappedOrEmpty(Function<T, ItemStack> mapper) {
		return t -> getOrEmpty(mapper.apply(t));
	}

	@NotNull
	public static RepositoryItem getOrEmpty(ItemStack item) {
		if (item.contains(CookiesDataComponentTypes.REPOSITORY_ITEM)) {
			return Optional.ofNullable(item.get(CookiesDataComponentTypes.REPOSITORY_ITEM)).orElse(EMPTY);
		}
		return EMPTY;
	}

	public Optional<RepositoryItemMuseumData> getMuseumData() {
		return Optional.ofNullable(museumData);
	}

	public RepositoryItemMuseumData getOrCreateMuseumData() {
		if (museumData == null) {
			museumData = new RepositoryItemMuseumData();
		}
		return museumData;
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

	public boolean isMuseumable() {
		return this.museumData != null && museumData.getDonationType() != RepositoryItemMuseumData.DonationType.NONE;
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
	public enum Tier implements StringIdentifiable {
		COMMON(Formatting.WHITE),
		UNCOMMON(Formatting.GREEN),
		RARE(Formatting.BLUE),
		EPIC(Formatting.DARK_PURPLE),
		LEGENDARY(Formatting.GOLD),
		MYTHIC(Formatting.LIGHT_PURPLE),
		SPECIAL(Formatting.RED),
		VERY_SPECIAL(Formatting.RED),
		ULTIMATE(Formatting.DARK_RED),
		ADMIN(Formatting.DARK_RED);

		public static final Codec<Tier> CODEC =
				StringIdentifiable.createCodec(Tier::values, String::toUpperCase).orElse(Tier.COMMON);

		private final Formatting formatting;

		Tier(Formatting formatting) {
			this.formatting = formatting;
		}

		@Override
		public String asString() {
			return name();
		}
	}

	public enum SoulBoundType implements StringIdentifiable {
		COOP,
		SOULBOUND,
		NONE;

		public static final Codec<SoulBoundType> CODEC = StringIdentifiable.createBasicCodec(SoulBoundType::values);

		@Override
		public String asString() {
			return name();
		}
	}
}
