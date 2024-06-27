package dev.morazzer.cookies.mod.utils.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

/**
 * Helper methods related to the scoreboard.
 */
public class ScoreboardUtils {

    /**
     * Gets the objective from the scoreboard.
     *
     * @return The objective.
     */
    public static ScoreboardObjective getObjective() {
        return getObjective(getScoreboard());
    }

    /**
     * Gets the objective from the scoreboard.
     *
     * @param scoreboard The scoreboard.
     * @return The objective.
     */
    public static ScoreboardObjective getObjective(Scoreboard scoreboard) {
        if (scoreboard == null) {
            return null;
        }
        return scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
    }

    /**
     * Gets the default scoreboard.
     *
     * @return The scoreboard.
     */
    public static Scoreboard getScoreboard() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        final ClientPlayerEntity player = minecraftClient.player;
        if (player == null) {
            return null;
        }
        return player.getScoreboard();
    }

    /**
     * Get the lines from the scoreboard objective.
     *
     * @return The lines.
     */
    public static List<String> getLines() {
        return getLines(getObjective(getScoreboard()));
    }

    /**
     * Get the lines from the scoreboard objective.
     *
     * @param scoreboardObjective The objective.
     * @return The lines.
     */
    public static List<String> getLines(ScoreboardObjective scoreboardObjective) {
        if (scoreboardObjective == null) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();

        final Scoreboard scoreboard = scoreboardObjective.getScoreboard();
        for (ScoreHolder knownScoreHolder : scoreboard.getKnownScoreHolders()) {
            if (!scoreboard.getScoreHolderObjectives(knownScoreHolder).containsKey(scoreboardObjective)) {
                continue;
            }
            Team team = scoreboard.getScoreHolderTeam(knownScoreHolder.getNameForScoreboard());

            if (team == null) {
                continue;
            }

            String line = (team.getPrefix().getString() + team.getSuffix().getString()).trim();

            if (line.isEmpty()) {
                continue;
            }

            lines.add(Formatting.strip(line));
        }

        return lines;
    }

}
