package dev.morazzer.cookies.mod.data.profile;

import dev.morazzer.cookies.mod.data.player.PlayerStorage;
import dev.morazzer.cookies.mod.data.profile.sub.RancherSpeeds;
import dev.morazzer.cookies.mod.data.profile.sub.SackTracker;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.json.Safe;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The data associated with a profile of a player. Note that the same profile on different accounts has different data.
 */
@Getter
@Setter
@AllArgsConstructor
public class ProfileData {

    @Setter(AccessLevel.PRIVATE)
    private UUID playerUuid;
    @Setter(AccessLevel.PRIVATE)
    private UUID profileUuid;
    @Setter(AccessLevel.PRIVATE)
    private GameMode gameMode = GameMode.UNKNOWN;
    @Setter(AccessLevel.PRIVATE)
    @Safe
    private SackTracker sackTracker = new SackTracker();
    private String selectedCraftHelperItem = "";
    private RancherSpeeds rancherSpeeds = new RancherSpeeds(this);

    /**
     * Create a profile.
     *
     * @param playerUuid  The uuid of the owner.
     * @param profileUuid The uuid of the profile.
     */
    public ProfileData(final UUID playerUuid, final UUID profileUuid) {
        this.playerUuid = playerUuid;
        this.profileUuid = profileUuid;
    }

    /**
     * Get the game mode of the profile.
     *
     * @return The game mode.
     */
    public GameMode getGameMode() {
        if (this.gameMode == GameMode.UNKNOWN && SkyblockUtils.isCurrentlyInSkyblock()) {
            ProfileStorage.saveCurrentProfile();
        }
        return this.gameMode;
    }

    /**
     * If the profile is currently active or not.
     *
     * @return If the profile is active.
     */
    public boolean isActive() {
        return PlayerStorage.getCurrentPlayer().map(uuid -> uuid.equals(this.playerUuid)).orElse(false)
               && SkyblockUtils.getLastProfileId().map(uuid -> uuid.equals(this.profileUuid)).orElse(false);
    }

    /**
     * The profile in string representation.
     *
     * @return The profile.
     */
    @Override
    public String toString() {
        return "ProfileData{"
               + "playerUuid=" + this.playerUuid
               + ", profileUuid=" + this.profileUuid
               + ", gameMode=" + this.gameMode
               + '}';
    }

}