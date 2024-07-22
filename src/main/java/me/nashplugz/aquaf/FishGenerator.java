package me.nashplugz.aquaf;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FishGenerator {
    private final List<Fish> fishList;
    private final Random random;

    public FishGenerator(FileConfiguration config) {
        this.fishList = new ArrayList<>();
        this.random = new Random();
        loadFishFromConfig(config);
    }

    private void loadFishFromConfig(FileConfiguration config) {
        ConfigurationSection fishSection = config.getConfigurationSection("fish");
        if (fishSection != null) {
            for (String fishType : fishSection.getKeys(false)) {
                ConfigurationSection fishTypeConfig = fishSection.getConfigurationSection(fishType);
                if (fishTypeConfig != null) {
                    String baseName = fishTypeConfig.getString("name", fishType);
                    Material material = Material.valueOf(fishTypeConfig.getString("material", "COD"));

                    for (Fish.Tier tier : Fish.Tier.values()) {
                        String tierName = tier.name().toLowerCase();
                        ConfigurationSection tierConfig = fishTypeConfig.getConfigurationSection(tierName);
                        if (tierConfig != null) {
                            String name = tierConfig.getString("name", baseName + " (" + tier.name() + ")");
                            double minValue = tierConfig.getDouble("min_value", 1.0);
                            double maxValue = tierConfig.getDouble("max_value", 10.0);
                            double chance = tierConfig.getDouble("chance", 1.0);

                            Fish fish = new Fish(name, minValue, maxValue, material, tier);
                            for (int i = 0; i < chance * 100; i++) {
                                fishList.add(fish);
                            }
                        }
                    }
                }
            }
        }
    }

    public Fish generateRandomFish() {
        if (fishList.isEmpty()) {
            return new Fish("Common Fish", 1.0, 5.0, Material.COD, Fish.Tier.COMMON);
        }
        return fishList.get(random.nextInt(fishList.size()));
    }

    public Fish getFishByName(String name) {
        for (Fish fish : fishList) {
            if (fish.getName().equalsIgnoreCase(name)) {
                return fish;
            }
        }
        return null;
    }

    public Fish getFishByNameAndTier(String name, Fish.Tier tier) {
        for (Fish fish : fishList) {
            if (fish.getName().equalsIgnoreCase(name) && fish.getTier() == tier) {
                return fish;
            }
        }
        return null;
    }

    public List<Fish> getAllFish() {
        return new ArrayList<>(fishList);
    }
}
