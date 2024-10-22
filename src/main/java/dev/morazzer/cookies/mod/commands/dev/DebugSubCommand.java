package dev.morazzer.cookies.mod.commands.dev;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.mod.commands.dev.debug.ApiDebug;
import dev.morazzer.cookies.mod.commands.dev.debug.GetRegionDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.GetRepoItemDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.IsUsingMod;
import dev.morazzer.cookies.mod.commands.dev.debug.LoadScreenDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.OpenCraftHelperPlacementScreen;
import dev.morazzer.cookies.mod.commands.dev.debug.PrintModStuffDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.ProfileDataDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.RecipeCalculationDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.RecipeDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.RenderDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.SackMessageDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.SendMessageDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.SetSelectedCraftHelperItemDebugCommand;
import dev.morazzer.cookies.mod.commands.dev.debug.SwapProfilesDebugCommand;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import org.jetbrains.annotations.NotNull;

/**
 * Sub command of the /dev command that allows access to various debug methods.
 * usage: /dev debug
 */
public class DebugSubCommand extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		final LiteralArgumentBuilder<FabricClientCommandSource> debug = super.literal("debug");
		ClientCommand[] clientCommands = {
				new RecipeDebugCommand(),
				new RenderDebugCommand(),
				new RecipeCalculationDebugCommand(),
				new SwapProfilesDebugCommand(),
				new SetSelectedCraftHelperItemDebugCommand(),
				new SackMessageDebugCommand(),
				new LoadScreenDebugCommand(),
				new SendMessageDebugCommand(),
				new GetRegionDebugCommand(),
				new OpenCraftHelperPlacementScreen(),
				new ProfileDataDebugCommand(),
				new ApiDebug(),
				new IsUsingMod(),
				new GetRepoItemDebugCommand(),
				new PrintModStuffDebugCommand()
		};

		for (ClientCommand clientCommand : clientCommands) {
			debug.then(clientCommand.getCommand());
		}
		return debug;
	}
}
