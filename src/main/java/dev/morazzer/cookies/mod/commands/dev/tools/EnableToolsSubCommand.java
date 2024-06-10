package dev.morazzer.cookies.mod.commands.dev.tools;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.utils.DevUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Identifier;

/**
 * Sub command of the /dev tools command to enable a specific flag.
 */
public class EnableToolsSubCommand extends ClientCommand {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("enable").then(argument(
            "tool",
            new RealIdentifierArgument(DevUtils.getDisabledTools(), "cookiesmod", "dev/")
        ).executes(run(context -> {
            Identifier identifier = context.getArgument("tool", Identifier.class);
            boolean enable = DevUtils.enable(identifier);
            if (!enable) {
                sendFailedMessage("No devtool found with name " + identifier.toString());
                return;
            }

            sendSuccessMessage("Enabled devtool " + identifier.toString());
        })));
    }

}