package dev.morazzer.cookies.mod.utils.minecraft;

import dev.morazzer.mods.cookies.generated.Regions;
import java.util.List;

/**
 * Utils to get the current location of the player.
 */
public class LocationUtils {

    private static Regions region;
    private static long lastUpdated = -1;

    /**
     * Gets the current region the player is in.
     *
     * @return The region.
     */
    public static Regions getRegion() {
        if (lastUpdated + 1000 > System.currentTimeMillis()) {
            return region;
        }

        final List<String> lines = ScoreboardUtils.getLines().reversed();
        String location = null;
        String icon = "";
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("⏣ ") || line.startsWith("ф ")) {
                icon = line.trim().substring(0, 1).trim();
                location = line.trim().substring(1).trim();
                break;
            }
        }

        if (location == null) {
            lastUpdated = System.currentTimeMillis();
            LocationUtils.region = Regions.UNKNOWN;
            return region;
        }

        for (Regions value : Regions.values()) {
            if (value == Regions.WIZARD_TOWER_HUB) {
                System.out.println("tower");
            }
            if (value.icon != null && !icon.equals(value.icon)) {
                continue;
            }
            if ((value.regex && location.matches(value.scoreboard)) || location.equals(value.scoreboard)) {
                lastUpdated = System.currentTimeMillis();
                LocationUtils.region = value;
                return region;
            }
        }

        lastUpdated = System.currentTimeMillis();
        LocationUtils.region = Regions.UNKNOWN;
        return region;
    }

    @SuppressWarnings("MissingJavadoc")
    public enum Island {
        PRIVATE_ISLAND,
        GARDEN,
        HUB,
        THE_BARN,
        MUSHROOM_DESERT,
        THE_PARK,
        SPIDERS_DEN,
        THE_END,
        CRIMSON_ISLE,
        GOLD_MINE,
        DEEP_CAVERNS,
        DWARVEN_MINES,
        CRYSTAL_HOLLOWS,
        WINTER_ISLAND,
        DUNGEON_HUB,
        RIFT,
        CATACOMBS,
        UNKNOWN
    }

}
