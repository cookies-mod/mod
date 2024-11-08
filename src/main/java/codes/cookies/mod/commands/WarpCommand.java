package codes.cookies.mod.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import codes.cookies.mod.commands.system.ClientCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Blueprint for warp shortcut commands.
 */
public class WarpCommand extends ClientCommand {
    private final String commandName;
    private final String warpName;

    /**
     * Creates a new warp shortcut command instance.
     *
     * @param mapEntry A map entry with the command name as the key and the warp name as the value.
     */
    public WarpCommand(Map.Entry<String, String> mapEntry) {
        this.commandName = mapEntry.getKey();
        this.warpName = mapEntry.getValue();
    }

    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal(commandName).executes(run(context -> {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendCommand("warp " + warpName);
        }));
    }
}
