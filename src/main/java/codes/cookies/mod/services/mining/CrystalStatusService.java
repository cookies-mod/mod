package codes.cookies.mod.services.mining;

import java.util.Optional;

import codes.cookies.mod.data.mining.crystal.CrystalStatus;
import codes.cookies.mod.data.mining.crystal.CrystalType;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.events.PlayerListWidgetEvent;
import codes.cookies.mod.utils.skyblock.playerlist.widgets.PlayerListWidgets;
import codes.cookies.mod.utils.skyblock.playerlist.widgets.crystal.CrystalWidget;

/**
 * Keeps track about current crystal status and makes accessing them easier.
 */
public class CrystalStatusService {

	public static void register() {
		PlayerListWidgetEvent.register(PlayerListWidgets.CRYSTAL, CrystalStatusService::onWidgetUpdate);
	}

	private static void onWidgetUpdate(CrystalWidget crystalWidget) {
		for (CrystalType nucleusCrystal : CrystalType.values()) {
			crystalWidget.getCrystalStatusByType(nucleusCrystal)
					.ifPresent(status -> updateCrystalStatus(nucleusCrystal, status));
		}
	}

	public static void updateCrystalStatus(CrystalType crystalType, CrystalStatus crystalStatus) {
		ProfileStorage.getCurrentProfile()
				.ifPresent(profileData -> profileData.getCrystalStatus().setStatus(crystalType, crystalStatus));
	}

	public static Optional<CrystalStatus> getCrystalStatus(CrystalType crystalType) {
		return ProfileStorage.getCurrentProfile()
				.map(ProfileData::getCrystalStatus)
				.map(status -> status.getStatus(crystalType));
	}

}
