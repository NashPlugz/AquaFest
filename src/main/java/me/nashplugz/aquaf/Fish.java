package me.nashplugz.aquaf;

import org.bukkit.Material;

public class Fish {
    private final String name;
    private final double value;
    private final Material material;
    private final Tier tier;

    public Fish(String name, double minValue, double maxValue, Material material, Tier tier) {
        this.name = name;
        this.value = minValue + Math.random() * (maxValue - minValue);
        this.material = material;
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public Material getMaterial() {
        return material;
    }

    public Tier getTier() {
        return tier;
    }

    public enum Tier {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }
}
