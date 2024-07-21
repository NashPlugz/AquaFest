package me.nashplugz.aquaf;

import org.bukkit.configuration.ConfigurationSection;

public class EventConfig {
    private String name;
    private long duration;
    private long fishShortageTime;
    private double fishShortageDistance;

    public EventConfig(String name, ConfigurationSection config) {
        this.name = name;
        this.duration = config.getLong("duration");
        this.fishShortageTime = config.getLong("fish_shortage_time");
        this.fishShortageDistance = config.getDouble("fish_shortage_distance");
    }

    // Add getters for all fields
    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public long getFishShortageTime() {
        return fishShortageTime;
    }

    public double getFishShortageDistance() {
        return fishShortageDistance;
    }
}
