package codes.cookies.mod.commands.dev;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.utils.dev.DevUtils;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Subcommand that toggles the extra logging in chat for certain keys.
 */
public class ExtraLoggingCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("extra-logging")
            .then(
                literal("add")
                    .then(
                        argument("key", StringArgumentType.greedyString()).executes(run(this::add))
                    )
            ).then(
                literal("remove")
                    .then(
                        argument("key", StringArgumentType.greedyString())
                            .suggests(this::suggestRemove)
                            .executes(run(this::remove))
                    )
            );
    }

    private void add(CommandContext<FabricClientCommandSource> context) {
        final String key = context.getArgument("key", String.class);
        final boolean contains = DevUtils.getEnabledExtraLogging().contains(key);
        if (contains) {
            sendFailedMessage("Key %s is already enabled".formatted(key));
            return;
        }
        DevUtils.getEnabledExtraLogging().add(key);
        sendSuccessMessage("Added key %s to extra logging".formatted(key));
    }

    private CompletableFuture<Suggestions> suggestRemove(CommandContext<FabricClientCommandSource> context,
                                                         SuggestionsBuilder suggestionsBuilder) {
        CommandSource.suggestMatching(DevUtils.getEnabledExtraLogging(), suggestionsBuilder);
        return suggestionsBuilder.buildFuture();
    }

    private void remove(CommandContext<FabricClientCommandSource> context) {
        final String key = context.getArgument("key", String.class);
        final boolean contains = DevUtils.getEnabledExtraLogging().contains(key);
        if (!contains) {
            sendFailedMessage("Key %s is not active".formatted(key));
            return;
        }
        DevUtils.getEnabledExtraLogging().remove(key);
        sendSuccessMessage("Removed key %s from extra logging".formatted(key));
    }
}
