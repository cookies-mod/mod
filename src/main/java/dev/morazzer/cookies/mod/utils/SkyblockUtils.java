package dev.morazzer.cookies.mod.utils;

import dev.morazzer.cookies.mod.events.ChatListener;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import dev.morazzer.cookies.mod.utils.minecraft.ScoreboardUtils;
import dev.morazzer.mods.cookies.generated.Regions;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Identifier;

/**
 * Utils related to skyblock.
 */
public class SkyblockUtils {
    private static final Identifier SKIP_SKYBLOCK_CHECK = DevUtils.createIdentifier("skyblock/skip_check");
    private static long lastSkyblock = -1;
    private static long lastServerSwap = -1;
    @Getter
    @Setter
    private static long lastPing = -1;

    /**
     * Sets lastServerSwap to the current time.
     */
    public static void swapServer() {
        lastServerSwap = System.currentTimeMillis();
    }

    /**
     * Gets the last recorded profile id.
     *
     * @return The last profile id.
     */
    public static Optional<UUID> getLastProfileId() {
        return Optional.ofNullable(ChatListener.lastProfileId);
    }

    /**
     * Whether the player is currently in skyblock or not.
     *
     * @return Whether the player is in skyblock.
     */
    public static boolean isCurrentlyInSkyblock() {
        if (lastSkyblock + 3000 > System.currentTimeMillis() && lastServerSwap + 3000 < System.currentTimeMillis()) {
            return (lastSkyblock & 1) == 1;
        }
        if (DevUtils.isEnabled(SKIP_SKYBLOCK_CHECK)) {
            return true;
        }

        final boolean isInSkyblock = LocationUtils.getRegion() != Regions.UNKNOWN ||
                                     (ScoreboardUtils.getObjective() != null && (ScoreboardUtils.getObjective()
                                                                                     .getDisplayName()
                                                                                     .getString()
                                                                                     .toLowerCase(Locale.ROOT)
                                                                                     .startsWith("skyblock") ||
                                                                                 ScoreboardUtils.getObjective()
                                                                                     .getDisplayName()
                                                                                     .getString()
                                                                                     .toLowerCase(Locale.ROOT)
                                                                                     .startsWith("skiblock")));

        lastSkyblock = System.currentTimeMillis() >> 1;
        lastSkyblock = lastSkyblock << 1;
        lastSkyblock |= isInSkyblock ? 1 : 0;

        return isInSkyblock;
    }
}
