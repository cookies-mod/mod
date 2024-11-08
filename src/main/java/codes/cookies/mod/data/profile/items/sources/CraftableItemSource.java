package codes.cookies.mod.data.profile.items.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import codes.cookies.mod.config.categories.ItemSearchConfig;
import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSource;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.CraftRecipe;
import codes.cookies.mod.repository.recipes.Recipe;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import org.apache.commons.lang3.ArrayUtils;

/**
 * An item source for all craftable and non craftable items.
 */
public class CraftableItemSource implements ItemSource<CraftableItemSource.Data> {
	@Override
	public Collection<Item<?>> getAllItems() {
		final Collection<Item<?>> items =
				ItemSources.getItems(ArrayUtils.remove(ItemSources.values(), ItemSources.CRAFTABLE.ordinal()));
		Map<RepositoryItem, Integer> repoItemMap = new HashMap<>();
		Map<RepositoryItem, Integer> superCraftAvailable = new HashMap<>();
		Map<RepositoryItem, List<Item<?>>> itemList = new HashMap<>();
		for (Item<?> item : items) {
			final RepositoryItem repositoryItem = item.itemStack().get(CookiesDataComponentTypes.REPOSITORY_ITEM);
			repoItemMap.computeIfPresent(repositoryItem, (r, amount) -> amount + item.amount());
			repoItemMap.computeIfAbsent(repositoryItem, r -> item.amount());
			final List<Item<?>> orDefault = itemList.getOrDefault(repositoryItem, new ArrayList<>());
			orDefault.add(item);
			itemList.put(repositoryItem, orDefault);
			if (item.source().isSupportsSupercraft()) {
				superCraftAvailable.computeIfPresent(repositoryItem, (r, amount) -> amount + item.amount());
				superCraftAvailable.computeIfAbsent(repositoryItem, r -> item.amount());
			}
		}
		List<Item<?>> craftable = new ArrayList<>();
		for (CraftRecipe allCraftRecipe : Recipe.ALL_CRAFT_RECIPES) {
			if (allCraftRecipe.getOutput() == null) {
				continue;
			}

			boolean hasAllIngredients = true;
			Map<Ingredient, IngredientData> map = new HashMap<>();
			boolean canSupercraft = true;
			boolean partialItems = false;
			boolean showSupercraftWarning = false;

			for (Ingredient ingredient : allCraftRecipe.getIngredients()) {
				final RepositoryItem repositoryItem = ingredient.getRepositoryItem();

				if (repositoryItem == null) {
					hasAllIngredients = false;
					map.put(ingredient, IngredientData.EMPTY);
					continue;
				}

				final int superCraftAmount = Objects.requireNonNullElse(superCraftAvailable.get(repositoryItem), 0);
				final int normalAmount = Objects.requireNonNullElse(repoItemMap.get(repositoryItem), 0);
				hasAllIngredients = hasAllIngredients && normalAmount >= ingredient.getAmount();

				if (normalAmount == 0) {
					map.put(ingredient, IngredientData.EMPTY);
					continue;
				}

				final IngredientData ingredientData = new IngredientData(
						itemList.getOrDefault(repositoryItem, Collections.emptyList()),
						normalAmount,
						superCraftAmount,
						superCraftAmount >= ingredient.getAmount(),
						normalAmount >= ingredient.getAmount());
				canSupercraft = canSupercraft && ingredientData.canSupercraft();
				partialItems = partialItems || ingredientData.hasAllItems();
				showSupercraftWarning = showSupercraftWarning || ingredientData.hasAllItems() && !ingredientData.canSupercraft();
				map.put(ingredient, ingredientData);
			}

			if (!ItemSearchConfig.getInstance().enableNotCraftableItems.getValue() && !hasAllIngredients) {
				continue;
			}

			craftable.add(new Item<>(
					allCraftRecipe.getOutput().getAsItem(),
					ItemSources.CRAFTABLE,
					1,
					new Data(map, hasAllIngredients, canSupercraft, partialItems, showSupercraftWarning, allCraftRecipe.getOutput())));
		}

		return craftable;
	}

	@Override
	public ItemSources getType() {
		return ItemSources.CRAFTABLE;
	}

	@Override
	public void remove(Item<?> item) {

	}

	/**
	 * @param amounts The ingredient mappings.
	 * @param hasAllIngredients Whether the player has all ingredients.
	 * @param canSupercraft Whether the player can supercraft the item.
	 * @param hasPartialItems If the player has at least one ingredient.
	 * @param showSupercraftWarning Whether the supercraft warning should be shown.
	 * @param output The output of the recipe.
	 */
	public record Data(
			Map<Ingredient, IngredientData> amounts, boolean hasAllIngredients, boolean canSupercraft,
			boolean hasPartialItems, boolean showSupercraftWarning, Ingredient output
	) {

	}

	/**
	 *
	 * @param items The items used in the ingredient.
	 * @param available The amount of total items.
	 * @param availableSupercraft The amount of items available to the supercraft.
	 * @param canSupercraft Whether there are enough items to super craft.
	 * @param hasAllItems Whether the player has all items to craft.
	 */
	public record IngredientData(
			List<Item<?>> items, int available, int availableSupercraft, boolean canSupercraft, boolean hasAllItems
	) {
		public static IngredientData EMPTY = new IngredientData(Collections.emptyList(), 0, 0, false, false);

		public boolean shouldBeIncluded() {
			if (ItemSearchConfig.getInstance().showOnlyMissingItems.getValue()) {
				return hasAllItems && !canSupercraft;
			}
			return true;
		}
	}
}
