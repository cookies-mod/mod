package dev.morazzer.cookies.mod.data.profile;

import dev.morazzer.cookies.mod.data.player.PlayerStorage;
import dev.morazzer.cookies.mod.data.profile.profile.GlobalProfileStorage;
import dev.morazzer.cookies.mod.data.profile.profile.GlobalProfileData;
import dev.morazzer.cookies.mod.data.profile.sub.AccessoryItemData;
import dev.morazzer.cookies.mod.data.profile.sub.ForgeTracker;
import dev.morazzer.cookies.mod.data.profile.sub.HotmData;
import dev.morazzer.cookies.mod.data.profile.sub.MiscItemData;
import dev.morazzer.cookies.mod.data.profile.sub.RancherSpeeds;
import dev.morazzer.cookies.mod.data.profile.sub.SackTracker;
import dev.morazzer.cookies.mod.data.profile.sub.StorageData;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.json.Exclude;
import dev.morazzer.cookies.mod.utils.json.Safe;

import java.util.Optional;
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
    private StorageData storageData = new StorageData();
    @Exclude
    private GlobalProfileData globalProfileData;
	private ForgeTracker forgeTracker = new ForgeTracker();
	private HotmData hotmData = new HotmData();
	private MiscItemData miscTracker = new MiscItemData();
	private AccessoryItemData accessoryTracker = new AccessoryItemData();

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
        return PlayerStorage.getCurrentPlayer().map(uuid -> uuid.equals(this.playerUuid)).orElse(false) &&
               SkyblockUtils.getLastProfileId().map(uuid -> uuid.equals(this.profileUuid)).orElse(false);
    }

    /**
     * Called right after all serializable fields have been set.
     */
    public void load() {
        this.globalProfileData = GlobalProfileStorage.load(this.profileUuid);
    }

    /**
     * Called right after all serializable fields have been saved.
     */
    public void save() {
        GlobalProfileStorage.save(this.globalProfileData);
    }

	public Optional<GlobalProfileData> getGlobalProfileData() {
		return Optional.ofNullable(this.globalProfileData);
	}

    /**
     * The profile in string representation.
     *
     * @return The profile.
     */
    @Override
    public String toString() {
        return "ProfileData{" + "playerUuid=" + this.playerUuid + ", profileUuid=" + this.profileUuid + ", gameMode=" +
               this.gameMode + '}';
    }

}
