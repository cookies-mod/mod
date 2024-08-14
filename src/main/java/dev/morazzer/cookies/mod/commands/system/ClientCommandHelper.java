package dev.morazzer.cookies.mod.commands.system;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.CookiesUtils;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for creating client-side commands.
 */
public interface ClientCommandHelper {
    /**
     * The logger for the commands.
     */
    Logger LOGGER = LoggerFactory.getLogger(ClientCommand.class);

    /**
     * Creates a {@linkplain LiteralArgumentBuilder} with the given name.
     *
     * @param command The name.
     * @return The literal argument builder.
     */
    default LiteralArgumentBuilder<FabricClientCommandSource> literal(String command) {
        return LiteralArgumentBuilder.literal(command);
    }

    /**
     * Creates a {@linkplain RequiredArgumentBuilder} with the given name and {@linkplain ArgumentType}
     *
     * @param name The name.
     * @param type The argument type.
     * @param <S>  The type of the argument type.
     * @return The required argument builder.
     */
    default <S> RequiredArgumentBuilder<FabricClientCommandSource, S> argument(
        String name,
        ArgumentType<S> type
    ) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    /**
     * Provides a {@linkplain Command} instance that executes the {@linkplain Consumer} upon execution.
     *
     * @param consumer The consumer.
     * @return The command.
     */
    default Command<FabricClientCommandSource> run(Consumer<CommandContext<FabricClientCommandSource>> consumer) {
        return context -> {
            consumer.accept(context);
            return 1;
        };
    }
    /**
     * Runs a piece of code without providing the context.
     *
     * @param runnable The consumer.
     * @return The command.
     */
    default Command<FabricClientCommandSource> run(Runnable runnable) {
        return context -> {
            runnable.run();
            return 1;
        };
    }

    /**
     * Modifies the provided value and returns it.
     * @param value The value.
     * @param config The configurator.
     * @return The configured value.
     * @param <T> The type of the value.
     */
    default <T> T create(T value, UnaryOperator<T> config) {
        return config.apply(value);
    }

    /**
     * Provides a {@linkplain Command} instance that executes the {@linkplain Consumer} upon execution, and catches every exception that might occur.
     *
     * @param consumer The consumer.
     * @return The command.
     */
    default Command<FabricClientCommandSource> runAndWrap(
        ThrowingConsumer<CommandContext<FabricClientCommandSource>> consumer) {
        return context -> {
            try {
                consumer.accept(context);
            } catch (Throwable e) {
                LOGGER.error("Command execution exception", e);
                context.getSource().sendError(
                    Text.translatable(TranslationKeys.UNEXPECTED_ERROR)
                );
                return 1;
            }
            return 1;
        };
    }

    /**
     * Sends a message without any formatting.
     *
     * @param message The message to send.
     */
    default void sendRawMessage(String message) {
        CookiesUtils.sendRawMessage(message);
    }

    /**
     * Sends a message with the mod prefix.
     *
     * @param message The message to send.
     */
    default void sendInformation(String message) {
        CookiesUtils.sendInformation(message);
    }

    /**
     * Sends a message with the mod prefix and in white color.
     *
     * @param message The message to send.
     */
    default void sendMessage(String message) {
        CookiesUtils.sendWhiteMessage(message);
    }

    /**
     * Sends a message with the mod prefix that indicates that something failed.
     *
     * @param message The message to send.
     */
    default void sendFailedMessage(String message) {
        CookiesUtils.sendFailedMessage(message);
    }

    /**
     * Sends a message with the mod prefix that indicates that something succeeded.
     *
     * @param message The message to send.
     */
    default void sendSuccessMessage(String message) {
        CookiesUtils.sendSuccessMessage(message);
    }

    /**
     * Interface to safely execute methods that might throw an exception.
     *
     * @param <T> The type of the parameter.
     */
    interface ThrowingConsumer<T> {
        /**
         * Executes the consumer with the parameter.
         *
         * @param t The parameter.
         * @throws Throwable A potential exception.
         */
        void accept(T t) throws Throwable;
    }

}
