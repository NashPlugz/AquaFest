package me.nashplugz.aquaf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {
    private AquaFest plugin;
    private Map<String, Scoreboard> worldScoreboards;
    private Scoreboard globalScoreboard;

    public ScoreboardManager(AquaFest plugin) {
        this.plugin = plugin;
        this.worldScoreboards = new HashMap<>();
        setupGlobalScoreboard();
    }

    private void setupGlobalScoreboard() {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            globalScoreboard = manager.getNewScoreboard();
            Objective objective = globalScoreboard.registerNewObjective("globalScores", "dummy", ChatColor.GOLD + "Global Top Fishers");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            plugin.getLogger().warning("Failed to get Scoreboard Manager!");
        }
    }

    public void updateScoreboard(String worldName) {
        Scoreboard scoreboard = worldScoreboards.computeIfAbsent(worldName, k -> Bukkit.getScoreboardManager().getNewScoreboard());
        Objective objective = scoreboard.getObjective("eventScores");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("eventScores", "dummy", ChatColor.GOLD + "Top Fishers");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        Map<UUID, Integer> playerScores = plugin.getLeaderboardManager().getTopScores(worldName, 5);
        updateScores(scoreboard, objective, playerScores);

        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            for (Player player : world.getPlayers()) {
                player.setScoreboard(scoreboard);
            }
        }
    }

    public void updateGlobalScoreboard() {
        Objective objective = globalScoreboard.getObjective("globalScores");
        if (objective == null) {
            objective = globalScoreboard.registerNewObjective("globalScores", "dummy", ChatColor.GOLD + "Global Top Fishers");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        Map<UUID, Integer> playerScores = plugin.getLeaderboardManager().getTopScores(5);
        updateScores(globalScoreboard, objective, playerScores);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(globalScoreboard);
        }
    }

    private void updateScores(Scoreboard scoreboard, Objective objective, Map<UUID, Integer> playerScores) {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(playerScores.entrySet());
        sortedEntries.sort(Map.Entry.<UUID, Integer>comparingByValue().reversed());

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : sortedEntries) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                Score score = objective.getScore(ChatColor.YELLOW + "#" + rank + " " + player.getName());
                score.setScore(entry.getValue());
                rank++;
            }
        }
    }

    public void removePlayerScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
