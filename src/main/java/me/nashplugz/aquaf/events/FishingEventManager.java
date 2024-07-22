package me.nashplugz.aquaf.events;

import me.nashplugz.aquaf.AquaFest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class FishingEventManager {
    private AquaFest plugin;
    private Map<String, String> activeWorldEvents;
    private String activeWorldwideEvent;
    private Map<String, Long> eventEndTimes;
    private Map<String, BukkitTask> eventTasks;

    public FishingEventManager(AquaFest plugin) {
        this.plugin = plugin;
        this.activeWorldEvents = new HashMap<>();
        this.eventEndTimes = new HashMap<>();
        this.eventTasks = new HashMap<>();
    }

    public boolean startEvent(String eventName, World world) {
        if (isEventActive(world)) {
            return false;
        }
        String worldName = world.getName();
        activeWorldEvents.put(worldName, eventName);
        long duration = plugin.getConfigManager().getWorldEvent(worldName).getDuration();
        long endTime = System.currentTimeMillis() + (duration * 1000);
        eventEndTimes.put(worldName, endTime);

        plugin.getEventBossBarManager().createWorldBossBar(worldName, eventName, duration);
        plugin.getScoreboardManager().updateScoreboard(worldName);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long remainingTime = (eventEndTimes.get(worldName) - System.currentTimeMillis()) / 1000;
            if (remainingTime <= 0) {
                stopEvent(eventName, world);
            } else {
                plugin.getEventBossBarManager().updateBossBar(worldName, remainingTime);
            }
        }, 20L, 20L); // Update every second

        eventTasks.put(worldName, task);
        return true;
    }

    public boolean stopEvent(String eventName, World world) {
        String worldName = world.getName();
        if (!isEventActive(world) || !activeWorldEvents.get(worldName).equals(eventName)) {
            return false;
        }
        activeWorldEvents.remove(worldName);
        eventEndTimes.remove(worldName);
        BukkitTask task = eventTasks.remove(worldName);
        if (task != null) {
            task.cancel();
        }
        plugin.getEventBossBarManager().removeWorldBossBar(worldName);
        for (Player player : world.getPlayers()) {
            plugin.getScoreboardManager().removePlayerScoreboard(player);
        }
        return true;
    }

    public boolean startWorldwideEvent(String eventName) {
        if (isWorldwideEventActive()) {
            return false;
        }
        activeWorldwideEvent = eventName;
        long duration = plugin.getConfigManager().getWorldwideEvent().getDuration();
        long endTime = System.currentTimeMillis() + (duration * 1000);
        eventEndTimes.put("worldwide", endTime);

        plugin.getEventBossBarManager().createWorldwideBossBar(eventName, duration);
        plugin.getScoreboardManager().updateGlobalScoreboard();

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long remainingTime = (eventEndTimes.get("worldwide") - System.currentTimeMillis()) / 1000;
            if (remainingTime <= 0) {
                stopWorldwideEvent(eventName);
            } else {
                plugin.getEventBossBarManager().updateWorldwideBossBar(remainingTime);
            }
        }, 20L, 20L); // Update every second

        eventTasks.put("worldwide", task);
        return true;
    }

    public boolean stopWorldwideEvent(String eventName) {
        if (!isWorldwideEventActive() || !activeWorldwideEvent.equals(eventName)) {
            return false;
        }
        activeWorldwideEvent = null;
        eventEndTimes.remove("worldwide");
        BukkitTask task = eventTasks.remove("worldwide");
        if (task != null) {
            task.cancel();
        }
        plugin.getEventBossBarManager().removeWorldwideBossBar();
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getScoreboardManager().removePlayerScoreboard(player);
        }
        return true;
    }

    public boolean isEventActive(World world) {
        return activeWorldEvents.containsKey(world.getName());
    }

    public boolean isWorldwideEventActive() {
        return activeWorldwideEvent != null;
    }

    public String getEventName(String worldName) {
        return activeWorldEvents.get(worldName);
    }

    public String getWorldwideEventName() {
        return activeWorldwideEvent;
    }

    public long getRemainingTime(String worldName) {
        Long endTime = eventEndTimes.get(worldName);
        return endTime != null ? Math.max(0, (endTime - System.currentTimeMillis()) / 1000) : 0;
    }
}
