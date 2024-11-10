package codes.cookies.mod.data.profile.profile;

import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.json.Safe;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Profile data that is player independent.
 */
@Getter
@RequiredArgsConstructor
public class GlobalProfileData {

    private final UUID uuid;
    @Safe
    private final IslandChestStorage islandStorage = new IslandChestStorage();

    /**
     * @return Whether this profile is still active or not.
     */
    public boolean isActive() {
        return SkyblockUtils.getLastProfileId().map(uuid::equals).orElse(false);
    }
}
