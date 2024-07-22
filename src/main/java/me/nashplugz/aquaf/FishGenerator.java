package me.nashplugz.aquaf;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FishGenerator {
    private Map<String, Fish> fishTypes;
    private Random random;

    public FishGenerator(FileConfiguration config) {
        fishTypes = new HashMap<>();
        random = new Random();
        loadFishTypes(config);
    }

    private void loadFishTypes(FileConfiguration config) {
        for (String fishName : config.getConfigurationSection("fish").getKeys(false)) {
            double minValue = config.getDouble("fish." + fishName + ".min_value");
            double maxValue = config.getDouble("fish." + fishName + ".max_value");
            String materialName = config.getString("fish." + fishName + ".material", "COD");
            String tierName = config.getString("fish." + fishName + ".tier", "COMMON");
            Material material = Material.valueOf(materialName.toUpperCase());
            Fish.Tier tier = Fish.Tier.valueOf(tierName.toUpperCase());
            fishTypes.put(fishName, new Fish(fishName, minValue, maxValue, material, tier));
        }
    }

    public Fish generateRandomFish() {
        String[] fishNames = fishTypes.keySet().toArray(new String[0]);
        String randomFishName = fishNames[random.nextInt(fishNames.length)];
        Fish fish = fishTypes.get(randomFishName);
        fish.regenerateValue(); // Generate a new random value for this catch
        return fish;
    }

    public Fish getFishByName(String name) {
        Fish fish = fishTypes.get(name);
        if (fish != null) {
            fish.regenerateValue(); // Generate a new random value when retrieved
        }
        return fish;
    }
}