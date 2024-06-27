package dev.morazzer.cookies.mod.utils;

import dev.morazzer.cookies.mod.events.ChatListener;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import java.util.Optional;
import java.util.UUID;

/**
 * Utils related to skyblock.
 */
public class SkyblockUtils {

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
        return LocationUtils.getRegion() != null;
    }
}
