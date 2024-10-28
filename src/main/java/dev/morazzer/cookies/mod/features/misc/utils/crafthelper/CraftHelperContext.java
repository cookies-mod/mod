package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips.CraftHelperComponent;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeResult;

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

	public int getMax() {
		return result.getAmount();
	}

	public int getAmountThroughParents() {
		if (evaluationContext.parent() == null) {
			return 0;
		}
		int amount = (evaluationContext.stack().peek() * (evaluationContext.recipeResult()
				.getAmount() / evaluationContext.parent().recipeResult().getAmount()));
		return Math.min(amount, result.getAmount());
	}

	public int amount() {
		int amount = 0;

		if (evaluationContext.parent() != null) {
			amount = getAmountThroughParents();
		}

		amount += itemTracker.take(result.getRepositoryItemNotNull(), getMax() - amount);

		return Math.min(amount, getMax());
	}

	public void pushAmount() {
		this.evaluationContext.stack().push(toolTipContext.getAmount() + toolTipContext.getAmountThroughParents()); // right
	}

	public void popAmount() {
		this.evaluationContext.stack().pop();
	}

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
