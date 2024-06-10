package dev.morazzer.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.features.misc.utils.CraftHelper;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class SetSelectedCraftHelperItemDebugCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        List<Identifier> identifiers = new ArrayList<>();
        RepositoryItem.getItemMap().keySet().forEach(
            item -> identifiers.add(
                Identifier.of("skyblock", item.toLowerCase(Locale.ROOT).replace(":", "_").toLowerCase(Locale.ROOT))
            )
        );

        return literal("setSelectedCraftHelperItem")
            .then(
                argument(
                    "item",
                    new RealIdentifierArgument(identifiers)
                ).executes(run(this::setSelectedItem))
            );
    }

    private void setSelectedItem(CommandContext<FabricClientCommandSource> context) {
        final Identifier item = context.getArgument("item", Identifier.class);
        final RepositoryItem repositoryItem = RepositoryItem.of(item.getPath());

        if (repositoryItem != null) {
            CraftHelper.setSelectedItem(repositoryItem);
            sendSuccessMessage("Switched to item " + item);
        } else {
            sendFailedMessage("Could not find item " + item.getPath());
        }
    }
}