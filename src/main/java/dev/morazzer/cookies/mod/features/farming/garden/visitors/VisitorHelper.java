package dev.morazzer.cookies.mod.features.farming.garden.visitors;

/**
 * Adds the amount of items that visitors need to their description.
 *
 * FIXME discontinued for now
 */
public class VisitorHelper {

    //@SuppressWarnings("MissingJavadoc")
    //public VisitorHelper() {
    //    ItemLoreEvent.EVENT_ITEM.register(ExceptionHandler.wrap(this::modify));
    //}
//
    //private void modify(ItemStack itemStack, List<MutableText> lines) {
    //    if (!this.shouldModify(itemStack)) {
    //        return;
    //    }
//
    //    final ListIterator<MutableText> iterator = lines.listIterator();
    //    while (iterator.hasNext()) {
    //        MutableText line = iterator.next();
    //        String literalContent = line.getString().trim();
//
    //        if (literalContent.isEmpty() || literalContent.equals(lines.getFirst().getString().trim()) ||
    //            literalContent.equals("Items Required: ")) {
    //            continue;
    //        }
    //        if (literalContent.equals("Rewards:")) {
    //            break;
    //        }
//
    //        final StackCountContext stackCountContext = new StackCountContext();
    //        if (literalContent.matches("([A-Za-z ]+)(?: x[\\d,]+)?")) {
    //            this.modify(literalContent, iterator, stackCountContext);
    //        }
    //    }
    //}
//
    //private boolean shouldModify(ItemStack itemStack) {
    //    return SkyblockUtils.isCurrentlyInSkyblock() && LocationUtils.Island.GARDEN.isActive() &&
    //           ConfigManager.getConfig().farmingConfig.visitorMaterialHelper.getValue() &&
    //           Plot.getCurrentPlot().isBarn() && itemStack.getItem() == Items.GREEN_TERRACOTTA &&
    //           itemStack.getName().getString().equals("Accept Offer");
    //}
//
    //private void modify(
    //    String literalContent, ListIterator<MutableText> iterator, StackCountContext stackCountContext) {
    //    final String name;
    //    final int amount;
    //    if (literalContent.matches(".*? x[\\d,]+")) {
    //        name = literalContent.replaceAll("([A-Za-z ]+) x[\\d,]+", "$1");
    //        amount = Integer.parseInt(literalContent.replaceAll("\\D", ""));
    //    } else {
    //        name = literalContent;
    //        amount = 1;
    //    }
    //    final Optional<RepositoryItem> repositoryItem = RepositoryItem.ofName(name);
    //    if (repositoryItem.isEmpty()) {
    //        iterator.add(Text.literal(" -> ").append(Text.translatable(TranslationKeys.ITEM_NOT_FOUND, name)).formatted(Formatting.RED));
    //        return;
    //    }
//
    //    if (repositoryItem.get().getRecipes().isEmpty()) {
    //        return;
    //    }
//
	//	final Result<RecipeCalculationResult, String> calculate = RecipeCalculator.calculate(repositoryItem.get());
	//	if (calculate.isError()) {
	//		iterator.add(Text.literal(calculate.getError().orElse("An internal error occurred!")));
	//		return;
	//	}
//
	//	final RecipeCalculationResult recipe = calculate.getResult().orElse(null);
	//	if (recipe == null) {
	//		return;
	//	}
    //    final RecipeCalculationResult multiply = recipe.multiply(amount);
//
    //    iterator.remove();
    //    List<MutableText> craft = new ArrayList<>();
    //    CraftHelper.append(
    //        " ",
    //        craft,
    //        multiply,
    //        0,
    //        new EvaluationContext(multiply, null),
    //        stackCountContext,
    //        this::format);
    //    craft.forEach(iterator::add);
    //}
//
    //private MutableText format(
    //    String prefix,
    //    String id,
    //    RepositoryItem repositoryItem,
    //    int amount,
    //    int amountOfItem,
	//	long lastForgeTime,
	//	boolean usedForge,
    //    boolean childrenFinished,
    //    int depth) {
    //    if (depth == 0) {
    //        prefix = "";
    //    }
    //    final MutableText literal = Text.empty().append(Text.literal(prefix).formatted(Formatting.DARK_GRAY));
//
    //    final double percentage = (double) amountOfItem / amount;
    //    //noinspection DataFlowIssue
    //    final int color =
    //        ColorUtils.calculateBetween(Formatting.RED.getColorValue(), Formatting.GREEN.getColorValue(), percentage);
    //    DateTimeFormatter.ofPattern("");
    //    final String formatted =
    //        "%s/%s".formatted(MathUtils.NUMBER_FORMAT.format(amountOfItem), MathUtils.NUMBER_FORMAT.format(amount));
    //    literal.append(Text.literal(formatted).withColor(color));
    //    literal.append(" ");
    //    if (repositoryItem != null) {
    //        literal.append(repositoryItem.getFormattedName());
    //    } else {
    //        literal.append(id);
    //    }
//
    //    return literal;
    //}

}
