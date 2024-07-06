package dev.morazzer.cookies.mod.features.farming.garden.visitors;

import dev.morazzer.cookies.mod.events.ItemLoreEvent;
import dev.morazzer.cookies.mod.features.farming.garden.Plot;
import dev.morazzer.cookies.mod.features.misc.utils.CraftHelper;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import dev.morazzer.cookies.mod.utils.ColorUtils;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Adds the amount of items that visitors need to their description.
 */
public class VisitorHelper {

    public VisitorHelper() {
        ItemLoreEvent.EVENT_ITEM.register(this::modify);
    }

    private void modify(ItemStack itemStack, List<MutableText> lines) {
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return;
        }
        if (!LocationUtils.Island.GARDEN.isActive()) {
            return;
        }
        if (!Plot.getCurrentPlot().isBarn()) {
            return;
        }
        if (itemStack.getItem() != Items.GREEN_TERRACOTTA) {
            return;
        }
        if (!itemStack.getName().getString().equals("Accept Offer")) {
            return;
        }

        final ListIterator<MutableText> iterator = lines.listIterator();
        while (iterator.hasNext()) {
            MutableText line = iterator.next();
            String literalContent = line.getString().trim();

            if (literalContent.equals(lines.getFirst().getString().trim())) {
                continue;
            }
            if (literalContent.equals("Items Required: ")) {
                continue;
            }
            if (literalContent.equals("Rewards:")) {
                break;
            }
            if (literalContent.isEmpty()) {
                continue;
            }

            final CraftHelper.StackCountContext stackCountContext = new CraftHelper.StackCountContext();
            if (literalContent.matches("([A-Za-z ]+)(?: x[\\d,]+)?")) {
                final String name;
                final int amount;
                if (literalContent.matches(".*? x[\\d,]+")) {
                    name = literalContent.replaceAll("([A-Za-z ]+) x[\\d,]+", "$1");
                    amount = Integer.parseInt(literalContent.replaceAll("\\D", ""));
                } else {
                    name = literalContent;
                    amount = 1;
                }
                final Optional<RepositoryItem> repositoryItem = RepositoryItem.ofName(name);
                if (repositoryItem.isEmpty()) {
                    iterator.add(Text.literal(" -> Could not find item %s".formatted(name)).formatted(Formatting.RED));
                    continue;
                }

                final RecipeCalculationResult recipe = RecipeCalculator.calculate(repositoryItem.get());
                final RecipeCalculationResult multiply = recipe.multiply(amount);

                iterator.remove();
                List<MutableText> craft = new ArrayList<>();
                CraftHelper.append(
                    " ",
                    craft,
                    multiply,
                    0,
                    new CraftHelper.EvaluationContext(multiply, null),
                    stackCountContext,
                    this::format);
                craft.forEach(iterator::add);
            }
        }
    }

    private MutableText format(
        String prefix,
        String id,
        RepositoryItem repositoryItem,
        int amount,
        int amountOfItem,
        boolean childrenFinished,
        int depth) {
        if (depth == 0) {
            prefix = "";
        }
        final MutableText literal = Text.empty().append(Text.literal(prefix).formatted(Formatting.DARK_GRAY));

        final double percentage = (double) amountOfItem / amount;
        //noinspection DataFlowIssue
        final int color = ColorUtils.calculateBetween(
            Formatting.RED.getColorValue(),
            Formatting.GREEN.getColorValue(),
            percentage);
        DateTimeFormatter.ofPattern("");
        final String formatted = "%s/%s".formatted(MathUtils.NUMBER_FORMAT.format(amountOfItem),
            MathUtils.NUMBER_FORMAT.format(amount));
        literal.append(Text.literal(formatted).withColor(color));
        literal.append(" ");
        if (repositoryItem != null) {
            literal.append(repositoryItem.getFormattedName());
        } else {
            literal.append(id);
        }

        return literal;
    }

}
