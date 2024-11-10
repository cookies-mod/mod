package codes.cookies.mod.data.profile;

import java.util.Optional;
import net.minecraft.util.StringIdentifiable;

/**
 * The game mode of a skyblock profile.
 */
@SuppressWarnings({"MissingJavadoc", "unused"})
public enum GameMode implements StringIdentifiable {

    CLASSIC,
    IRONMAN("♲ Ironman"),
    STRANDED("☀ Stranded"),
    BINGO("Ⓑ Bingo"),
    /**
     * Used to indicate that the profile has not evaluated its game mode yet.
     */
    UNSET,
    /**
     * Used to indicate a not currently know game mode, this might be caused by wrong detection or addition of a new one.
     */
    UNKNOWN("[^A-Za-z0-9⏣] .*");

    private final String symbol;

    GameMode() {
        this(null);
    }

    GameMode(final String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the game mode based on the symbol.
     *
     * @param gameMode The symbol.
     * @return The game mode.
     */
    public static GameMode getByString(final String gameMode) {
        if (gameMode.matches(IRONMAN.symbol)) {
            return IRONMAN;
        } else if (gameMode.matches(STRANDED.symbol)) {
            return STRANDED;
        } else if (gameMode.matches(BINGO.symbol)) {
            return BINGO;
        }
        return UNKNOWN;
    }

    /**
     * Get the symbol of the game mode.
     *
     * @return The symbol.
     */
    @SuppressWarnings("unused")
    public Optional<String> getSymbol() {
        return Optional.ofNullable(this.symbol);
    }

    @Override
    public String asString() {
        return this.name();
    }
}
