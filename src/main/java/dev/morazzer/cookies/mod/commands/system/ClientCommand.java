package dev.morazzer.cookies.mod.commands.system;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Helper class for client-side commands.
 */

public abstract class ClientCommand implements ClientCommandHelper {

    private static final Identifier identifier = Identifier.of("cookie", "commands");

    /**
     * Registers all commands on the {@linkplain CommandDispatcher}.
     *
     * @param dispatcher The dispatcher.
     * @param commands   The commands.
     */
    public static void loadCommands(
        @NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
        @NotNull ClientCommand... commands
    ) {
        for (ClientCommand command : commands) {
            command.register(dispatcher);
        }
    }

    /**
     * Registers the command instance on the {@linkplain CommandDispatcher}.
     *
     * @param dispatcher The dispatcher.
     */
    public void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = this.getCommand();
        if (!this.isAvailableOnServers()) {
            Predicate<FabricClientCommandSource> requirement = command.getRequirement();
            command.requires(fabricClientCommandSource -> {
                ClientWorld world = MinecraftClient.getInstance().world;
                return (this.isAvailableOnServers()
                        || world != null
                           && world.isClient())
                       && requirement.test(fabricClientCommandSource);
            });
        }

        LiteralCommandNode<FabricClientCommandSource> register = dispatcher.register(command);
        for (String alias : getAliases()) {
            dispatcher.register(literal(alias)
                .executes(command.getCommand())
                .requires(command.getRequirement())
                .redirect(register, getRedirectModifier(alias)));

            if (alias.startsWith("cookie")) {
                alias = alias.substring(6);
            }

            String namespace = String.format("%s:%s", identifier.getNamespace(), alias);
            dispatcher.register(literal(namespace)
                .executes(command.getCommand())
                .requires(command.getRequirement())
                .redirect(register, getRedirectModifier(namespace)));
        }

        String name = register.getName();
        if (name.startsWith("cookie")) {
            name = name.substring(6);
        }
        String namespace = String.format("%s:%s", identifier.getNamespace(), name);

        dispatcher.register(literal(namespace)
            .executes(command.getCommand())
            .requires(command.getRequirement())
            .redirect(register, getRedirectModifier(namespace)));
    }

    /**
     * Gets the command tree.
     *
     * @return The command.
     */
    @NotNull
    public abstract LiteralArgumentBuilder<FabricClientCommandSource> getCommand();

    /**
     * Whether the command should be available on servers or not.
     *
     * @return The value.
     */
    @Contract(pure = true)
    public boolean isAvailableOnServers() {
        return true;
    }

    /**
     * Gets the aliases of the command.
     *
     * @return The aliases.
     */
    @NotNull
    @Contract(pure = true)
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    /**
     * Creates a redirect modifier.
     *
     * @param commandName The commands name.
     * @return The modifier.
     */
    @NotNull
    public SingleRedirectModifier<FabricClientCommandSource> getRedirectModifier(
        @SuppressWarnings("unused") @NotNull String commandName
    ) {
        return CommandContext::getSource;
    }
}
