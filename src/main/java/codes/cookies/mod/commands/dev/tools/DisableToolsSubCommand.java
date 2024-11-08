package codes.cookies.mod.commands.dev.tools;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import codes.cookies.mod.commands.arguments.RealIdentifierArgument;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.utils.dev.DevUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;

/**
 * Sub command of the /dev tools command to disable a specific flag.
 */
public class DisableToolsSubCommand extends ClientCommand {

    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return super.literal("disable").then(super.argument(
            "tool",
            new RealIdentifierArgument(DevUtils.getEnabledTools(), "cookiesmod", "dev/")
        ).executes(super.run(context -> {
            Identifier identifier = context.getArgument("tool", Identifier.class);
            boolean disabled = DevUtils.disable(identifier);

            if (!disabled) {
				super.sendFailedMessage("No devtool found with name" + identifier.toString());
                return;
            }

			super.sendSuccessMessage("Disabled devtool " + identifier.toString());
        })));
    }

}
