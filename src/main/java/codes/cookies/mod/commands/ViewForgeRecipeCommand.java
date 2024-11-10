package codes.cookies.mod.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.config.ConfigKeys;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.ForgeRecipe;
import codes.cookies.mod.repository.recipes.Recipe;
import codes.cookies.mod.screen.inventory.ForgeRecipeScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Command to view the recipe of an item, if it has a forge recipe.
 * <br>
 * Usage: /viewforgerecipe {@literal <name/id> }
 */
public class ViewForgeRecipeCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("viewforgerecipe").then(argument("recipe", StringArgumentType.greedyString()).suggests(this::suggest)
            .executes(run(this::open)));
    }

    private CompletableFuture<Suggestions> suggest(
        CommandContext<FabricClientCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        final List<RepositoryItem> list =
            Recipe.ALL_FORGE_RECIPES.stream().map(ForgeRecipe::getOutput).map(Ingredient::getRepositoryItem).toList();
        List<String> suggest = new ArrayList<>();
        list.stream().map(RepositoryItem::getInternalId).forEach(suggest::add);
        list.stream().map(RepositoryItem::getName).map(Text::getString).forEach(suggest::add);
        return CommandSource.suggestMatching(suggest, suggestionsBuilder);
    }

    private void open(CommandContext<FabricClientCommandSource> context) {
        if (!ConfigKeys.MISC_FORGE_RECIPE.get()) {
            sendFailedMessage("Forge recipes aren't enabled, to use this command enable them in the config! (/cookie:config)");
            return;
        }

        final String recipe = context.getArgument("recipe", String.class);

        final List<RepositoryItem> list =
            Recipe.ALL_FORGE_RECIPES.stream().map(ForgeRecipe::getOutput).map(Ingredient::getRepositoryItem).toList();
        final RepositoryItem repositoryItem = list.stream().filter(this.doesMatch(recipe)).findFirst().orElse(null);
        if (repositoryItem == null) {
            sendFailedMessage("No recipe found for '" + recipe + "'");
            return;
        }

        CookiesMod.openScreen(new ForgeRecipeScreen(repositoryItem.getRecipes()
            .stream()
            .filter(ForgeRecipe.class::isInstance)
            .map(ForgeRecipe.class::cast)
            .findFirst()
            .orElse(null), null));
    }

    private Predicate<? super RepositoryItem> doesMatch(String recipe) {
        return repositoryItem -> repositoryItem.getInternalId().equalsIgnoreCase(recipe) ||
                                 repositoryItem.getName().getString().equalsIgnoreCase(recipe);
    }
}
