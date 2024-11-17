package codes.cookies.mod.features.misc.utils.crafthelper;

import java.util.List;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.features.misc.utils.crafthelper.tooltips.CraftHelperComponent;
import codes.cookies.mod.features.misc.utils.crafthelper.tooltips.DebugComponent;
import codes.cookies.mod.features.misc.utils.crafthelper.tooltips.NormalComponent;
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import codes.cookies.mod.repository.recipes.calculations.RecipeResult;

/**
 * Formatter for the craft helper.
 */
public class CraftHelperFormatter {

	public CraftHelperInstance instance;

	public CraftHelperFormatter(CraftHelperInstance instance) {
		this.instance = instance;
	}

	/**
	 * Formats the recipe calculation result in a list of craft helper elements.
	 * @param result The recipe result.
	 * @param instance The instance this belongs to.
	 * @return The formatted list.
	 */
	public List<CraftHelperComponent> format(RecipeCalculationResult result, CraftHelperInstance instance) {
		final CraftHelperContext craftHelperContext = CraftHelperContext.create(
				result,
				ConfigManager.getConfig().helpersConfig.craftHelper.getSources().toArray(ItemSources[]::new));
		append(craftHelperContext, instance);
		craftHelperContext.components().forEach(CraftHelperComponent::init);
		return craftHelperContext.components();
	}

	private void append(CraftHelperContext context, CraftHelperInstance instance) {
		ToolTipContext toolTipContext = context.toContext();
		toolTipContext.instance = instance;
		final NormalComponent normalComponent = new NormalComponent(toolTipContext);
		normalComponent.setCollapsed(instance.getCollapsed().contains(toolTipContext.path));
		context.components().add(normalComponent);

		if (context.result() instanceof RecipeCalculationResult calculationResult) {
			if (toolTipContext.isDone()) {
				context.components().add(new DebugComponent("Skipping children, parent full!"));
				return;
			}
			for (RecipeResult<?> recipeResult : calculationResult.getRequired()) {
				toolTipContext.hasChildren = true;
				preAppend(
						context,
						recipeResult,
						calculationResult.getRequired().indexOf(recipeResult) == calculationResult.getRequired()
								.size() - 1,
						normalComponent,
						instance);
			}
		} else {
			toolTipContext.childrenDone = false;
		}
	}

	private void preAppend(
			CraftHelperContext context,
			RecipeResult<?> recipeResult,
			boolean isLast,
			CraftHelperComponent current,
			CraftHelperInstance instance
	) {
		context.pushAmount();
		final CraftHelperContext push = context.push(recipeResult, isLast);
		append(push, instance);
		final ToolTipContext child = push.toContext();
		child.isLast = isLast;
		context.toContext().childrenDone = context.toContext().childrenDone && (push.toContext()
				.isDone() || push.toContext().childrenDone);
		context.components()
				.add(new DebugComponent(context.prefix() + "c: %s, d: %s, r: %s".formatted(
						context.toContext().childrenDone,
						push.toContext().isDone(),
						current.hashCode())));
		context.components().addAll(push.components());
		for (CraftHelperComponent component : push.components()) {
			if (component.getParent().isEmpty()) {
				component.setParent(current);
			}
		}
		context.popAmount();
	}
}
