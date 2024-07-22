package me.nashplugz.aquaf;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderboardManager {
    private AquaFest plugin;
    private Map<String, Map<UUID, Integer>> eventScores;

    public LeaderboardManager(AquaFest plugin) {
        this.plugin = plugin;
        this.eventScores = new ConcurrentHashMap<>();
    }

    public void addScore(Player player, int score, String eventName) {
        UUID playerId = player.getUniqueId();
        eventScores.computeIfAbsent(eventName, k -> new ConcurrentHashMap<>())
                .merge(playerId, score, Integer::sum);
    }

    public Map<UUID, Integer> getTopScores(int limit) {
        Map<UUID, Integer> totalScores = new HashMap<>();

        for (Map<UUID, Integer> scores : eventScores.values()) {
            for (Map.Entry<UUID, Integer> entry : scores.entrySet()) {
                totalScores.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return sortByValueAndLimit(totalScores, limit);
    }

    public Map<UUID, Integer> getTopScores(String eventName, int limit) {
        Map<UUID, Integer> eventScores = this.eventScores.getOrDefault(eventName, new HashMap<>());
        return sortByValueAndLimit(eventScores, limit);
    }

    private Map<UUID, Integer> sortByValueAndLimit(Map<UUID, Integer> map, int limit) {
        List<Map.Entry<UUID, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<UUID, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            Map.Entry<UUID, Integer> entry = list.get(i);
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public void clearScores(String eventName) {
        eventScores.remove(eventName);
    }

    public void clearAllScores() {
        eventScores.clear();
    }
}
