package dev.morazzer.cookies.mod.commands.dev.debug;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Debug command to print information about the current area and island.
 * <br>
 * usage: /dev debug getRegion
 */
public class GetRegionDebugCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("getRegion").executes(run(this::getRegion));
    }

    private void getRegion(CommandContext<FabricClientCommandSource> context) {
        sendInformation("Current Region: " + LocationUtils.getRegion().name());
        sendInformation("Current Island: " + LocationUtils.getRegion().island.name());
    }
}
