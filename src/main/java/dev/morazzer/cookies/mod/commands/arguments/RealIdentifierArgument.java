package dev.morazzer.cookies.mod.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.CookiesUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A "real identifier" argument is nothing else then just an identifier from a predefined
 * {@linkplain Collection} instance. Meaning you can always adjust the values of that
 * {@linkplain Collection} instance while the argument exists. Note that you might want to use a thread-save
 * version of the {@linkplain Collection} type to avoid
 * {@linkplain java.util.ConcurrentModificationException}s.
 */
public class RealIdentifierArgument implements ArgumentType<Identifier> {

    private static final SimpleCommandExceptionType COMMAND_EXCEPTION =
        new SimpleCommandExceptionType(Text.translatable(
            "argument.id.invalid"));
    private static final DynamicCommandExceptionType IDENTIFIER_NOT_FOUND =
        new DynamicCommandExceptionType(id -> CookiesUtils
            .createPrefix(Constants.FAIL_COLOR)
            .append("Identifier %s not found".formatted(id)));
    private final Collection<Identifier> identifierCollection;
    private final String namespace;
    private final String pathPrefix;

    /**
     * Constructor to create a {@linkplain dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument} with no
     * namespace and no path prefix. The namespace defaults to "minecraft".
     *
     * @param identifierCollection The {@linkplain Collection} instance which describes the allowed values.
     */
    public RealIdentifierArgument(@NotNull Collection<Identifier> identifierCollection) {
        this(identifierCollection, "minecraft");
    }

    /**
     * Constructor to create a {@linkplain dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument}.
     *
     * @param identifierCollection The {@linkplain Collection} instance which describes the allowed values.
     * @param namespace            The namespace all values default to, this still allows the use of other namespaces.
     */
    public RealIdentifierArgument(
        @NotNull Collection<Identifier> identifierCollection,
        @NotNull @NotBlank String namespace
    ) {
        this(identifierCollection, namespace, "");
    }

    /**
     * Constructor to create a {@linkplain dev.morazzer.cookies.mod.commands.arguments.RealIdentifierArgument}.
     *
     * @param identifierCollection The {@linkplain Collection} instance which describes the allowed values.
     * @param namespace            The namespace all values default to, this still allows the use of other namespaces.
     * @param pathPrefix           The prefix that will be prepended to the path if it's not matching a valid
     *                             identifier. This prefix should have a trailing '/'.
     */
    public RealIdentifierArgument(
        @NotNull Collection<Identifier> identifierCollection, @NotNull @NotBlank String namespace,
        @NotNull @NotBlank String pathPrefix
    ) {
        this.identifierCollection = identifierCollection;
        this.namespace = namespace;
        this.pathPrefix = pathPrefix;
    }

    /**
     * Parser to get a valid identifier from a string reader.
     *
     * @param reader The reader to read the identifier from.
     * @return The identifier if it's a valid identifier from the provided {@linkplain Collection}.
     * @throws CommandSyntaxException If the provided identifier is invalid or not in the
     *                                {@linkplain Collection}.
     */
    @Override
    public Identifier parse(@NotNull StringReader reader) throws CommandSyntaxException {
        Identifier identifier = fromCommandInput(reader);
        if (!identifierCollection.contains(identifier)) {
            throw IDENTIFIER_NOT_FOUND.create(identifier);
        }

        return identifier;
    }

    /**
     * Creates an identifier from a string reader.
     *
     * @param reader The reader to read the identifier from.
     * @return The Identifier.
     * @throws CommandSyntaxException If the reader doesn't match the
     *                                {@linkplain Identifier#isNamespaceValid(String)} and the
     *                                {@linkplain Identifier#isPathValid(String)} methods
     */
    public Identifier fromCommandInput(@NotNull StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && Identifier.isCharValid(reader.peek())) {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        try {
            String[] split = split(string);
            return Identifier.of(split[0], split[1]);
        } catch (InvalidIdentifierException invalidIdentifierException) {
            reader.setCursor(i);
            throw COMMAND_EXCEPTION.createWithContext(reader);
        }
    }

    /**
     * Used to create a string array with the default namespace and the prefixed path.
     *
     * @param id The identifier in string form.
     * @return A string array representing with namespace at index zero and the path at index one.
     */
    @NotNull
    @Contract(pure = true)
    protected String[] split(@NotNull String id) {
        String[] strings = new String[] {this.namespace, pathPrefix + id};
        int i = id.indexOf(':');
        if (i >= 0) {
            strings[1] = id.substring(i + 1);
            if (i >= 1) {
                strings[0] = id.substring(0, i);
            }
        }
        return strings;
    }

    /**
     * Suggestions for the identifiers.
     *
     * @param context The {@link CommandContext} provided by the
     *                {@link com.mojang.brigadier.builder.ArgumentBuilder#executes(com.mojang.brigadier.Command)}
     *                method.
     * @param builder A {@link SuggestionsBuilder} which is also provided by the
     *                {@linkplain com.mojang.brigadier.builder.ArgumentBuilder#executes(com.mojang.brigadier.Command)}
     *                method.
     * @param <S>     The {@link net.minecraft.command.CommandSource} type.
     * @return A future that will resolve to the suggestions.
     */
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
        @NotNull CommandContext<S> context,
        @NotNull SuggestionsBuilder builder
    ) {
        if (builder.getRemaining().isEmpty()) {
            identifierCollection.forEach(identifier -> builder.suggest(identifier.toString()));
        } else {
            identifierCollection.stream().filter(
                    ((Predicate<Identifier>) i -> i.toString().startsWith(builder.getRemaining()))
                        .or(i -> i.getPath().startsWith(builder.getRemaining()))
                        .or(i -> i.getPath().startsWith(pathPrefix + builder.getRemaining())))
                .map(Identifier::toString)
                .forEach(builder::suggest);
        }

        return builder.buildFuture();
    }

    /**
     * Examples of valid arguments for the identifier.
     *
     * @return A list of valid arguments.
     */
    @Override
    @Contract(pure = true)
    @NotNull
    public Collection<String> getExamples() {
        return Arrays.asList(
            "%s:%sdebug_logging".formatted(this.namespace, this.pathPrefix),
            "%s:%s".formatted(this.namespace, this.pathPrefix.isBlank() ? "test" : this.pathPrefix)
        );
    }

}
