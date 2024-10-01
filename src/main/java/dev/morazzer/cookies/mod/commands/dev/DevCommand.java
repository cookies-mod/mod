package dev.morazzer.cookies.mod.commands.dev;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.mod.commands.dev.tools.ToolsSubCommand;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Command that contains all dev related methods.
 */
public class DevCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        final LiteralArgumentBuilder<FabricClientCommandSource> dev = super.literal("dev");

        ClientCommand[] clientCommands = new ClientCommand[] {
            new DebugSubCommand(),
            new ToolsSubCommand(),
            new ExtraLoggingCommand()
        };

        for (ClientCommand clientCommand : clientCommands) {
            dev.then(clientCommand.getCommand());
        }

        return dev;
    }


    @Override
    public @NotNull List<String> getAliases() {
        return List.of("development");
    }
}
