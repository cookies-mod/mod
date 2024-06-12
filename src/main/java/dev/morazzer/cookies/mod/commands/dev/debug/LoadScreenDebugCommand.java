package dev.morazzer.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.utils.dev.DevInventoryUtils;
import java.util.Optional;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

/**
 * Debug command to load previously saved screens.
 * <br>
 * usage: /dev debug loadScreen {@literal <screen id>}
 */
public class LoadScreenDebugCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("loadScreen")
            .then(
                argument("id", StringArgumentType.greedyString())
                    .executes(run(this::loadScreen))
            );
    }

    private void loadScreen(CommandContext<FabricClientCommandSource> context) {
        if (!MinecraftClient.getInstance().world.isClient) {
            sendFailedMessage("Can't use this command on non local worlds!");
            return;
        }

        final Optional<Screen> id = DevInventoryUtils.createInventory(context.getArgument("id", String.class));
        if (id.isPresent()) {
            sendSuccessMessage("Opening screen %s".formatted(context.getArgument("id", String.class)));
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(id.get()));
        } else {
            sendFailedMessage("Failed to open screen!");
        }
    }
}
