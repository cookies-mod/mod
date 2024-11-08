package codes.cookies.mod.commands.dev.tools;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import codes.cookies.mod.commands.system.ClientCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Dev sub command to toggle tools/flags.
 */
public class ToolsSubCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        final LiteralArgumentBuilder<FabricClientCommandSource> debug = literal("tools");
        ClientCommand[] clientCommands = new ClientCommand[] {
            new EnableToolsSubCommand(),
            new DisableToolsSubCommand()
        };

        for (ClientCommand clientCommand : clientCommands) {
            debug.then(clientCommand.getCommand());
        }
        return debug;
    }
}
