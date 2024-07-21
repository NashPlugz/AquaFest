package me.nashplugz.aquaf.events;

import me.nashplugz.aquaf.AquaFest;
import me.nashplugz.aquaf.EventConfig;
import me.nashplugz.aquaf.Fish;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishingEventManager {
    private AquaFest plugin;
    private Map<String, ActiveEvent> activeEvents;
    private ActiveEvent worldwideEvent;

    public FishingEventManager(AquaFest plugin) {
        this.plugin = plugin;
        this.activeEvents = new HashMap<>();
    }

    public boolean startEvent(String eventName, World world) {
        if (worldwideEvent != null) {
            return false;
        }
        if (activeEvents.containsKey(world.getName())) {
            return false;
        }
        EventConfig config = plugin.getConfigManager().getWorldEvent(world.getName());
        if (config == null) {
            plugin.getLogger().warning("No event configuration found for world: " + world.getName());
            return false;
        }
        ActiveEvent event = new ActiveEvent(eventName, config, world);
        activeEvents.put(world.getName(), event);
        plugin.getEventBossBarManager().createWorldBossBar(world.getName(), eventName, config.getDuration());
        startEventTimer(event);
        return true;
    }

    public boolean startWorldwideEvent(String eventName) {
        if (worldwideEvent != null || !activeEvents.isEmpty()) {
            return false;
        }
        EventConfig config = plugin.getConfigManager().getWorldwideEvent();
        if (config == null) {
            plugin.getLogger().warning("No configuration found for worldwide event");
            return false;
        }
        worldwideEvent = new ActiveEvent(eventName, config, null);
        plugin.getEventBossBarManager().createWorldwideBossBar(eventName, config.getDuration());
        startEventTimer(worldwideEvent);
        return true;
    }

    private void startEventTimer(ActiveEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.update()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Update every second
    }

    public boolean stopEvent(String eventName, World world) {
        ActiveEvent event = activeEvents.remove(world.getName());
        if (event != null && event.getName().equals(eventName)) {
            plugin.getEventBossBarManager().removeWorldBossBar(world.getName());
            // Handle event end (e.g., announce winners)
            return true;
        }
        return false;
    }

    public boolean stopWorldwideEvent(String eventName) {
        if (worldwideEvent != null && worldwideEvent.getName().equals(eventName)) {
            plugin.getEventBossBarManager().removeWorldwideBossBar();
            worldwideEvent = null;
            // Handle worldwide event end (e.g., announce global winners)
            return true;
        }
        return false;
    }

    public boolean isEventActive(World world) {
        return worldwideEvent != null || activeEvents.containsKey(world.getName());
    }

    public void recordCatch(Player player, Fish fish) {
        World world = player.getWorld();
        ActiveEvent event = worldwideEvent != null ? worldwideEvent : activeEvents.get(world.getName());
        if (event != null) {
            event.recordCatch(player, fish);
        }
    }

    public String getEventName(String worldName) {
        ActiveEvent event = activeEvents.get(worldName);
        return event != null ? event.getName() : null;
    }

    public String getWorldwideEventName() {
        return worldwideEvent != null ? worldwideEvent.getName() : null;
    }

    private class ActiveEvent {
        private String name;
        private EventConfig config;
        private World world;
        private Map<UUID, PlayerEventData> playerData;
        private long startTime;
        private long endTime;
        private long remainingTime;

        public ActiveEvent(String name, EventConfig config, World world) {
            this.name = name;
            this.config = config;
            this.world = world;
            this.playerData = new HashMap<>();
            this.startTime = System.currentTimeMillis();
            this.endTime = startTime + (config.getDuration() * 1000);
            this.remainingTime = config.getDuration();
        }

        public String getName() {
            return name;
        }

        public boolean update() {
            remainingTime = Math.max(0, (endTime - System.currentTimeMillis()) / 1000);
            if (world != null) {
                plugin.getEventBossBarManager().updateBossBar(world.getName(), remainingTime);
            } else {
                plugin.getEventBossBarManager().updateWorldwideBossBar(remainingTime);
            }

            plugin.getScoreboardManager().updateScoreboard();

            if (remainingTime <= 0) {
                if (world != null) {
                    stopEvent(name, world);
                } else {
                    stopWorldwideEvent(name);
                }
                return true; // Event has ended
            }
            return false; // Event is still ongoing
        }

        public void recordCatch(Player player, Fish fish) {
            UUID playerId = player.getUniqueId();
            PlayerEventData data = playerData.computeIfAbsent(playerId, k -> new PlayerEventData());
            data.addFish(fish);

            // Update leaderboard
            plugin.getLeaderboardManager().addScore(player, (int) fish.getValue(), world == null ? "worldwide" : world.getName());
        }
    }

    private class PlayerEventData {
        private Map<String, Integer> fishCaught;

        public PlayerEventData() {
            fishCaught = new HashMap<>();
        }

        public void addFish(Fish fish) {
            fishCaught.put(fish.getName(), fishCaught.getOrDefault(fish.getName(), 0) + 1);
        }

        public Map<String, Integer> getFishCaught() {
            return fishCaught;
        }
    }
}
