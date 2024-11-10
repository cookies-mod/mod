package codes.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;

/**
 * Debug command to imitate a message that has been sent by the server.
 * <br>
 * usage: /dev debug sendMessage {@literal <message>}
 */
public class SendMessageDebugCommand extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return super.literal("sendMessage")
				.then(super.argument("message", StringArgumentType.greedyString())
						.executes(super.run(this::sendMessage)));
	}

	private void sendMessage(CommandContext<FabricClientCommandSource> context) {
		CookiesUtils.sendMessage(Text.literal(context.getArgument("message", String.class)));
	}
}
