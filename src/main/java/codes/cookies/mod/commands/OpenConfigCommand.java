package codes.cookies.mod.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.commands.system.ClientCommand;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import org.jetbrains.annotations.NotNull;

/**
 * Command to open the config screen.
 */
public class OpenConfigCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return this.literal("config").executes(this.run(CookiesMod::openConfig));
    }
}
