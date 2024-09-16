package dev.morazzer.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;

import dev.morazzer.cookies.mod.utils.cookies.CookiesBackendUtils;

import java.util.UUID;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import org.jetbrains.annotations.NotNull;

public class IsUsingMod extends ClientCommand {

	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return super.literal("using")
				.requires(super::ensureDevEnvironment)
				.then(super.argument("name", StringArgumentType.string()).executes(super.run(this::execute)));
	}

	private void execute(CommandContext<FabricClientCommandSource> context) {
		final String name = context.getArgument("name", String.class);

		final boolean b = CookiesBackendUtils.usesMod(UUID.fromString(name));
		if (b) {
			super.sendSuccessMessage("Is using the mod!");
		} else {
			super.sendFailedMessage("Is not using the mod!");
		}
	}
}
