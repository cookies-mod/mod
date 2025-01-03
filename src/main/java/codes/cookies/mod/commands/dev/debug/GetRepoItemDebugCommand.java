package codes.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import codes.cookies.mod.commands.arguments.RealIdentifierArgument;
import codes.cookies.mod.commands.system.ClientCommand;

import codes.cookies.mod.repository.RepositoryItem;

import codes.cookies.mod.utils.cookies.CookiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;

/**
 * Adds the repo item to the player inventory.
 */
public class GetRepoItemDebugCommand extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		List<Identifier> identifiers = new ArrayList<>();
		RepositoryItem.getItemMap().keySet().forEach(
				item -> identifiers.add(
						Identifier.of("skyblock", item.replace(":", "/").replace("-", "/").replace(";", "/").toLowerCase(Locale.ROOT))
				)
		);
		return literal("get_item")
				.requires(super::ensureDevEnvironment)
				.then(argument("id", new RealIdentifierArgument(identifiers, "skyblock")).executes(run(this::execute)));
	}

	private void execute(CommandContext<FabricClientCommandSource> context) {
		final Identifier id = context.getArgument("id", Identifier.class);
		final RepositoryItem repositoryItem = RepositoryItem.of(id.getPath());
		CookiesUtils.getPlayer().ifPresent(player -> player.getInventory().insertStack(repositoryItem.constructItemStack()));
	}
}
