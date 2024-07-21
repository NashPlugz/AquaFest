package me.nashplugz.aquaf;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private AquaFest plugin;
    private FileConfiguration config;
    private Map<String, EventConfig> worldEvents;
    private EventConfig worldwideEvent;

    public ConfigManager(AquaFest plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.worldEvents = new HashMap<>();
        loadDefaultConfig();
    }

    private void loadDefaultConfig() {
        // Default config options
        config.addDefault("fish_shortage_time", 300);
        config.addDefault("fish_shortage_distance", 10.0);

        // Default world event
        config.addDefault("events.default.name", "DefaultEvent");
        config.addDefault("events.default.duration", 3600);
        config.addDefault("events.default.fish_shortage_time", 300);
        config.addDefault("events.default.fish_shortage_distance", 10);

        // Worldwide event
        config.addDefault("events.worldwide.name", "WorldwideEvent");
        config.addDefault("events.worldwide.duration", 7200);
        config.addDefault("events.worldwide.fish_shortage_time", 600);
        config.addDefault("events.worldwide.fish_shortage_distance", 20);

        config.options().copyDefaults(true);
        plugin.saveConfig();

        // Load world events
        loadWorldEvents();
        loadWorldwideEvent();
    }

    private void loadWorldEvents() {
        ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
        if (worldsSection != null) {
            for (String worldName : worldsSection.getKeys(false)) {
                String eventName = worldsSection.getString(worldName + ".event", "default");
                EventConfig eventConfig = loadEventConfig(eventName);
                if (eventConfig != null) {
                    worldEvents.put(worldName, eventConfig);
                }
            }
        }
    }

    private void loadWorldwideEvent() {
        worldwideEvent = loadEventConfig("worldwide");
    }

    public EventConfig getWorldEvent(String worldName) {
        return worldEvents.getOrDefault(worldName, loadEventConfig("default"));
    }

    public EventConfig getWorldwideEvent() {
        return worldwideEvent;
    }

    private EventConfig loadEventConfig(String eventName) {
        ConfigurationSection eventSection = config.getConfigurationSection("events." + eventName);
        if (eventSection == null) {
            plugin.getLogger().warning("No configuration found for event: " + eventName);
            return null;
        }
        return new EventConfig(eventName, eventSection);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // Add other methods as needed
}
