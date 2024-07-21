package me.nashplugz.aquaf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {
    private AquaFest plugin;
    private Scoreboard scoreboard;
    private Objective objective;

    public ScoreboardManager(AquaFest plugin) {
        this.plugin = plugin;
        setupScoreboard();
    }

    private void setupScoreboard() {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            scoreboard = manager.getNewScoreboard();
            objective = scoreboard.registerNewObjective("eventScores", "dummy", ChatColor.GOLD + "Top Fishers");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            plugin.getLogger().warning("Failed to get Scoreboard Manager!");
        }
    }

    public void updateScoreboard() {
        if (scoreboard == null || objective == null) {
            plugin.getLogger().warning("Scoreboard or Objective is null!");
            return;
        }

        Map<UUID, Integer> playerScores = plugin.getLeaderboardManager().getTopScores(5);
        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(playerScores.entrySet());
        sortedEntries.sort(Map.Entry.<UUID, Integer>comparingByValue().reversed());

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : sortedEntries) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                Score score = objective.getScore(ChatColor.YELLOW + "#" + rank + " " + player.getName());
                score.setScore(entry.getValue());
                rank++;
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }
}
