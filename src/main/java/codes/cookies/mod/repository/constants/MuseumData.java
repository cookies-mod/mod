package codes.cookies.mod.repository.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.Either;
import codes.cookies.mod.utils.Result;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Repository data for the museum.
 * @param weapons All weapons that can be put in the museum.
 * @param armor All armor items that can be put in the museum.
 * @param rarity All items that fall under the rarity category.
 * @param special All items that fall under the special category.
 * @param museumItems All museum items.
 * @param exceptions All naming exceptions.
 */
@Slf4j
public record MuseumData(
		List<MuseumItem> weapons, List<ArmorItem> armor, List<MuseumItem> rarity, List<RepositoryItem> special,
		Set<RepositoryItem> museumItems, Map<String, String> exceptions
) {

	public static Codec<MuseumData> CODEC =
			RecordCodecBuilder.create(instance -> instance.group(MuseumItem.CODEC.listOf()
									.fieldOf("museum_weapons")
									.forGetter(MuseumData::weapons),
							ArmorItem.CODEC.listOf().fieldOf("museum_armor").forGetter(MuseumData::armor),
							MuseumItem.CODEC.listOf().fieldOf("museum_rarity").forGetter(MuseumData::rarity),
							RepositoryItem.ID_CODEC.listOf().fieldOf("museum_special").forGetter(MuseumData::special),
							Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("armor_exceptions").forGetter(MuseumData::exceptions))
					.apply(instance, MuseumData::create));

	public static MuseumData load(JsonObject jsonObject) {
		final DataResult<MuseumData> parse = CODEC.parse(JsonOps.INSTANCE, jsonObject);
		if (parse.isSuccess()) {
			return parse.getOrThrow();
		}
		final String message = parse.error().orElseThrow().message();
		log.error("Can't load museum data: {}", message);
		return new MuseumData(Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptySet(),
				Collections.emptyMap());
	}

	private static MuseumData create(
			List<MuseumItem> museumWeapons,
			List<ArmorItem> armorItems,
			List<MuseumItem> museumRarity,
			List<RepositoryItem> museumSpecial,
			Map<String, String> armorExceptions) {
		Set<RepositoryItem> museumItems = new HashSet<>();
		for (MuseumItem museumWeapon : museumWeapons) {
			museumWeapon.item.getOrCreateMuseumData().addWeapon(museumWeapon);
			museumItems.add(museumWeapon.item);
		}
		for (MuseumItem museumItem : museumRarity) {
			museumItem.item.getOrCreateMuseumData().addRarity(museumItem);
			museumItems.add(museumItem.item);
		}
		for (ArmorItem armorItem : armorItems) {
			for (RepositoryItem armorId : armorItem.armorIds) {
				armorId.getOrCreateMuseumData().addArmorItems(armorItem);
			}
			museumItems.addAll(armorItem.armorIds);
		}
		for (RepositoryItem special : museumSpecial) {
			special.getOrCreateMuseumData().setSpecial();
		}
		museumItems.addAll(museumSpecial);
		return new MuseumData(museumWeapons, armorItems, museumRarity, museumSpecial, museumItems, new HashMap<>(armorExceptions));
	}

	public Result<Either<MuseumItem, ArmorItem>, MuseumDataError> getItemByName(String name) {
		String lowerCaseName = CookiesUtils.stripColor(name.toLowerCase().replaceAll("[✖✔]", "")).trim();
		List<String> armorNames = Arrays.asList(
				"set",
				"suit",
				"armor",
				"outfit",
				"equipment",
				"'s special armor",
				"'s armor",
				"armor of",
				"tuxedo");
		if (armorNames.stream().anyMatch(lowerCaseName::contains)) {
			final Optional<String> first = armorNames.stream()
					.map(armor -> lowerCaseName.replaceAll(armor, ""))
					.min(Comparator.comparingInt(String::length));
			final String modifiedName = first.get();
			final String id = exceptions.getOrDefault(lowerCaseName, modifiedName).trim().toUpperCase().replaceAll(" ", "_");
			final String secondaryId = lowerCaseName.trim().toUpperCase().replaceAll(" ", "_");
			for (ArmorItem armorItem : armor) {
				if (armorItem.id.equals(id) || armorItem.id.equals(secondaryId)) {
					return Result.success(Either.right(armorItem));
				}
			}
			return Result.error(new MuseumDataError(MuseumDataError.MuseumDataErrorType.NO_ARMOR_FOUND, name));
		}
		final Optional<RepositoryItem> repositoryItem = RepositoryItem.ofName(lowerCaseName);
		if (repositoryItem.isEmpty()) {
			return Result.error(new MuseumDataError(MuseumDataError.MuseumDataErrorType.ITEM_NOT_FOUND, name));
		}
		final RepositoryItem item = repositoryItem.get();
		for (MuseumItem weapon : weapons) {
			if (weapon.item == item || weapon.mappedItems.contains(item)) {
				return Result.success(Either.left(weapon));
			}
		}
		for (MuseumItem museumItem : rarity) {
			if (museumItem.item == item || museumItem.mappedItems.contains(item)) {
				return Result.success(Either.left(museumItem));
			}
		}
		return Result.error(new MuseumDataError(MuseumDataError.MuseumDataErrorType.NO_MATCHING_MUSEUM_FOUND));
	}

	public record MuseumDataError(MuseumDataErrorType errorType, Optional<String> message) {
		public MuseumDataError(MuseumDataErrorType errorType) {
			this(errorType, Optional.empty());
		}

		public MuseumDataError(MuseumDataErrorType errorType, String message) {
			this(errorType, Optional.of(message));
		}

		public enum MuseumDataErrorType {
			ITEM_NOT_FOUND,
			NO_ARMOR_FOUND,
			NO_MATCHING_MUSEUM_FOUND
		}
	}

	public record MuseumItem(RepositoryItem item, @Nullable RepositoryItem parent, List<RepositoryItem> mappedItems) {

		public static Codec<MuseumItem> CODEC =
				RecordCodecBuilder.create(instance -> instance.group(RepositoryItem.ID_CODEC.fieldOf("id")
								.forGetter(MuseumItem::item),
						RepositoryItem.ID_CODEC.optionalFieldOf("parent", RepositoryItem.EMPTY)
								.forGetter(MuseumItem::parent),
						RepositoryItem.ID_CODEC.listOf()
								.optionalFieldOf("mapped_item_ids", Collections.emptyList())
								.forGetter(MuseumItem::mappedItems)).apply(instance, MuseumItem::of));

		public static MuseumItem of(
				RepositoryItem item, @Nullable RepositoryItem parent, List<RepositoryItem> mappedItems) {
			if (parent == RepositoryItem.EMPTY) {
				return new MuseumItem(item, null, mappedItems);
			}
			return new MuseumItem(item, parent, mappedItems);
		}
	}

	public record ArmorItem(String id, List<RepositoryItem> armorIds, @Nullable String parent) {
		public static Codec<ArmorItem> CODEC =
				RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("armor_id")
										.forGetter(ArmorItem::id),
								RepositoryItem.ID_CODEC.listOf()
										.optionalFieldOf("items", Collections.emptyList())
										.forGetter(ArmorItem::armorIds),
								Codec.STRING.optionalFieldOf("parent", "").forGetter(ArmorItem::parent))
						.apply(instance, ArmorItem::of));

		public static ArmorItem of(String id, List<RepositoryItem> armorIds, @Nullable String parent) {
			if (parent == null || parent.isBlank()) {
				return new ArmorItem(id, armorIds, null);
			}
			return new ArmorItem(id, armorIds, parent);
		}
	}

}
