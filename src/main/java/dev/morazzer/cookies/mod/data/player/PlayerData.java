package dev.morazzer.cookies.mod.data.player;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Player specific data.
 */
@Getter
@Setter
public class PlayerData {

    private UUID playerUuid;

    @SuppressWarnings("MissingJavadoc")
    public PlayerData(final UUID player) {
        this.playerUuid = player;
    }

    @Override
    public String toString() {
        return "PlayerData{"
               + "playerUUID=" + this.playerUuid
               + '}';
    }

}
