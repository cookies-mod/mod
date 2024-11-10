package codes.cookies.mod.commands.dev.debug;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.commands.system.ClientCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class HudEditScreenDebugCommand extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return literal("hud_edit_screen").executes(run(CookiesMod::openHudScreen));
	}
}
