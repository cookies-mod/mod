package dev.morazzer.cookies.mod.commands.dev.tools;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;

/**
 * Sub command of the /dev tools command to enable a specific flag.
 */
public class EnableToolsSubCommand extends ClientCommand {

    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return super.literal("enable").then(super.argument(
            "tool",
            new RealIdentifierArgument(DevUtils.getDisabledTools(), "cookiesmod", "dev/")
        ).executes(super.run(context -> {
            Identifier identifier = context.getArgument("tool", Identifier.class);
            boolean enable = DevUtils.enable(identifier);
            if (!enable) {
				super.sendFailedMessage("No devtool found with name " + identifier.toString());
                return;
            }

            super.sendSuccessMessage("Enabled devtool " + identifier.toString());
        })));
    }

}
