package codes.cookies.mod.commands.dev;

import codes.cookies.mod.commands.dev.debug.FiestaDebugCommand;
import codes.cookies.mod.commands.dev.debug.HudEditScreenDebugCommand;
import codes.cookies.mod.commands.dev.debug.PrintCorpseLocationsDebugCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import codes.cookies.mod.commands.dev.debug.ApiDebug;
import codes.cookies.mod.commands.dev.debug.GetRegionDebugCommand;
import codes.cookies.mod.commands.dev.debug.GetRepoItemDebugCommand;
import codes.cookies.mod.commands.dev.debug.IsUsingMod;
import codes.cookies.mod.commands.dev.debug.LoadScreenDebugCommand;
import codes.cookies.mod.commands.dev.debug.OpenCraftHelperPlacementScreen;
import codes.cookies.mod.commands.dev.debug.PrintModStuffDebugCommand;
import codes.cookies.mod.commands.dev.debug.ProfileDataDebugCommand;
import codes.cookies.mod.commands.dev.debug.RecipeCalculationDebugCommand;
import codes.cookies.mod.commands.dev.debug.RecipeDebugCommand;
import codes.cookies.mod.commands.dev.debug.RenderDebugCommand;
import codes.cookies.mod.commands.dev.debug.SackMessageDebugCommand;
import codes.cookies.mod.commands.dev.debug.SendMessageDebugCommand;
import codes.cookies.mod.commands.dev.debug.SetSelectedCraftHelperItemDebugCommand;
import codes.cookies.mod.commands.dev.debug.SwapProfilesDebugCommand;
import codes.cookies.mod.commands.system.ClientCommand;

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
				new PrintModStuffDebugCommand(),
				new HudEditScreenDebugCommand(),
				new PrintCorpseLocationsDebugCommand(),
				new FiestaDebugCommand()
		};

		for (ClientCommand clientCommand : clientCommands) {
			debug.then(clientCommand.getCommand());
		}
		return debug;
	}
}
