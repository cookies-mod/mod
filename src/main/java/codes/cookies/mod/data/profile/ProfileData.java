package codes.cookies.mod.data.profile;

import codes.cookies.mod.data.farming.squeakymousemat.SqueakyMousematData;
import codes.cookies.mod.data.mining.PowderData;
import codes.cookies.mod.data.mining.crystal.CrystalStatusData;
import codes.cookies.mod.data.player.PlayerStorage;
import codes.cookies.mod.data.profile.profile.GlobalProfileStorage;
import codes.cookies.mod.data.profile.profile.GlobalProfileData;
import codes.cookies.mod.data.profile.sub.AccessoryItemData;
import codes.cookies.mod.data.profile.sub.CraftHelperData;
import codes.cookies.mod.data.profile.sub.EquipmentData;
import codes.cookies.mod.data.profile.sub.ForgeTracker;
import codes.cookies.mod.data.profile.sub.HotmData;
import codes.cookies.mod.data.profile.sub.MiscItemData;
import codes.cookies.mod.data.profile.sub.PlotData;
import codes.cookies.mod.data.profile.sub.ProfileStats;
import codes.cookies.mod.data.profile.sub.RancherSpeeds;
import codes.cookies.mod.data.profile.sub.SackTracker;
import codes.cookies.mod.data.profile.sub.StorageData;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.json.Exclude;
import codes.cookies.mod.utils.json.Safe;

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
	private CraftHelperData craftHelperData = new CraftHelperData();
	@Setter(AccessLevel.PRIVATE)
    @Safe
    private SackTracker sackTracker = new SackTracker();
	private RancherSpeeds rancherSpeeds = new RancherSpeeds(this);
	private StorageData storageData = new StorageData();
	@Exclude
    private GlobalProfileData globalProfileData;
	private ForgeTracker forgeTracker = new ForgeTracker();
	private HotmData hotmData = new HotmData();
	private MiscItemData miscTracker = new MiscItemData();
	private AccessoryItemData accessoryTracker = new AccessoryItemData();
	private ProfileStats profileStats = new ProfileStats();
	private EquipmentData equipmentData = new EquipmentData();
	private PlotData plotData = new PlotData();
	private SqueakyMousematData squeakyMousematData = SqueakyMousematData.getDefault();
	private PowderData powderData = new PowderData();
	private CrystalStatusData crystalStatus = new CrystalStatusData();

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
