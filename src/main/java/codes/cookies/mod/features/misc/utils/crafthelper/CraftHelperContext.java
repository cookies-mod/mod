package codes.cookies.mod.features.misc.utils.crafthelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.features.misc.utils.crafthelper.tooltips.CraftHelperComponent;
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import codes.cookies.mod.repository.recipes.calculations.RecipeResult;

/**
 * Context for craft helper component formatting and evaluation.
 * @param components A list of components.
 * @param prefix The current prefix.
 * @param itemTracker The item tracker state.
 * @param result The result the component is for.
 * @param evaluationContext The evaluation context.
 * @param path The current path.
 * @param toolTipContext The tooltip context to use.
 */
public record CraftHelperContext(List<CraftHelperComponent> components,
								 String prefix,
								 ItemTracker itemTracker,
								 RecipeResult<?> result,
								 EvaluationContext evaluationContext, String path, ToolTipContext toolTipContext) {

	public CraftHelperContext(
			List<CraftHelperComponent> components,
			String prefix,
			ItemTracker itemTracker,
			RecipeResult<?> result,
			EvaluationContext evaluationContext,
			String path
	) {
		this(
				components,
				prefix,
				itemTracker,
				result,
				evaluationContext,
				path,
				new ToolTipContext(result.getRepositoryItemNotNull(), prefix));
	}

	/**
	 * Creates a new craft helper context for the result and the provided item sources.
	 */
	public static CraftHelperContext create(RecipeCalculationResult result, ItemSources... values) {
		final Stack<Integer> objects = new Stack<>();
		objects.push(0);
		return new CraftHelperContext(
				new ArrayList<>(),
				"  ",
				new ItemTracker(values),
				result,
				new EvaluationContext(null, result, objects), "root");
	}

	/**
	 * Creates or returns the tooltip context.
	 */
	public ToolTipContext toContext() {
		if (!toolTipContext.hasBeenInitialized) {
			toolTipContext.amount = amount() - getAmountThroughParents();
			toolTipContext.amountThroughParents = getAmountThroughParents();
			toolTipContext.itemTracker = itemTracker.copy();
			toolTipContext.required = getMax() - getAmountThroughParents();
			toolTipContext.hasBeenInitialized = true;
			toolTipContext.path = path;
		}
		return toolTipContext;
	}

	/**
	 * @return The max amount.
	 */
	public int getMax() {
		return result.getAmount();
	}

	/**
	 * @return The amount that is present through parent elements.
	 */
	public int getAmountThroughParents() {
		if (evaluationContext.parent() == null) {
			return 0;
		}
		int amount = (evaluationContext.stack().peek() * (evaluationContext.recipeResult()
				.getAmount() / evaluationContext.parent().recipeResult().getAmount()));
		return Math.min(amount, result.getAmount());
	}

	/**
	 * @return The amount that is present.
	 */
	public int amount() {
		int amount = 0;

		if (evaluationContext.parent() != null) {
			amount = getAmountThroughParents();
		}

		amount += itemTracker.take(result.getRepositoryItemNotNull(), getMax() - amount);

		return Math.min(amount, getMax());
	}

	/**
	 * Pushes the amount onto the count stack.
	 */
	public void pushAmount() {
		this.evaluationContext.stack().push(toolTipContext.getAmount() + toolTipContext.getAmountThroughParents()); // right
	}

	/**
	 * Pops the amount from the count stack.
	 */
	public void popAmount() {
		this.evaluationContext.stack().pop();
	}

	/**
	 * Push the context one layer to the next recipe.
	 * @param recipeResult The next recipe result.
	 * @param isLast Whether it is the last element in the list.
	 * @return The new context.
	 */
	public CraftHelperContext push(RecipeResult<?> recipeResult, boolean isLast) {
		String newPrefix;
		if (prefix.isEmpty()) {
			newPrefix = isLast ? "└ " : "├ ";
		} else {
			newPrefix = prefix.replace("├", "│").replace("└", "  ") + (isLast ? "└ " : "├ ");
		}

		return new CraftHelperContext(
				new ArrayList<>(),
				newPrefix,
				itemTracker,
				recipeResult,
				evaluationContext.push(recipeResult),
				path + "." + recipeResult.getId()
		);
	}
}
