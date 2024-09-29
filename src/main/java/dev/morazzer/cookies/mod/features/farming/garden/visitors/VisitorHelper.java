package dev.morazzer.cookies.mod.features.farming.garden.visitors;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.ItemLoreEvent;
import dev.morazzer.cookies.mod.features.farming.garden.Plot;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelper;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.EvaluationContext;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.StackCountContext;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.ColorUtils;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;
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

    @SuppressWarnings("MissingJavadoc")
    public VisitorHelper() {
        ItemLoreEvent.EVENT_ITEM.register(ExceptionHandler.wrap(this::modify));
    }

    private void modify(ItemStack itemStack, List<MutableText> lines) {
        if (!this.shouldModify(itemStack)) {
            return;
        }

        final ListIterator<MutableText> iterator = lines.listIterator();
        while (iterator.hasNext()) {
            MutableText line = iterator.next();
            String literalContent = line.getString().trim();

            if (literalContent.isEmpty() || literalContent.equals(lines.getFirst().getString().trim()) ||
                literalContent.equals("Items Required: ")) {
                continue;
            }
            if (literalContent.equals("Rewards:")) {
                break;
            }

            final StackCountContext stackCountContext = new StackCountContext();
            if (literalContent.matches("([A-Za-z ]+)(?: x[\\d,]+)?")) {
                this.modify(literalContent, iterator, stackCountContext);
            }
        }
    }

    private boolean shouldModify(ItemStack itemStack) {
        return SkyblockUtils.isCurrentlyInSkyblock() && LocationUtils.Island.GARDEN.isActive() &&
               ConfigManager.getConfig().farmingConfig.visitorMaterialHelper.getValue() &&
               Plot.getCurrentPlot().isBarn() && itemStack.getItem() == Items.GREEN_TERRACOTTA &&
               itemStack.getName().getString().equals("Accept Offer");
    }

    private void modify(
        String literalContent, ListIterator<MutableText> iterator, StackCountContext stackCountContext) {
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
            iterator.add(Text.literal(" -> ").append(Text.translatable(TranslationKeys.ITEM_NOT_FOUND, name)).formatted(Formatting.RED));
            return;
        }

        if (repositoryItem.get().getRecipes().isEmpty()) {
            return;
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
            new EvaluationContext(multiply, null),
            stackCountContext,
            this::format);
        craft.forEach(iterator::add);
    }

    private MutableText format(
        String prefix,
        String id,
        RepositoryItem repositoryItem,
        int amount,
        int amountOfItem,
		long lastForgeTime,
		boolean usedForge,
        boolean childrenFinished,
        int depth) {
        if (depth == 0) {
            prefix = "";
        }
        final MutableText literal = Text.empty().append(Text.literal(prefix).formatted(Formatting.DARK_GRAY));

        final double percentage = (double) amountOfItem / amount;
        //noinspection DataFlowIssue
        final int color =
            ColorUtils.calculateBetween(Formatting.RED.getColorValue(), Formatting.GREEN.getColorValue(), percentage);
        DateTimeFormatter.ofPattern("");
        final String formatted =
            "%s/%s".formatted(MathUtils.NUMBER_FORMAT.format(amountOfItem), MathUtils.NUMBER_FORMAT.format(amount));
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
