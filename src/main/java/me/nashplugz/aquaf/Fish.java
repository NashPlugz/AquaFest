package me.nashplugz.aquaf;

import org.bukkit.Material;

public class Fish {
    public enum Tier {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    private String name;
    private double minValue;
    private double maxValue;
    private Material material;
    private double actualValue;
    private Tier tier;

    public Fish(String name, double minValue, double maxValue, Material material, Tier tier) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.material = material;
        this.tier = tier;
        this.actualValue = generateRandomValue();
    }

    private double generateRandomValue() {
        return minValue + Math.random() * (maxValue - minValue);
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return actualValue;
    }

    public Material getMaterial() {
        return material;
    }

    public void regenerateValue() {
        this.actualValue = generateRandomValue();
    }

    public Tier getTier() {
        return tier;
    }
}