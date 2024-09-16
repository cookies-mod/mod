package dev.morazzer.cookies.mod.commands.dev.debug;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;
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
        return super.literal("getRegion").executes(super.run(this::getRegion));
    }

    private void getRegion(CommandContext<FabricClientCommandSource> context) {
		super.sendInformation("Current Region: " + LocationUtils.getRegion().name());
		super.sendInformation("Current Island: " + LocationUtils.getRegion().island.name());
		super.sendInformation("Is Skyblock: " + SkyblockUtils.isCurrentlyInSkyblock());
    }
}
