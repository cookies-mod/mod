package dev.morazzer.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipePrinter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Debug command to show the result of a recipe calculation.
 * <br>
 * usage: /dev debug recipeCalculation {@literal <skyblock id>}
 */
public class RecipeCalculationDebugCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        List<Identifier> identifiers = new ArrayList<>();
        RepositoryItem.getItemMap().keySet().forEach(
            item -> identifiers.add(
                Identifier.of("skyblock", item.replace(":", "/").replace("-", "/").replace(";", "/").toLowerCase(Locale.ROOT))
            )
        );

        return literal("recipeCalculation")
            .then(
                argument(
                    "item",
                    new RealIdentifierArgument(identifiers)
                ).executes(run(this::calculate))
            );
    }

    private void calculate(CommandContext<FabricClientCommandSource> context) {
        final Identifier item = context.getArgument("item", Identifier.class);
        final RepositoryItem repositoryItem = RepositoryItem.of(item.getPath());

        final Optional<Recipe> first = repositoryItem.getRecipes().stream().findFirst();
        if (first.isEmpty()) {
            sendFailedMessage("Item doesn't have a recipe");
            return;
        }

        final Recipe recipe = first.get();
        final RecipeCalculationResult calculate = RecipeCalculator.calculate(recipe).getResult().orElseThrow();
        final String s = RecipePrinter.printRecipe(calculate);

        sendInformation("Recipes for " + repositoryItem.getName());
        sendRawMessage(s);
    }
}
