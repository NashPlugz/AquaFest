package me.nashplugz.aquaf;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeaderboardManager {
    private final AquaFest plugin;
    private final Map<String, Map<UUID, Double>> leaderboards;

    public LeaderboardManager(AquaFest plugin) {
        this.plugin = plugin;
        this.leaderboards = new HashMap<>();
    }

    public void addScore(Player player, double score, String worldName) {
        Map<UUID, Double> leaderboard = leaderboards.computeIfAbsent(worldName, k -> new HashMap<>());
        leaderboard.merge(player.getUniqueId(), score, Double::sum);
    }

    public Map<UUID, Double> getLeaderboard(String worldName) {
        return leaderboards.getOrDefault(worldName, new HashMap<>());
    }

    public String getFormattedLeaderboard(String worldName) {
        Map<UUID, Double> leaderboard = getLeaderboard(worldName);
        StringBuilder sb = new StringBuilder("Top Fishers:\n");
        leaderboard.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .forEach(entry -> {
                    String playerName = plugin.getServer().getOfflinePlayer(entry.getKey()).getName();
                    sb.append(playerName).append(": $").append(String.format("%.2f", entry.getValue())).append("\n");
                });
        return sb.toString();
    }

    public void clearLeaderboard(String worldName) {
        leaderboards.remove(worldName);
    }
}
