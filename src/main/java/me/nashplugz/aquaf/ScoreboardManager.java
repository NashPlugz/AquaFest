package me.nashplugz.aquaf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    private final AquaFest plugin;

    public ScoreboardManager(AquaFest plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(String worldName) {
        Map<UUID, Double> leaderboard = plugin.getLeaderboardManager().getLeaderboard(worldName);

        for (Player player : Bukkit.getWorld(worldName).getPlayers()) {
            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = board.registerNewObjective("fishing", "dummy", ChatColor.GOLD + "Fishing Leaderboard");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            int score = 15;
            for (Map.Entry<UUID, Double> entry : leaderboard.entrySet()) {
                if (score <= 0) break;
                String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                if (playerName != null) {
                    String displayName = ChatColor.GREEN + playerName + ": " + ChatColor.YELLOW + "$" + String.format("%.2f", entry.getValue());
                    objective.getScore(displayName).setScore(score);
                    score--;
                }
            }

            player.setScoreboard(board);
        }
    }

    public void updateGlobalScoreboard() {
        Map<UUID, Double> leaderboard = plugin.getLeaderboardManager().getLeaderboard("worldwide");

        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = board.registerNewObjective("fishing", "dummy", ChatColor.GOLD + "Global Fishing Leaderboard");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            int score = 15;
            for (Map.Entry<UUID, Double> entry : leaderboard.entrySet()) {
                if (score <= 0) break;
                String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                if (playerName != null) {
                    String displayName = ChatColor.GREEN + playerName + ": " + ChatColor.YELLOW + "$" + String.format("%.2f", entry.getValue());
                    objective.getScore(displayName).setScore(score);
                    score--;
                }
            }

            player.setScoreboard(board);
        }
    }

    public void removePlayerScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
