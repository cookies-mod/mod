package codes.cookies.mod.utils;

import codes.cookies.mod.events.ChatListener;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;

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
	@Getter
	@Setter
	private static long lastPing = -1;


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
		return DevUtils.isEnabled(SKIP_SKYBLOCK_CHECK) || LocationUtils.isInSkyblock();
	}
}
