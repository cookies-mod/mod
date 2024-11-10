package codes.cookies.mod.screen.search;

import com.google.common.base.Predicates;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import java.util.Optional;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * The search categories for the {@link ItemSearchScreen}
 */
@AllArgsConstructor
@Getter
public enum ItemSearchCategories {

	ALL(new ItemStack(Items.GRASS_BLOCK), Text.translatable(TranslationKeys.ITEM_SEARCH_ALL), Predicates.alwaysTrue()),
	ARMOR(
			new ItemStack(Items.CHAINMAIL_CHESTPLATE),
			Text.translatable(TranslationKeys.ITEM_SEARCH_ARMOR),
			createCategoryPredicateWithDungeon("CHESTPLATE").or(createCategoryPredicateWithDungeon("BOOTS"))
					.or(createCategoryPredicateWithDungeon("HELMET"))
					.or(createCategoryPredicateWithDungeon("LEGGINGS"))),
	WEAPONS(
			new ItemStack(Items.NETHERITE_SWORD),
			Text.translatable(TranslationKeys.ITEM_SEARCH_WEAPONS),
			createCategoryPredicateWithDungeon("SWORD").or(createCategoryPredicate("FISHING WEAPON"))
					.or(createCategoryPredicateWithDungeon("BOW"))
					.or(createCategoryPredicate("WAND"))
					.or(createCategoryPredicate("DUNGEON LONGSWORD"))),
	MATERIAL(
			new ItemStack(Items.IRON_INGOT),
			Text.translatable(TranslationKeys.ITEM_SEARCH_MATERIAL),
			repositoryItem -> repositoryItem != null && (repositoryItem.getUsedInRecipeAsIngredient().size() > 1 &&
														 repositoryItem.getCategory() == null)),
	MINION(
			new ItemBuilder(Items.PLAYER_HEAD).setSkin(
							"eyJ0aW1lc3RhbXAiOjE1NzY1MTMxOTQ4MDUsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y2ZDE4MDY4NGMzNTIxYzlmYzg5NDc4YmE0NDA1YWU5Y2U0OTdkYTgxMjRmYTBkYTVhMDEyNjQzMWM0Yjc4YzMifX19")
					.build(),
			Text.translatable(TranslationKeys.ITEM_SEARCH_MINION),
			repositoryItem -> repositoryItem != null && repositoryItem.getInternalId().matches(".*GENERATOR_\\d+"));

	public static final ItemSearchCategories[] VALUES = values();
	private final ItemStack display;
	private final Text name;
	private final Predicate<RepositoryItem> itemPredicate;

	private static Predicate<RepositoryItem> createCategoryPredicateWithDungeon(String category) {
		final Predicate<RepositoryItem> categoryPredicate = createCategoryPredicate(category);
		return categoryPredicate.or(createCategoryPredicate("DUNGEON " + category));
	}

	private static Predicate<RepositoryItem> createCategoryPredicate(String category) {
		return repositoryItem -> repositoryItem != null && Optional.ofNullable(repositoryItem.getCategory())
				.map(s -> s.equalsIgnoreCase(category))
				.orElse(false);
	}
}
