package dev.morazzer.cookies.mod.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.config.screen.ConfigScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

/**
 * Command to open the config screen.
 */
public class OpenConfigCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        ConfigManager.getConfig();
        return this.literal("config").executes(this.run(context -> MinecraftClient.getInstance()
            .send(() -> MinecraftClient.getInstance().setScreen(new ConfigScreen(ConfigManager.getConfigReader())))));
    }
}
