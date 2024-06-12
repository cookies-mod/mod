package dev.morazzer.cookies.mod.commands.dev.tools;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Identifier;

/**
 * Sub command of the /dev tools command to disable a specific flag.
 */
public class DisableToolsSubCommand extends ClientCommand {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("disable").then(argument(
            "tool",
            new RealIdentifierArgument(DevUtils.getEnabledTools(), "cookiesmod", "dev/")
        ).executes(run(context -> {
            Identifier identifier = context.getArgument("tool", Identifier.class);
            boolean disabled = DevUtils.disable(identifier);

            if (!disabled) {
                sendFailedMessage("No devtool found with name" + identifier.toString());
                return;
            }

            sendSuccessMessage("Disabled devtool " + identifier.toString());
        })));
    }

}