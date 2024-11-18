package codes.cookies.mod.utils.skyblock.playerlist.widgets.corpse;

/**
 * Player list corpse entry.
 * @param corpseType The type of the corpse.
 * @param found Whether it was found by the player.
 */
public record CorpseEntry(CorpseType corpseType, boolean found) {
}
