package codes.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import codes.cookies.mod.commands.arguments.RealIdentifierArgument;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.Recipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Debug command to get information about an item.
 * <br>
 * usage: /dev debug recipe {@literal <skyblock id>}
 */
public class RecipeDebugCommand extends ClientCommand {

    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        List<Identifier> identifiers = new ArrayList<>();
        RepositoryItem.getItemMap().keySet().forEach(
            item -> identifiers.add(
                Identifier.of("skyblock", item.replace(":", "/").replace("-", "/").replace(";", "/").toLowerCase(Locale.ROOT))
            )
        );

        return literal("recipe")
            .then(
                argument(
                    "item",
                    new RealIdentifierArgument(identifiers)
                ).executes(run(this::printRecipes))
            );
    }

    private void printRecipes(CommandContext<FabricClientCommandSource> context) {
        final Identifier item = context.getArgument("item", Identifier.class);
        final RepositoryItem repositoryItem = RepositoryItem.of(item.getPath());

        sendInformation("Recipes for " + repositoryItem.getName());
        sendRawMessage(
            "Used in %s recipes as ingredient".formatted(
                repositoryItem.getUsedInRecipeAsIngredient().size()
            )
        );
        for (Recipe recipe : repositoryItem.getUsedInRecipeAsIngredient()) {
            if (recipe.getOutput() == null) {
                continue;
            }
            sendRawMessage(
                recipe.getOutput().getRepositoryItem() == null ? recipe.getOutput().getId() :
                    recipe.getOutput().getRepositoryItem().getName().getString()
            );
        }
        sendRawMessage("");
        sendRawMessage(
            "There are %s Recipes for %s".formatted(
                repositoryItem.getRecipes().size(),
                repositoryItem.getName()
            )
        );
    }
}
