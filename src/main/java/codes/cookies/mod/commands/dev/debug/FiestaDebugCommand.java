package codes.cookies.mod.commands.dev.debug;

import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.features.mining.fiesta.MiningFiesta;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import org.jetbrains.annotations.NotNull;

public class FiestaDebugCommand extends ClientCommand {

	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return literal("fiesta")
				.then(literal("start").executes(run(MiningFiesta::start)))
				.then(literal("stop").executes(run(MiningFiesta::stop)));
	}
}
