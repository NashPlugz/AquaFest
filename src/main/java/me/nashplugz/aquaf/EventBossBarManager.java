package me.nashplugz.aquaf;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class EventBossBarManager {
    private AquaFest plugin;
    private Map<String, BossBar> worldBossBars;
    private BossBar worldwideBossBar;

    public EventBossBarManager(AquaFest plugin) {
        this.plugin = plugin;
        this.worldBossBars = new HashMap<>();
    }

    public void createWorldBossBar(String worldName, String eventName, long duration) {
        BossBar bossBar = Bukkit.createBossBar(
                "Event: " + eventName,
                BarColor.BLUE,
                BarStyle.SOLID
        );
        worldBossBars.put(worldName, bossBar);
        updateBossBar(worldName, duration);

        // Show BossBar only to players in the specific world
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            for (Player player : world.getPlayers()) {
                bossBar.addPlayer(player);
            }
        }
    }

    public void createWorldwideBossBar(String eventName, long duration) {
        worldwideBossBar = Bukkit.createBossBar(
                "Worldwide Event: " + eventName,
                BarColor.PURPLE,
                BarStyle.SOLID
        );
        updateWorldwideBossBar(duration);

        // Show BossBar to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            worldwideBossBar.addPlayer(player);
        }
    }

    public void updateBossBar(String worldName, long remainingTime) {
        BossBar bossBar = worldBossBars.get(worldName);
        if (bossBar != null) {
            double progress = Math.max(0, Math.min(1, remainingTime / (double) plugin.getConfigManager().getWorldEvent(worldName).getDuration()));
            bossBar.setProgress(progress);
            bossBar.setTitle("Event: " + plugin.getEventManager().getEventName(worldName) + " - Time left: " + formatTime(remainingTime));
        }
    }

    public void updateWorldwideBossBar(long remainingTime) {
        if (worldwideBossBar != null) {
            double progress = Math.max(0, Math.min(1, remainingTime / (double) plugin.getConfigManager().getWorldwideEvent().getDuration()));
            worldwideBossBar.setProgress(progress);
            worldwideBossBar.setTitle("Worldwide Event: " + plugin.getEventManager().getWorldwideEventName() + " - Time left: " + formatTime(remainingTime));
        }
    }

    public void updatePlayerBossBar(Player player) {
        String worldName = player.getWorld().getName();
        BossBar worldBossBar = worldBossBars.get(worldName);

        // Remove player from all boss bars
        for (BossBar bossBar : worldBossBars.values()) {
            bossBar.removePlayer(player);
        }
        if (worldwideBossBar != null) {
            worldwideBossBar.removePlayer(player);
        }

        // Add player to the correct boss bar
        if (worldBossBar != null) {
            worldBossBar.addPlayer(player);
        } else if (worldwideBossBar != null) {
            worldwideBossBar.addPlayer(player);
        }
    }

    public void removeWorldBossBar(String worldName) {
        BossBar bossBar = worldBossBars.remove(worldName);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void removeWorldwideBossBar() {
        if (worldwideBossBar != null) {
            worldwideBossBar.removeAll();
            worldwideBossBar = null;
        }
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
