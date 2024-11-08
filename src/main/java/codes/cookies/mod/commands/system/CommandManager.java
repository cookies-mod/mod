package codes.cookies.mod.commands.system;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.jetbrains.annotations.NotNull;

/**
 * Mod to load all commands.
 */
public class CommandManager {

    private static final List<ClientCommand> COMMANDS = new ArrayList<>();

    /**
     * Adds a list of commands.
     *
     * @param commands The commands to add.
     */
    public static void addCommands(@NotNull ClientCommand... commands) {
        COMMANDS.addAll(Arrays.asList(commands));
    }

    /**
     * Registers the command manger, so it can create the commands.
     */
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register(CommandManager::registerCommands);
    }

    private static void registerCommands(
        CommandDispatcher<FabricClientCommandSource> dispatcher,
        CommandRegistryAccess registryAccess
    ) {
        ClientCommand.loadCommands(dispatcher, COMMANDS.toArray(new ClientCommand[0]));
    }
}
